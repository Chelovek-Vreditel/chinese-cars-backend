package com.github.ChelovekVreditel.chinese_cars.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class ImageStorageService {

    private final S3Client s3Client;

    @Value("${rustfs.bucket}")
    private String bucket;

    public String upload(byte[] imageBytes, String contentType, long carId) {
        String key = "cars/%d/%s".formatted(carId, UUID.randomUUID());

        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build(),
            RequestBody.fromBytes(imageBytes)
        );

        return key;
    }

    public byte[] download(String storageKey) {
        ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(
            GetObjectRequest.builder()
                .bucket(bucket)
                .key(storageKey)
                .build()
        );
        return response.asByteArray();
    }

    public void delete(String storageKey) {
        s3Client.deleteObject(
            DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(storageKey)
                .build()
        );
    }
}
