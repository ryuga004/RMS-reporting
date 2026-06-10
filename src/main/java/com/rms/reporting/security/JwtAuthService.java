package com.rms.reporting.security;

import com.rms.reporting.persistence.jpa.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtAuthService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Optional<UsernamePasswordAuthenticationToken> authenticate(String token) {
        try {
            Long userId = extractUserIdFromPayload(token);
            String secret = resolveSecret(userId);
            if (secret == null) return Optional.empty();
            Claims claims = parseAndVerify(token, secret);
            JwtPrincipal principal = new JwtPrincipal(claims);
            String authority = roleToAuthority(principal.getRoleId());
            return Optional.of(new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    List.of(new SimpleGrantedAuthority(authority))));
        } catch (Exception e) {
            log.debug("JWT authentication failed: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private String resolveSecret(Long userId) {
        return userRepository.findJwtSecretByUserId(userId).orElse(null);
    }

    private Long extractUserIdFromPayload(String token) {
        String payload = new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]), StandardCharsets.UTF_8);
        int subIndex = payload.indexOf("\"sub\":\"");
        if (subIndex == -1) throw new IllegalArgumentException("Token missing subject");
        int start = subIndex + 7;
        int end = payload.indexOf("\"", start);
        return Long.valueOf(payload.substring(start, end));
    }

    private Claims parseAndVerify(String token, String secret) {
        return Jwts.parser()
                .verifyWith(buildKey(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private static SecretKey buildKey(String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, keyBytes.length);
            keyBytes = padded;
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private static String roleToAuthority(Long roleId) {
        if (roleId == null) return "ROLE_UNKNOWN";
        if (roleId == 1) return "ROLE_SUPER_ADMIN";
        if (roleId == 2) return "ROLE_ADMIN";
        if (roleId == 3) return "ROLE_TENANT";
        return "ROLE_" + roleId;
    }
}
