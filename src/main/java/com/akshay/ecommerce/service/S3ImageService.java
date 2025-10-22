package com.akshay.ecommerce.service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class S3ImageService {
    private final S3Client s3Client;
    @Value("${aws.s3.bucketName}")
    private String bucketName;
    @Value("${aws.region}")
    private String region;
    public String uploadProductVariationImage(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();
        s3Client.putObject(putObjectRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName, region, fileName);
    }
    public void validateImage(MultipartFile file) {
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File size too large (max 5MB)");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Invalid file type. Only images allowed");
        }
    }
public String convertToFullS3Url(String imageName) {
    if (imageName == null || imageName.isEmpty()) {
        return String.format("https://%s.s3.%s.amazonaws.com/default-product.png", bucketName, region);
    }
    if (imageName.startsWith("http://") || imageName.startsWith("https://")) {
        return imageName;
    }
    String cleanImageName = imageName;
    if (imageName.startsWith("profile/")) {
        cleanImageName = imageName.substring("profile/".length());
    }
    return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, cleanImageName);}
}
