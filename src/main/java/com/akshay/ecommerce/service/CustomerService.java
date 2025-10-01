package com.akshay.ecommerce.service;
import com.akshay.ecommerce.constants.Authority;
import com.akshay.ecommerce.repository.RoleRepository;
import com.akshay.ecommerce.entity.ActivationToken;
import com.akshay.ecommerce.entity.Customer;
import com.akshay.ecommerce.repository.ActivationTokenRepository;
import com.akshay.ecommerce.repository.CustomerRepository;
import com.akshay.ecommerce.dto.CustomerRegisterDto;
import com.akshay.ecommerce.entity.Role;
import com.akshay.ecommerce.exceptions.DuplicateException;
import com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass.InvalidTokenException;
import com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass.NotFoundException;
import com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass.TokenAlreadyUsedException;
import com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass.TokenExpiredException;
import com.akshay.ecommerce.exceptions.PasswordMismatchException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final GenerateTokenService activationTokenService;
    private final EmailService emailService;
    private final ActivationTokenRepository activationTokenRepository;
    private final MessageService messageService;
    private final MessageSource messageSource;
    @Value("${activation.token.expiry-minutes}")
    private long expiryMinutes;
    @Transactional
    public UUID register(CustomerRegisterDto dto) {
        Locale locale = LocaleContextHolder.getLocale();
        String normalizedEmail = dto.getEmail().trim().toLowerCase();
        if (repo.existsByEmail(normalizedEmail)) {
            throw new DuplicateException(messageService.getMessage("customer.register.email.exists"));
        }
        if (repo.existsByContact(Long.valueOf(dto.getContact().trim()))) throw new DuplicateException(messageService.getMessage("customer.register.contact.exists"));

        if (dto.getPassword() == null) {
            throw new PasswordMismatchException(messageService.getMessage("password.notblank"));
        }
        if (dto.getConfirmPassword() == null) {
            throw new PasswordMismatchException(messageService.getMessage("confirmpassword.notblank"));
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new PasswordMismatchException(messageService.getMessage("password.mismatch"));
        }
        Customer c = new Customer();
        c.setEmail(normalizedEmail);
        c.setContact(Long.valueOf(dto.getContact().trim()));
        c.setFirstName(dto.getFirstName().trim());
        c.setLastName(dto.getLastName().trim());
        c.setPassword(passwordEncoder.encode(dto.getPassword()));
        c.setActive(false);
        Role customerRole = roleRepository.findByAuthority(Authority.CUSTOMER)
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setAuthority(Authority.CUSTOMER);
                    return roleRepository.save(r);
                });
        c.addRole(customerRole);
        c = repo.save(c);
        ActivationToken token = activationTokenService.createForCustomer(c.getId(), Duration.ofMinutes(expiryMinutes));
        log.info("Activation Token for customer " + c.getId() + ": " + token.getToken());
        try {
            emailService.sendActivationEmail(c.getEmail(), token.getToken());
        } catch (Exception e) {
            log.warn("Activation email send failed for {}: {}", c.getEmail(), e.getMessage(), e);
        }
        return c.getId();
    }

    @Transactional
        public UUID activateAndHandlingExpiry(String tokenValue) {
            ActivationToken token = activationTokenRepository.findByToken(tokenValue).orElseThrow(() -> new InvalidTokenException("Invalid token"));
            if (token.getUsedAt() != null) {
                Customer already = repo.findById(token.getCustomerId()).orElse(null);
                if (already != null && already.isActive()) {
                    throw new TokenAlreadyUsedException("Token already used. User is already active");
                } else {
                    throw new TokenAlreadyUsedException("Token already used");
                }
            }
            Instant now = Instant.now();
            if (now.isAfter(token.getExpiresAt())) {
                ActivationToken newToken = activationTokenService.reissueForExpired(tokenValue, Duration.ofMinutes(expiryMinutes));
                Customer customer = repo.findById(newToken.getCustomerId())
                        .orElseThrow(() -> new NotFoundException("Customer not found for token"));
                try {
                    emailService.sendActivationEmail(customer.getEmail(), newToken.getToken());
                } catch (Exception e) {
                }
                throw new TokenExpiredException("Activation link expired. A new link has been sent to your email");
            }
            token.setUsedAt(now);
            activationTokenRepository.save(token);
            Customer customer = repo.findById(token.getCustomerId())
                    .orElseThrow(() -> new InvalidTokenException("Customer not found"));
            if (!customer.isActive()) {
                customer.setActive(true);
                repo.save(customer);
            }
        activationTokenRepository.deleteByToken(tokenValue);
            return customer.getId();
        }
        // i made changes in this if user try to re hit after activate then delete token and show token is invalid
    @Transactional
    public void resendActivationLink(String email) {
        Customer customer = repo.findByEmail(email).orElse(null);
        if (customer == null) {
            throw new NotFoundException("Customer not found, You entered wrong Email"); // 404
        }
        if (customer.isActive()) {
            throw new DuplicateException("Account already active"); // 409
        }
        activationTokenRepository.deleteUnusedTokenByCustomerId(customer.getId());
        ActivationToken token = activationTokenService.createForCustomer(customer.getId(), Duration.ofMinutes(expiryMinutes));
        emailService.sendActivationEmail(customer.getEmail(), token.getToken());
        log.info("Activation token for customer " + token.getToken());
        log.info("Activation email for customer " + customer.getEmail());
    }
}
//deactive user only should get the email, email should be valid check at dto,controller

//    @Transactional
//    public UUID activateWithToken(String token) {
//        UUID customerId = activationTokenService.validate(token);
//        Customer c = repo.findById(customerId).orElse(null);
//        if (c == null) {
//            throw new IllegalArgumentException("Customer not found");
//        }
//        if (!c.isActive()) {
//            c.setActive(true);
//            repo.save(c);
//        }
//        activationTokenRepository.deleteByToken(token);
//        return c.getId();
//    }

//    @Transactional
//    public UUID activateAndHandlingExpiry(String token) {
//        ActivationToken t = activationTokenRepository.findByToken(token).orElse(null);
//        if (t == null) {
//            throw new IllegalArgumentException("invalid token");
//        }
//        if (t.getUsedAt() != null) {
//            throw new IllegalStateException("this token is already been used");
//        }
//        if (Instant.now().isAfter(t.getExpiresAt())) {
//            ActivationToken newTok = activationTokenService.reissueForExpired(token, Duration.ofMinutes(15));
//            Customer customer = repo.findById(newTok.getCustomerId())
//                    .orElseThrow(() -> new IllegalStateException("Customer not found for token"));
//
//            emailService.sendActivationEmail(customer.getEmail(), newTok.getToken());
//            throw new IllegalStateException("this token is expired");
//        }
//        t.setUsedAt(Instant.now());
//        //mark customer activated
//        Customer c = repo.findById(t.getCustomerId())
//                .orElseThrow(() -> new IllegalArgumentException("customer not found"));
//        if (!c.isActive()) {
//            c.setActive(true);
//            repo.save(c);
//        }
//        activationTokenRepository.deleteByToken(token);
//        return c.getId();
//    }




