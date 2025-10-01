package com.akshay.ecommerce.controllers;
import com.akshay.ecommerce.dto.*;
import com.akshay.ecommerce.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController
@RequestMapping("/public/customers")
//@RequiredArgsConstructor
public class CustomerController {
    @Autowired
    private CustomerService service;
    @Autowired
    private MessageSource messageSource;

    @PostMapping("/register")
    public ResponseEntity<CustomerRegisterResponseDto> registerCustomer(@Valid @RequestBody CustomerRegisterDto dto) {
        UUID id = service.register(dto);
        CustomerRegisterResponseDto response = new CustomerRegisterResponseDto("Success", id);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/activateCustomer")
    public ResponseEntity<ActivationResponseDto> activateCustomer( @Valid @RequestBody ActivationRequestDto req) {
        if (req == null || req.token() == null || req.token().isBlank()) {
            throw new IllegalArgumentException("Activation token is mandatory");
        }
        UUID id = service.activateAndHandlingExpiry(req.token());
//        UUID id = service.activateWithToken(req.token());
        return ResponseEntity.ok(new ActivationResponseDto("your account activated", id));
    }
    @PostMapping("/resendaActivationEmail")
    public ResponseEntity<String> resendActivation(@Valid @RequestBody ResendActivationRequestDto req) {
        service.resendActivationLink(req.getEmail());
        return ResponseEntity.ok("Check your mail, activation link sent");
    }
}



