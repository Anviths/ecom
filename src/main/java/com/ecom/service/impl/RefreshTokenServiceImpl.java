package com.ecom.service.impl;

import com.ecom.dao.RefreshTokenRepository;
import com.ecom.entity.RefreshToken;
import com.ecom.entity.User;
import com.ecom.exception.AuthenticationException;
import com.ecom.exception.TokenExpiredException;
import com.ecom.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository repo;

    @Value("${jwt.refresh-expiration-ms:2592000000}") // default 30 days
    private long refreshExpirationMs;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user) {
      RefreshToken refreshToken=   RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiresAt(Instant.now().plusMillis(refreshExpirationMs))
                .revoked(false)
                .user(user)
                .build();

       return  repo.save(refreshToken);

    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public RefreshToken verifyAndGet(String token) {

        RefreshToken rt=repo.findByToken(token)
                .orElseThrow(()-> new AuthenticationException("Invalid Refresh"));
                if(rt.isExpired() || rt.isRevoked()){
                    throw new TokenExpiredException();
                }
        return rt;
    }

    @Override
    @Transactional
    public void revoke(String token) {
       repo.findByToken(token).ifPresent(rt->{
               rt.revoke();
               repo.save(rt);});
    }

    @Override
    @Transactional
    public void revokeAllForUser(User user) {
        repo.findAllByUser(user).forEach(rt -> {
            rt.revoke();
            repo.save(rt);
        });
    }

    @Override
    public void deleteAllByUser(User user) {
        repo.deleteAllByUser(user);
    }
}
