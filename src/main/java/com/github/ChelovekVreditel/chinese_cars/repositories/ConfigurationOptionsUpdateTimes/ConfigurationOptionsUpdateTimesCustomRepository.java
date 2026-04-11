package com.github.ChelovekVreditel.chinese_cars.repositories.ConfigurationOptionsUpdateTimes;

import java.util.List;

public interface ConfigurationOptionsUpdateTimesCustomRepository {

    public void batchUpsert(List<Long> ids);
}
