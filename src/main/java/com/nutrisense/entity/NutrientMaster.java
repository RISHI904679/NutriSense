package com.nutrisense.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "nutrient_master")
public class NutrientMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nutrient_name", nullable = false, unique = true, length = 100)
    private String nutrientName;

    @Enumerated(EnumType.STRING)
    @Column(name = "nutrient_type", nullable = false, length = 20)
    private NutrientType nutrientType;

    @Column(name = "unit", nullable = false, length = 20)
    private String unit;

    @Column(name = "recommended_daily_value", precision = 10, scale = 2)
    private BigDecimal recommendedDailyValue;

    @Column(name = "upper_limit", precision = 10, scale = 2)
    private BigDecimal upperLimit;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active")
    private Boolean active;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Instant updatedAt;

    public enum NutrientType {
        MACRO,
        VITAMIN,
        MINERAL,
        OTHER
    }
}
