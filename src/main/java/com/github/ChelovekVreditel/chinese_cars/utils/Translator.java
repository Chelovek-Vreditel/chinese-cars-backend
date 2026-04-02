package com.github.ChelovekVreditel.chinese_cars.utils;

import com.github.ChelovekVreditel.chinese_cars.dtos.ConfigurationDetails;
import com.github.ChelovekVreditel.chinese_cars.models.Car;
import com.github.ChelovekVreditel.chinese_cars.models.CarConfiguration;
import com.github.ChelovekVreditel.chinese_cars.models.ConfigurationOption;

public interface Translator {
    public void translateCar(Car car);
    public void translateCarConfiguration(CarConfiguration carConfiguration);
    public void translateConfigurationOption(ConfigurationOption configurationOption);
    public void translateConfigurationDetails(ConfigurationDetails configurationDetails);
}
