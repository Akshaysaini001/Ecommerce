package com.akshay.ecommerce.security;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
@Service
public class Caffeine implements TokenBlackListService {
    private final Cache<String, Boolean> cache;
    public Caffeine(){
        this.cache = com.github.benmanes.caffeine.cache.Caffeine.newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();
    }
    @Override
    public void blacklist(String jti, Duration ttl) {
        if (jti == null || jti.isBlank()) return;
        cache.put(jti, Boolean.TRUE);
    }
    @Override
    public boolean isBlacklisted(String jti) {
        return cache.getIfPresent(jti) != null;
    }

}


