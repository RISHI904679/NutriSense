package com.nutrisense.mapper.user;

import com.nutrisense.dto.user.UserProfileRequest;
import com.nutrisense.dto.user.UserProfileResponse;
import com.nutrisense.entity.UserProfile;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserProfileMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "heightCm", source = "heightCm")
    @Mapping(target = "currentWeightKg", source = "currentWeightKg")
    @Mapping(target = "activityLevelId", source = "activityLevel.id")
    @Mapping(target = "foodPreferenceId", source = "foodPreference.id")
    @Mapping(target = "profileImageUrl", source = "profileImageUrl")
    UserProfileResponse toResponse(UserProfile entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "activityLevel", ignore = true)
    @Mapping(target = "foodPreference", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "heightCm", source = "heightCm")
    @Mapping(target = "currentWeightKg", source = "currentWeightKg")
    @Mapping(target = "profileImageUrl", source = "profileImageUrl")
    UserProfile toEntity(UserProfileRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "activityLevel", ignore = true)
    @Mapping(target = "foodPreference", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "heightCm", source = "heightCm")
    @Mapping(target = "currentWeightKg", source = "currentWeightKg")
    @Mapping(target = "profileImageUrl", source = "profileImageUrl")
    void update(UserProfileRequest request, @MappingTarget UserProfile entity);
}
