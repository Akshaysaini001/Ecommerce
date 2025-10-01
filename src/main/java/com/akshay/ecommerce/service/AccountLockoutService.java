package com.akshay.ecommerce.service;
import com.akshay.ecommerce.repository.UserRepository;
import com.akshay.ecommerce.entity.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
@Service
@AllArgsConstructor
@Slf4j
public class AccountLockoutService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    @Transactional
    public void recordFailedAttempt(String email) {
        Optional<User> userfind = userRepository.findByEmail(email);
        if (userfind.isPresent()) {
            User user = userfind.get();
            int newAttempts = user.getInvalidAttemptCount() + 1;
            user.setInvalidAttemptCount(newAttempts);
            if (newAttempts > 3) {
                user.setLocked(true);
                user.setLockedAt(LocalDateTime.now());
                log.info("Account locked");
                emailService.sendPlainEmail(
                        user.getEmail(),"Your account has been locked","Your account has been locked due to 3 plusinvalid login attempts."
                );
            }
            userRepository.save(user);
        }
    }
    public boolean isAccountLocked(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return false;
        }
        return user.isLocked();
    }
    @Transactional
    public void resetFailedAttempts(String email) {
        userRepository.findByEmail(email).ifPresent(u -> {
            u.setInvalidAttemptCount(0);
            userRepository.save(u);
            log.info("failed attempts reset for {}", email);
        });
    }
}




//    @Transactional
//    public void unlockAccount(String email) {
//        userRepository.findByEmail(email).ifPresent(user -> {
//            user.setLocked(false);
//            user.setInvalidAttemptCount(0);
//            userRepository.save(user);
//            log.info("Account manually unlocked for email: {}", email);
//        });
//    }

