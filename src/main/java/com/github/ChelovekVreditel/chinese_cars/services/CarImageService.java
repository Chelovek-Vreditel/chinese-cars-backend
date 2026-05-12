package com.github.ChelovekVreditel.chinese_cars.services;

import com.github.ChelovekVreditel.chinese_cars.dtos.FetchedImage;
import com.github.ChelovekVreditel.chinese_cars.exceptions.EntityNotFoundException;
import com.github.ChelovekVreditel.chinese_cars.models.CarImage;
import com.github.ChelovekVreditel.chinese_cars.repositories.CarImageRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarImageService {

    @Autowired
    private CarImageRepository carImageRepository;

    @Autowired
    private ImageStorageService imageStorageService;

    public FetchedImage getCarImageInBytes(Long carId) {
        CarImage carImageMeta = carImageRepository.findByCarId(carId)
            .orElseThrow(() -> new EntityNotFoundException("Нет изображения для данного авто."));
        String storageKey = carImageMeta.getStorageKey();
        String contentType = carImageMeta.getContentType();

        byte[] imageBytes = imageStorageService.download(storageKey);
        return new FetchedImage(imageBytes, contentType);
    }
}
