package com.nutrisense.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMax;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRequest {

    @NotBlank
    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    @Past
    private LocalDate dateOfBirth;

    private Gender gender;

    @Positive
    @DecimalMax("999.99")
    private Double heightCm;

    @Positive
    @DecimalMax("999.99")
    private Double currentWeightKg;

    @NotNull
    private Long activityLevelId;

    @NotNull
    private Long foodPreferenceId;

    @Size(max = 500)
    private String profileImageUrl;

    public enum Gender {
        MALE,
        FEMALE,
        OTHER
    }
}
