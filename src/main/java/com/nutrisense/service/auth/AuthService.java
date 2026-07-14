package com.nutrisense.service.auth;

import com.nutrisense.dto.auth.RegisterRequest;
import com.nutrisense.dto.auth.LoginRequest;
import com.nutrisense.dto.auth.AuthResponse;
import com.nutrisense.entity.RefreshToken;
import com.nutrisense.entity.OtpVerification;
import com.nutrisense.entity.Role;
import com.nutrisense.entity.User;
import com.nutrisense.entity.UserRole;
import com.nutrisense.exception.DuplicateResourceException;
import com.nutrisense.exception.InvalidTokenException;
import com.nutrisense.exception.ResourceNotFoundException;
import com.nutrisense.exception.TokenExpiredException;
import com.nutrisense.exception.UnauthorizedException;
import com.nutrisense.mapper.auth.AuthMapper;
import com.nutrisense.repository.RoleRepository;
import com.nutrisense.repository.OtpVerificationRepository;
import com.nutrisense.repository.UserRepository;
import com.nutrisense.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.security.SecureRandom;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthenticationManager {

    private static final String ROLE_USER = "ROLE_USER";
    private static final long EMAIL_VERIFICATION_OTP_EXPIRATION_MILLIS = 600_000L;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OtpVerificationRepository otpVerificationRepository;
    private final UserRoleRepository userRoleRepository;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email is already registered");
        }

        String phoneNumber = request.getPhoneNumber();
        if (phoneNumber != null && !phoneNumber.isBlank()
                && userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new DuplicateResourceException("Phone number is already registered");
        }

        Role role = roleRepository.findByRoleName(ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role ROLE_USER not found"));

        User user = authMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(phoneNumber == null || phoneNumber.isBlank() ? null : phoneNumber);
        user.setAccountStatus(User.AccountStatus.INACTIVE);
        user.setEmailVerified(false);
        user.setPhoneVerified(false);
        user.setDeleted(false);

        User savedUser = userRepository.save(user);
        userRoleRepository.save(UserRole.builder()
                .user(savedUser)
                .role(role)
                .build());

        return savedUser;
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.getEmail(), request.getPassword()));
        User user = (User) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user).getToken();

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                accessToken,
                refreshToken,
                "Bearer",
                900L);
    }

    public AuthResponse refreshAccessToken(String token) {
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(token);
        User user = refreshToken.getUser();

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                jwtService.generateAccessToken(user),
                refreshToken.getToken(),
                "Bearer",
                900L);
    }

    @Transactional
    public String generateEmailVerificationOtp(User user) {
        String otpCode = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));

        otpVerificationRepository.save(OtpVerification.builder()
                .user(user)
                .otpCode(otpCode)
                .otpType(OtpVerification.OtpType.REGISTER)
                .expiresAt(Instant.now().plusMillis(EMAIL_VERIFICATION_OTP_EXPIRATION_MILLIS))
                .used(false)
                .build());

        return otpCode;
    }

    @Transactional
    public String generatePasswordResetOtp(User user) {
        String otpCode = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));

        otpVerificationRepository.save(OtpVerification.builder()
                .user(user)
                .otpCode(otpCode)
                .otpType(OtpVerification.OtpType.PASSWORD_RESET)
                .expiresAt(Instant.now().plusMillis(EMAIL_VERIFICATION_OTP_EXPIRATION_MILLIS))
                .used(false)
                .build());

        return otpCode;
    }

    @Transactional
    public boolean resetPassword(String email, String otpCode, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidTokenException("Invalid password reset OTP"));

        OtpVerification otpVerification = otpVerificationRepository.findByUserIdAndUsedFalse(user.getId()).stream()
                .filter(otp -> otp.getOtpType() == OtpVerification.OtpType.PASSWORD_RESET)
                .filter(otp -> otp.getOtpCode().equals(otpCode))
                .findFirst()
                .orElseThrow(() -> new InvalidTokenException("Invalid password reset OTP"));

        if (!otpVerification.getExpiresAt().isAfter(Instant.now())) {
            throw new TokenExpiredException("Password reset OTP has expired");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        otpVerification.setUsed(true);

        userRepository.save(user);
        otpVerificationRepository.save(otpVerification);

        return true;
    }

    public boolean logout(String refreshToken) {
        refreshTokenService.revokeRefreshToken(refreshToken);
        return true;
    }

    @Transactional
    public void verifyEmailOtp(String email, String otpCode) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidTokenException("Invalid email verification OTP"));

        OtpVerification otpVerification = otpVerificationRepository.findByUserIdAndUsedFalse(user.getId()).stream()
                .filter(otp -> otp.getOtpType() == OtpVerification.OtpType.REGISTER)
                .filter(otp -> otp.getOtpCode().equals(otpCode))
                .findFirst()
                .orElseThrow(() -> new InvalidTokenException("Invalid email verification OTP"));

        if (!otpVerification.getExpiresAt().isAfter(Instant.now())) {
            throw new TokenExpiredException("Email verification OTP has expired");
        }

        otpVerification.setUsed(true);
        user.setEmailVerified(true);
        user.setAccountStatus(User.AccountStatus.ACTIVE);

        otpVerificationRepository.save(otpVerification);
        userRepository.save(user);
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(authentication.getCredentials().toString(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        if (Boolean.TRUE.equals(user.getDeleted())
                || user.getAccountStatus() != User.AccountStatus.ACTIVE) {
            throw new UnauthorizedException("Account is not active");
        }

        return UsernamePasswordAuthenticationToken.authenticated(user, null, List.of());
    }
}
