package com.akshay.ecommerce.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
//@RestController
//@RequestMapping
//public class ImageController {
//
//    @Value("${app.image.profile.path:profile/}")
//    private String profileImagePath;
//
//    @GetMapping("/{fileName:.+}")
//    @PreAuthorize("hasRole('SELLER')")
//    public ResponseEntity<Resource> getImage(@PathVariable String fileName) throws IOException {
//        Path filePath = Paths.get(profileImagePath).resolve(fileName).normalize();
//
//        if (!Files.exists(filePath)) {
//            return ResponseEntity.notFound().build();
//        }
//
//        Resource resource = new UrlResource(filePath.toUri());
//
//        String contentType = Files.probeContentType(filePath);
//        if (contentType == null) {
//            contentType = "application/octet-stream";
//        }
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(contentType))
//                .body(resource);
//    }
//}

//
//@RestController
//@RequestMapping
//public class ImageController {
//    @Value("${app.image.profile.path:profile/}")
//    private String profileImagePath;
//
//    @GetMapping("/{fileName:.+}")
//    @PreAuthorize("hasRole('SELLER')")
//    public ResponseEntity<Resource> getImage(@PathVariable String fileName) throws IOException {
//        Path filePath = Paths.get(profileImagePath).resolve(fileName).normalize();
//
//        if (!Files.exists(filePath)) {
//            return ResponseEntity.notFound().build();
//        }
//
//        Resource resource = new UrlResource(filePath.toUri());
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.IMAGE_PNG) // tum chaho to detect kar sakte ho
//                .body(resource);
//    }
//}
