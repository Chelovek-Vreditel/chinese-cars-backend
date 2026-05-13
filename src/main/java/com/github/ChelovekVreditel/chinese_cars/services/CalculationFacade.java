package com.github.ChelovekVreditel.chinese_cars.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import com.github.ChelovekVreditel.chinese_cars.dtos.CalculationRequest;
import com.github.ChelovekVreditel.chinese_cars.dtos.CalculationResponse;
import com.github.ChelovekVreditel.chinese_cars.dtos.ImportCostsDto;
import com.github.ChelovekVreditel.chinese_cars.dtos.SelectedOptionDto;
import com.github.ChelovekVreditel.chinese_cars.enums.EngineType;
import com.github.ChelovekVreditel.chinese_cars.exceptions.EntityNotFoundException;
import com.github.ChelovekVreditel.chinese_cars.models.CarConfiguration;
import com.github.ChelovekVreditel.chinese_cars.models.ConfigurationTechnicalSpecs;
import com.github.ChelovekVreditel.chinese_cars.repositories.ConfigurationTechnicalSpecsRepository;
import com.github.ChelovekVreditel.chinese_cars.repositories.ImportSettingRepository;
import com.github.ChelovekVreditel.chinese_cars.repositories.CarConfigurations.CarConfigurationRepository;
import com.github.ChelovekVreditel.chinese_cars.repositories.ConfigurationOptions.ConfigurationOptionRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalculationFacade {

    private final ConfigurationTechnicalSpecsRepository technicalSpecsRepository;
    private final CarConfigurationRepository configurationsRepository;
    private final ConfigurationOptionRepository optionsRepository;
    private final ImportSettingRepository importSettingRepository;

    private final CustomsDutyService customsDutyService;
    private final CustomsFeeService customsFeeService;
    private final RecyclingFeeService recyclingFeeService;
    private final ExciseService exciseService;

    private final CurrencyConverter currencyConverter;

    public CalculationResponse calculate(CalculationRequest request) {
        // 1. Загружаем технические характеристики конфигурации
        ConfigurationTechnicalSpecs specs = technicalSpecsRepository
                .findById(request.getConfigurationId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Технические характеристики конфигурации не найдены: " + request.getConfigurationId()
                ));

        // 2. Базовая цена конфигурации в рублях
        CarConfiguration configuration = configurationsRepository
                .findById(request.getConfigurationId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Конфигурация не найдена: " + request.getConfigurationId()
                ));

        BigDecimal baseCarPriceRub = currencyConverter.convertCnyToRub(configuration.getBasePriceCny());

        // 3. Выбранные опции
        List<SelectedOptionDto> selectedOptions = new ArrayList<>();
        optionsRepository.findAllById(request.getOptionIds())
            .forEach(option -> {
                SelectedOptionDto dto = new SelectedOptionDto();
                dto.setId(option.getId());
                dto.setName(option.getName());
                dto.setPriceRub(currencyConverter.convertCnyToRub(option.getPriceCny()));
                selectedOptions.add(dto);
            });

        BigDecimal optionsTotalRub = selectedOptions.stream()
                .map(SelectedOptionDto::getPriceRub)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Общая стоисоть
        BigDecimal customsValueRub = baseCarPriceRub.add(optionsTotalRub);

        // 5. Расчёт платежей
        BigDecimal customsDutyRub  = customsDutyService.calculate(specs, customsValueRub);
        BigDecimal customsFeeRub   = customsFeeService.calculate(customsValueRub);
        BigDecimal recyclingFeeRub = recyclingFeeService.calculate(specs);

        // 6. Акциз и НДС — только для электромобилей
        BigDecimal exciseRub = null;
        BigDecimal vatRub    = null;

        if (specs.getEngineType() == EngineType.ELECTRIC) {
            exciseRub = exciseService.calculate(specs);

            BigDecimal vatRate = getSettingAsDecimal("vat_rate_percent");
            vatRub = customsValueRub
                    .add(customsDutyRub)
                    .add(exciseRub)
                    .multiply(vatRate)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        // 7. Итоговые издержки ввоза
        ImportCostsDto importCosts = buildImportCosts(
                customsDutyRub, customsFeeRub, recyclingFeeRub, exciseRub, vatRub
        );

        // 8. Итоговая стоимость
        BigDecimal totalCostRub = customsValueRub.add(importCosts.getTotalImportCostRub());

        CalculationResponse response = new CalculationResponse();
        response.setBaseCarPriceRub(baseCarPriceRub);
        response.setSelectedOptions(selectedOptions);
        response.setImportCosts(importCosts);
        response.setTotalCostRub(totalCostRub);

        log.debug("response: {}", response);
        return response;
    }

    private ImportCostsDto buildImportCosts(BigDecimal customsDutyRub,
                                            BigDecimal customsFeeRub,
                                            BigDecimal recyclingFeeRub,
                                            BigDecimal exciseRub,
                                            BigDecimal vatRub) {
        BigDecimal total = Stream.of(customsDutyRub, customsFeeRub, recyclingFeeRub, exciseRub, vatRub)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ImportCostsDto dto = new ImportCostsDto();
        dto.setCustomsDutyRub(customsDutyRub);
        dto.setCustomsFeeRub(customsFeeRub);
        dto.setRecyclingFeeRub(recyclingFeeRub);
        dto.setExciseRub(exciseRub);
        dto.setVatRub(vatRub);
        dto.setTotalImportCostRub(total);
        return dto;
    }

    private BigDecimal getSettingAsDecimal(String key) {
        return importSettingRepository.getByKey(key)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Настройка не найдена: " + key
                ));
    }
}
