package com.ecom.service;

import com.ecom.entity.RefreshToken;
import com.ecom.entity.User;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user);
    RefreshToken verifyAndGet(String token);
    void revoke(String token);
    void revokeAllForUser(User user);

    void deleteAllByUser(User user);
}
