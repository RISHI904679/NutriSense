package com.nutrisense.controller.user;

import com.nutrisense.dto.common.ApiResponse;
import com.nutrisense.dto.user.UserProfileRequest;
import com.nutrisense.dto.user.UserProfileResponse;
import com.nutrisense.service.user.UserProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUserProfile(
            HttpServletRequest httpServletRequest) {

        UserProfileResponse profile = userProfileService.getCurrentUserProfile();

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                HttpStatus.OK.value(),
                null,
                "Profile retrieved successfully.",
                httpServletRequest.getRequestURI(),
                LocalDateTime.now(),
                profile
        ));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateCurrentUserProfile(
            @Valid @RequestBody UserProfileRequest request,
            HttpServletRequest httpServletRequest) {

        UserProfileResponse profile = userProfileService.updateCurrentUserProfile(request);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                HttpStatus.OK.value(),
                null,
                "Profile updated successfully.",
                httpServletRequest.getRequestURI(),
                LocalDateTime.now(),
                profile
        ));
    }
}
