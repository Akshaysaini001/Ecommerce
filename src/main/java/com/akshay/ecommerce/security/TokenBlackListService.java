package com.akshay.ecommerce.security;

import java.time.Duration;

public interface TokenBlackListService {
    void blacklist(String jti, Duration ttl);
    boolean isBlacklisted(String jti);
}
