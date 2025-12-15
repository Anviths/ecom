package com.ecom.service.impl;

import com.ecom.dao.UserRepository;
import com.ecom.dto.AuthResponse;
import com.ecom.dto.LoginRequest;
import com.ecom.dto.RegisterRequest;
import com.ecom.entity.RefreshToken;
import com.ecom.entity.User;
import com.ecom.exception.AccountLockedException;
import com.ecom.exception.InvalidCredentialsException;
import com.ecom.exception.TooManyRequestsException;
import com.ecom.exception.UserNotFoundException;
import com.ecom.ratelimter.LoginRateLimiter;
import com.ecom.security.JwtService;
import com.ecom.service.AuthService;
import com.ecom.service.RefreshTokenService;
import com.ecom.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final LoginRateLimiter loginRateLimiter;
    @Override
    public AuthResponse registerUser(RegisterRequest request) {
        User user=userService.createUser(request);
        String token=jwtService.generateToken(user);
        RefreshToken refreshToken=refreshTokenService.createRefreshToken(user);
        return buildResponse(user,token,refreshToken.getToken());
    }

    @Override
    @Transactional(noRollbackFor = InvalidCredentialsException.class)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        // 🔒 Check lock
        if (user.getAccountLockedUntil() != null &&
                user.getAccountLockedUntil().isAfter(LocalDateTime.now())) {
            throw new AccountLockedException(
                    "Account locked until " + user.getAccountLockedUntil());
        }

        if (!loginRateLimiter.allowRequest(request.getEmail())) {
            throw new TooManyRequestsException("Too many login attempts");
        }
        try {

                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getEmail(),
                                    request.getPassword()
                            )
                    );


            user.setFailedLoginAttempts(0);
            user.setAccountLockedUntil(null);
            userRepository.save(user);

            refreshTokenService.deleteAllByUser(user);

            String token = jwtService.generateToken(user);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            return buildResponse(user, token, refreshToken.getToken());

        } catch (BadCredentialsException ex) {


            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);

            if (attempts >= MAX_FAILED_ATTEMPTS) {
                user.setAccountLockedUntil(LocalDateTime.now().plus(LOCK_DURATION));
            }

            userRepository.save(user);
            throw new InvalidCredentialsException();
        }

    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        RefreshToken rt=refreshTokenService.verifyAndGet(refreshToken);
        User user=rt.getUser();
        String acessToken= jwtService.generateToken(user);
        return buildResponse(user,acessToken,rt.getToken());
    }

    @Override
    public void logout(String refreshToken) {
        refreshTokenService.revoke(refreshToken);
    }
    private AuthResponse buildResponse(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .name(user.getName())
                .roles(new HashSet<>(user.getRoles()))
                .build();
    }
}
