package com.akshay.ecommerce.config;
import com.akshay.ecommerce.constants.Authority;
import com.akshay.ecommerce.repository.RoleRepository;
import com.akshay.ecommerce.repository.UserRepository;
import com.akshay.ecommerce.entity.Role;
import com.akshay.ecommerce.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
@Slf4j
public class BootStrapAdmin implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    @Transactional
    public void run(String... args) {
        createAdminIfMissing();
    }
    private void createAdminIfMissing() {
        final String adminEmail = "editzakshay137@gmail.com";
        final String rawPassword = "Akshay@1234";
        Role adminRole = roleRepository.findByAuthority(Authority.ADMIN)
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setAuthority(Authority.ADMIN);
                    return roleRepository.save(r);
                });

        User user = userRepository.findByEmail(adminEmail).orElse(null);
        if (user == null) {
            User u = new User();
            u.setEmail(adminEmail);
            u.setFirstName("Akshay");
            u.setLastName("Admin");
            u.setPassword(passwordEncoder.encode(rawPassword));
            u.setActive(true);
            u.getRoles().add(adminRole);
            userRepository.save(u);
            log.info("created akshay admin : {}", adminEmail);
        }
    }
}

