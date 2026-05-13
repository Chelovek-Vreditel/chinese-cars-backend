package com.github.ChelovekVreditel.chinese_cars.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import com.github.ChelovekVreditel.chinese_cars.enums.AgeCategory;
import com.github.ChelovekVreditel.chinese_cars.enums.EngineType;
import com.github.ChelovekVreditel.chinese_cars.exceptions.RateNotFoundException;
import com.github.ChelovekVreditel.chinese_cars.models.ConfigurationTechnicalSpecs;
import com.github.ChelovekVreditel.chinese_cars.models.RecyclingFeeRate;
import com.github.ChelovekVreditel.chinese_cars.repositories.ImportSettingRepository;
import com.github.ChelovekVreditel.chinese_cars.repositories.RecyclingFeeRateRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecyclingFeeService {

    private final RecyclingFeeRateRepository recyclingFeeRateRepository;
    private final ImportSettingRepository importSettingRepository;

    public BigDecimal calculate(ConfigurationTechnicalSpecs specs) {
        BigDecimal baseRate = importSettingRepository.getByKey("recycling_base_rate")
            .orElseThrow(() -> new RateNotFoundException("Не найдена базовая ставка для утилизационного сбора."));
        Boolean moreThreeYears = isMoreThanThreeYears(specs.getManufactureYear());
        Boolean isElectrical;
        if (specs.getEngineType().equals(EngineType.ELECTRIC)) {
            isElectrical = true;
            BigDecimal powerLimit = importSettingRepository.getByKey("electric_preferential_engine_power")
                .orElseThrow(() -> new RateNotFoundException("Не найдена максимальная мощность электромотора для льготной ставки."));
            if (specs.getEnginePowerKw().compareTo(powerLimit) <= 0) {
                String key = moreThreeYears ? "electric_preferential_coef_more" : "electric_preferential_coef_less";
                BigDecimal coef = importSettingRepository.getByKey(key)
                    .orElseThrow(() -> new RateNotFoundException("Не найден коэффициент для льготного утильсбора электромобиля."));
                return baseRate.multiply(coef).setScale(2, RoundingMode.HALF_UP);
            }
        }
        else {
            isElectrical = false;
            BigDecimal powerLimit = importSettingRepository.getByKey("preferential_power_kw")
                .orElseThrow(() -> new RateNotFoundException("Не найдена максимальная мощность ДВС для льготной ставки."));
            Integer volumeLimit = importSettingRepository.getByKey("preferential_volume_cc")
                .orElseThrow(() -> new RateNotFoundException("Не найден максимальный объём ДВС для льготной ставки.")).intValue();
            if (specs.getEnginePowerKw().compareTo(powerLimit) <= 0 && specs.getEngineVolumeCc()<= volumeLimit) {
                String key = moreThreeYears ? "preferential_coef_more" : "preferential_coef_less";
                BigDecimal coef = importSettingRepository.getByKey(key)
                    .orElseThrow(() -> new RateNotFoundException("Не найден коэффициент для льготного утильсбора автомобиля."));
                return baseRate.multiply(coef).setScale(2, RoundingMode.HALF_UP);
            }
        }
        BigDecimal engineVolumeL = BigDecimal.valueOf(specs.getEngineVolumeCc())
            .divide(BigDecimal.valueOf(1000))
            .setScale(2, RoundingMode.HALF_UP);
        RecyclingFeeRate rate = recyclingFeeRateRepository.getRate(isElectrical, specs.getEnginePowerHp(), engineVolumeL)
            .orElseThrow(() -> new RateNotFoundException("Не найден коэффициент для расчёта утильсбора."));

        BigDecimal multiplier = moreThreeYears ? rate.coefMore() : rate.coefLess();
        return baseRate.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }

    private Boolean isMoreThanThreeYears(int age) {
        int pastAge = LocalDate.now().getYear() - age;
        return pastAge >= 3 ? true : false;
    }
}
