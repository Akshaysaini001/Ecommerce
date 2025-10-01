package com.akshay.ecommerce.service;
import com.akshay.ecommerce.entity.User;
import com.akshay.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class AccountUnlockScheduler {

    @Autowired
    private UserRepository userRepository;
    @Value("${account.unlock.duration.minutes}")
    private long unlockDurationMinutes;

    @Scheduled(fixedRate = 60000)
    public void unlockAccounts() {
        List<User> lockedUsers = userRepository.findByIsLocked(true);
        for (User user : lockedUsers) {
            if (user.getLockedAt() != null &&
                    Duration.between(user.getLockedAt(), LocalDateTime.now()).toMinutes() >= unlockDurationMinutes) {
                user.setLocked(false);
                user.setLockedAt(null);
                userRepository.save(user);
            }
        }
    }
}
