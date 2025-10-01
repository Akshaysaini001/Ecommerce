package com.akshay.ecommerce.controllers;
import com.akshay.ecommerce.service.S3ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Slf4j
public class ImageUploadController {
    private final S3ImageService s3ImageService;
    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<ImageUploadResponseDto> uploadImage(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        log.info("Upload request received for file: {}", file.getOriginalFilename());
        log.info("User authorities: {}", authentication.getAuthorities());
        try {
            s3ImageService.validateImage(file);
            String imageUrl = s3ImageService.uploadProductVariationImage(file);
            log.info("Successfully uploaded file: {}", file.getOriginalFilename());
            return ResponseEntity.ok(new ImageUploadResponseDto(
                    UUID.randomUUID().toString(),
                    imageUrl,
                    "Image uploaded successfully to S3"
            ));
        } catch (IOException e) {
            log.error("IO error during upload: ", e);
            return ResponseEntity.internalServerError().body(new ImageUploadResponseDto(
                    null, null, "Upload failed due to IO error: " + e.getMessage()
            ));
        } catch (IllegalArgumentException e) {
            log.error("Validation error: ", e);
            return ResponseEntity.badRequest().body(new ImageUploadResponseDto(
                    null, null, "Validation failed: " + e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Unexpected error during upload: ", e);
            return ResponseEntity.internalServerError().body(new ImageUploadResponseDto(
                    null, null, "Unexpected error: " + e.getMessage()
            ));
        }
    }
    public record ImageUploadResponseDto(
            String imageId,
            String imageUrl,
            String message
    ) {}
}
