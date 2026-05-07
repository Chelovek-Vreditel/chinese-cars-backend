package com.github.ChelovekVreditel.chinese_cars.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import com.github.ChelovekVreditel.chinese_cars.enums.AgeCategory;
import com.github.ChelovekVreditel.chinese_cars.enums.EngineType;
import com.github.ChelovekVreditel.chinese_cars.exceptions.RateNotFoundException;
import com.github.ChelovekVreditel.chinese_cars.models.ConfigurationTechnicalSpecs;
import com.github.ChelovekVreditel.chinese_cars.models.CustomsDutyRate;
import com.github.ChelovekVreditel.chinese_cars.models.ElectricVehicleDutyRate;
import com.github.ChelovekVreditel.chinese_cars.repositories.CustomsDutyRateRepository;
import com.github.ChelovekVreditel.chinese_cars.repositories.ElectricVehicleDutyRateRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomsDutyService {

    private final CustomsDutyRateRepository customsDutyRateRepository;
    private final ElectricVehicleDutyRateRepository electricVehicleDutyRateRepository;
    private final CurrencyConverter currencyConverter;

    public BigDecimal calculate(ConfigurationTechnicalSpecs specs, BigDecimal customsValueRub) {
        LocalDate today = LocalDate.now();

        if (specs.getEngineType() == EngineType.ELECTRIC) {
            return calculateForElectric(customsValueRub, today);
        }
        return calculateForIce(specs, customsValueRub, today);
    }

    private BigDecimal calculateForElectric(BigDecimal customsValueRub, LocalDate date) {
        ElectricVehicleDutyRate rate = electricVehicleDutyRateRepository.findCurrentRate(date)
                .orElseThrow(() -> new RateNotFoundException("Ставка пошлины для электромобиля не найдена"));

        return customsValueRub
                .multiply(rate.ratePercent())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateForIce(ConfigurationTechnicalSpecs specs,
                                       BigDecimal customsValueRub,
                                       LocalDate date) {
        AgeCategory ageCategory = resolveAgeCategory(specs.getManufactureYear());

        CustomsDutyRate rate = customsDutyRateRepository.findRate(
                ageCategory.name(),
                specs.getEngineVolumeCc(),
                date
        ).orElseThrow(() -> new RateNotFoundException("Ставка единой таможенной пошлины не найдена"));

        // Ставка за куб.см всегда присутствует — применима для всех возрастных групп
        BigDecimal byVolume = BigDecimal.valueOf(specs.getEngineVolumeCc())
                .multiply(rate.rateEurPerCc())
                .multiply(currencyConverter.convertEurToRub(BigDecimal.ONE))
                .setScale(2, RoundingMode.HALF_UP);

        // Для новых авто берём max(% от стоимости, за куб.см)
        if (ageCategory == AgeCategory.NEW) {
            BigDecimal byPercent = customsValueRub
                    .multiply(rate.ratePercent())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            return byPercent.max(byVolume);
        }

        return byVolume;
    }

    // NEW < 3 лет, USED_3_5 = 3–5 лет, USED_5_PLUS > 5 лет
    private AgeCategory resolveAgeCategory(int manufactureYear) {
        int age = LocalDate.now().getYear() - manufactureYear;
        if (age < 3) return AgeCategory.NEW;
        if (age <= 5) return AgeCategory.USED_3_5;
        return AgeCategory.USED_5_PLUS;
    }
}
