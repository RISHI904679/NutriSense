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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "health_scores", uniqueConstraints = @UniqueConstraint(name = "uk_healthscore", columnNames = {"user_id", "score_date"}))
public class HealthScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "score_date", nullable = false)
    private LocalDate scoreDate;

    @Column(name = "nutrition_score", precision = 5, scale = 2)
    private BigDecimal nutritionScore;

    @Column(name = "hydration_score", precision = 5, scale = 2)
    private BigDecimal hydrationScore;

    @Column(name = "workout_score", precision = 5, scale = 2)
    private BigDecimal workoutScore;

    @Column(name = "overall_score", precision = 5, scale = 2)
    private BigDecimal overallScore;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;
}
