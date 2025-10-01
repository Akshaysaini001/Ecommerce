package com.akshay.ecommerce.service;
import com.akshay.ecommerce.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
@Slf4j
@Service
@AllArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {
    private final UserRepository repo;
    @Override
    public UserDetails loadUserByUsername(String email) {
        var u = repo.findByEmailWithRoles(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for email " + email));
        List<SimpleGrantedAuthority> authorities = u.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getAuthority()))
                .toList();
        log.info("Authorities: {}", authorities);
        return org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())
                .password(u.getPassword())
                .authorities(authorities)
                .accountLocked(u.isLocked())
                .disabled(!u.isActive())
                .accountExpired(u.isExpired())
                .build();
    }
}
