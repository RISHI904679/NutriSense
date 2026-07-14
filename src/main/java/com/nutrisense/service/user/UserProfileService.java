package com.nutrisense.service.user;

import com.nutrisense.dto.user.UserProfileRequest;
import com.nutrisense.dto.user.UserProfileResponse;
import com.nutrisense.entity.ActivityLevel;
import com.nutrisense.entity.FoodPreference;
import com.nutrisense.entity.User;
import com.nutrisense.entity.UserProfile;
import com.nutrisense.exception.ResourceNotFoundException;
import com.nutrisense.exception.UnauthorizedException;
import com.nutrisense.mapper.user.UserProfileMapper;
import com.nutrisense.repository.ActivityLevelRepository;
import com.nutrisense.repository.FoodPreferenceRepository;
import com.nutrisense.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final ActivityLevelRepository activityLevelRepository;
    private final FoodPreferenceRepository foodPreferenceRepository;
    private final UserProfileMapper userProfileMapper;

    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile() {
        User user = getCurrentUser();
        UserProfile profile = userProfileRepository.findByUserId(user.getId())
                .filter(existingProfile -> !Boolean.TRUE.equals(existingProfile.getDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found"));

        return userProfileMapper.toResponse(profile);
    }

    @Transactional
    public UserProfileResponse updateCurrentUserProfile(UserProfileRequest request) {
        User user = getCurrentUser();
        ActivityLevel activityLevel = activityLevelRepository.findById(request.getActivityLevelId())
                .orElseThrow(() -> new ResourceNotFoundException("Activity level not found"));
        FoodPreference foodPreference = foodPreferenceRepository.findById(request.getFoodPreferenceId())
                .orElseThrow(() -> new ResourceNotFoundException("Food preference not found"));

        UserProfile profile = userProfileRepository.findByUserId(user.getId())
                .filter(existingProfile -> !Boolean.TRUE.equals(existingProfile.getDeleted()))
                .orElseGet(() -> {
                    UserProfile newProfile = userProfileMapper.toEntity(request);
                    newProfile.setUser(user);
                    newProfile.setDeleted(false);
                    return newProfile;
                });

        userProfileMapper.update(request, profile);
        profile.setActivityLevel(activityLevel);
        profile.setFoodPreference(foodPreference);

        return userProfileMapper.toResponse(userProfileRepository.save(profile));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new UnauthorizedException("Authentication is required");
        }

        return user;
    }
}
