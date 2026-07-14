package com.nutrisense.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "nutrition_targets")
public class NutritionTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "target_calories", precision = 8, scale = 2)
    private BigDecimal targetCalories;

    @Column(name = "target_protein", precision = 8, scale = 2)
    private BigDecimal targetProtein;

    @Column(name = "target_carbs", precision = 8, scale = 2)
    private BigDecimal targetCarbs;

    @Column(name = "target_fat", precision = 8, scale = 2)
    private BigDecimal targetFat;

    @Column(name = "target_water_liters", precision = 5, scale = 2)
    private BigDecimal targetWaterLiters;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Instant updatedAt;
}
