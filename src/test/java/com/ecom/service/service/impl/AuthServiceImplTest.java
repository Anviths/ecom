package com.ecom.service.service.impl;

import com.ecom.dto.AuthResponse;
import com.ecom.dto.LoginRequest;
import com.ecom.dto.RegisterRequest;
import com.ecom.entity.RefreshToken;
import com.ecom.entity.User;
import com.ecom.exception.InvalidCredentialsException;
import com.ecom.security.JwtService;
import com.ecom.service.RefreshTokenService;
import com.ecom.service.UserService;
import com.ecom.service.impl.AuthServiceImpl;
import com.ecom.service.util.TestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
   private UserService userService;
    @Mock
   private JwtService jwtService;
    @Mock
   private AuthenticationManager authenticationManager;
    @Mock
   private RefreshTokenService refreshTokenService;
   @InjectMocks
   private AuthServiceImpl authService;

    @Test
    void register_success_shouldReturnAuthResponse() {
        RegisterRequest request= TestData.registerRequest();
        User user=TestData.user();
        RefreshToken token=TestData.refreshToken(user);

        when(userService.createUser(request)).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");
        when(refreshTokenService.createRefreshToken(user)).thenReturn(token);

        AuthResponse  authResponse=authService.registerUser(request);

        assertNotNull(authResponse);
        assertEquals("jwt-token",authResponse.getToken());
        assertEquals(token.getToken(),authResponse.getRefreshToken());
        assertEquals(user.getEmail(),authResponse.getEmail());

        verify(userService).createUser(request);
        verify(jwtService).generateToken(user);
        verify(refreshTokenService).createRefreshToken(user);


    }

    @Test
    void login_success_shouldReturnTokens(){
        LoginRequest request=TestData.loginRequest();
        User user=TestData.user();
        RefreshToken refreshToken=TestData.refreshToken(user);

        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));

        when(userService.loadUserByEmail(request.getEmail()))
                .thenReturn(user);
        when(jwtService.generateToken(user))
                .thenReturn("jwt-token");
        when(refreshTokenService.createRefreshToken(user)).thenReturn(refreshToken);

        AuthResponse response=authService.login(request);

        assertNotNull(response.getToken());
        assertNotNull(response.getRefreshToken());
    }

    @Test
    void login_invalidCredentials_shouldThrowException() {
        LoginRequest request=TestData.loginRequest();

       when( authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

       assertThrows(InvalidCredentialsException.class,()->authService.login(request));
    }

    @Test
    void refreshToken_valid_shouldReturnNewJwt() {
        User user=TestData.user();
        RefreshToken refreshToken=TestData.refreshToken(user);


        when(refreshTokenService.verifyAndGet(refreshToken.getToken())).thenReturn(refreshToken);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthResponse response=authService.refreshToken(refreshToken.getToken());

        assertEquals("jwt-token",response.getToken());
    }

    @Test
    void logout_shouldRevokeRefreshToken() {
        String refreshToken = "refresh-token";

        authService.logout(refreshToken);

        verify(refreshTokenService).revoke(refreshToken);
    }
}
