package com.akshay.ecommerce.controllers;
import com.akshay.ecommerce.dto.SellerRegisterDto;

import java.util.List;
import java.util.UUID;

import com.akshay.ecommerce.dto.viewCategory.CategoryDetailDto;
import com.akshay.ecommerce.service.SellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/public/seller")
@RequiredArgsConstructor
public class SellerController {
    private final SellerService sellerService;

    @PostMapping("/register")
    public ResponseEntity<Map<String,Object>> registerSeller(@Valid @RequestBody SellerRegisterDto dto) {
        UUID id = sellerService.register(dto);
        return ResponseEntity.ok(Map.of("message","Success","id",id));
    }

}

