package com.github.ChelovekVreditel.chinese_cars.services;

import com.github.ChelovekVreditel.chinese_cars.utils.AudiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarService {

    private final AudiParser audiParser = new AudiParser();

    private void updateCarsCatalogs() {

    }
}
