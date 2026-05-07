package com.github.ChelovekVreditel.chinese_cars.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import com.github.ChelovekVreditel.chinese_cars.enums.AgeCategory;
import com.github.ChelovekVreditel.chinese_cars.enums.EngineType;
import com.github.ChelovekVreditel.chinese_cars.exceptions.RateNotFoundException;
import com.github.ChelovekVreditel.chinese_cars.models.ConfigurationTechnicalSpecs;
import com.github.ChelovekVreditel.chinese_cars.models.RecyclingFeeRate;
import com.github.ChelovekVreditel.chinese_cars.repositories.RecyclingFeeRateRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecyclingFeeService {

    private final RecyclingFeeRateRepository recyclingFeeRateRepository;

    public BigDecimal calculate(ConfigurationTechnicalSpecs specs) {
        boolean isPreferential = isPreferentialApplicable(specs);
        AgeCategory ageCategory = resolveAgeCategory(specs.getManufactureYear());

        // Для электромобилей объём не учитывается — передаём 0
        int engineVolumeCc = specs.getEngineType() == EngineType.ELECTRIC
                ? 0
                : specs.getEngineVolumeCc();

        RecyclingFeeRate rate = recyclingFeeRateRepository.findRate(
                ageCategory.name(),
                isPreferential,
                engineVolumeCc,
                specs.getEnginePowerKw(),
                LocalDate.now()
        ).orElseThrow(() -> new RateNotFoundException("Ставка утилизационного сбора не найдена"));

        return rate.baseRateRub()
                .multiply(rate.coefficient())
                .setScale(2, RoundingMode.HALF_UP);
    }

    // Льгота: мощность ≤ 160 л.с. И объём ≤ 3000 куб.см (для электро — только мощность)
    // private boolean isPreferentialApplicable(ConfigurationTechnicalSpecs specs) {
    //     boolean powerOk = specs.getEnginePowerHp() <= 160;
    //
    //     if (specs.getEngineType() == EngineType.ELECTRIC) {
    //         return powerOk;
    //     }
    //     return powerOk && specs.getEngineVolumeCc() <= 3000;
    // }

    private static final BigDecimal PREFERENTIAL_POWER_LIMIT_KW = new BigDecimal("117.7");

    private boolean isPreferentialApplicable(ConfigurationTechnicalSpecs specs) {
        boolean powerOk = specs.getEnginePowerKw()
            .compareTo(PREFERENTIAL_POWER_LIMIT_KW) <= 0;

        if (specs.getEngineType() == EngineType.ELECTRIC) {
            return powerOk;
        }
        return powerOk && specs.getEngineVolumeCc() <= 3000;
    }

    // NEW < 3 лет, USED ≥ 3 лет
    private AgeCategory resolveAgeCategory(int manufactureYear) {
        int age = LocalDate.now().getYear() - manufactureYear;
        return age < 3 ? AgeCategory.NEW : AgeCategory.USED;
    }
}
