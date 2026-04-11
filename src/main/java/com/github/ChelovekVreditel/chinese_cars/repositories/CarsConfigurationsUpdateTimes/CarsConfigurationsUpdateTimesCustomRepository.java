package com.github.ChelovekVreditel.chinese_cars.repositories.CarsConfigurationsUpdateTimes;

import java.util.List;

public interface CarsConfigurationsUpdateTimesCustomRepository {
    
    public void batchUpsert(List<Long> ids);
}
