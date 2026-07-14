package com.nutrisense.controller.auth;

import com.nutrisense.dto.auth.RegisterRequest;
import com.nutrisense.dto.auth.LoginRequest;
import com.nutrisense.dto.auth.AuthResponse;
import com.nutrisense.dto.auth.OtpVerificationRequest;
import com.nutrisense.dto.auth.RefreshTokenRequest;
import com.nutrisense.dto.common.ApiResponse;
import com.nutrisense.entity.RefreshToken;
import com.nutrisense.entity.User;
import com.nutrisense.exception.ResourceNotFoundException;
import com.nutrisense.repository.UserRepository;
import com.nutrisense.service.auth.AuthService;
import com.nutrisense.service.auth.JwtService;
import com.nutrisense.service.auth.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpServletRequest) {

        authService.register(request);

        ApiResponse<Object> response = new ApiResponse<>(
                true,
                HttpStatus.CREATED.value(),
                null,
                "Registration successful. Please verify your email.",
                httpServletRequest.getRequestURI(),
                LocalDateTime.now(),
                null
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpServletRequest) {

        AuthResponse authResponse = authService.login(request);

        ApiResponse<Object> response = new ApiResponse<>(
                true,
                HttpStatus.OK.value(),
                null,
                "Login successful.",
                httpServletRequest.getRequestURI(),
                LocalDateTime.now(),
                authResponse
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Object>> refresh(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpServletRequest) {

        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(request.getRefreshToken());
        AuthResponse authResponse = new AuthResponse(
                refreshToken.getUser().getId(),
                refreshToken.getUser().getEmail(),
                jwtService.generateAccessToken(refreshToken.getUser()),
                refreshToken.getToken(),
                "Bearer",
                900L
        );

        ApiResponse<Object> response = new ApiResponse<>(
                true,
                HttpStatus.OK.value(),
                null,
                "Access token refreshed.",
                httpServletRequest.getRequestURI(),
                LocalDateTime.now(),
                authResponse
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<Object>> verifyEmail(
            @Valid @RequestBody OtpVerificationRequest request,
            HttpServletRequest httpServletRequest) {

        authService.verifyEmailOtp(request.getEmail(), request.getOtpCode());

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                HttpStatus.OK.value(),
                null,
                "Email verified successfully.",
                httpServletRequest.getRequestURI(),
                LocalDateTime.now(),
                null
        ));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Object>> forgotPassword(
            @RequestParam String email,
            HttpServletRequest httpServletRequest) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        authService.generatePasswordResetOtp(user);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                HttpStatus.OK.value(),
                null,
                "Password reset OTP generated.",
                httpServletRequest.getRequestURI(),
                LocalDateTime.now(),
                null
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Object>> resetPassword(
            @Valid @RequestBody OtpVerificationRequest request,
            @RequestParam String newPassword,
            HttpServletRequest httpServletRequest) {

        authService.resetPassword(request.getEmail(), request.getOtpCode(), newPassword);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                HttpStatus.OK.value(),
                null,
                "Password reset successfully.",
                httpServletRequest.getRequestURI(),
                LocalDateTime.now(),
                null
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpServletRequest) {

        authService.logout(request.getRefreshToken());

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                HttpStatus.OK.value(),
                null,
                "Logout successful.",
                httpServletRequest.getRequestURI(),
                LocalDateTime.now(),
                null
        ));
    }
}
