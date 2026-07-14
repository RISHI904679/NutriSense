package com.nutrisense.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "nutrient_gap_analysis", uniqueConstraints = @UniqueConstraint(name = "uk_summary_nutrient", columnNames = {"summary_id", "nutrient_id"}))
public class NutrientGapAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "summary_id", nullable = false)
    private DailyNutrientSummary summary;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nutrient_id", nullable = false)
    private NutrientMaster nutrient;

    @Column(name = "required_amount", precision = 10, scale = 2)
    private BigDecimal requiredAmount;

    @Column(name = "consumed_amount", precision = 10, scale = 2)
    private BigDecimal consumedAmount;

    @Column(name = "deficit_amount", precision = 10, scale = 2)
    private BigDecimal deficitAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10)
    private GapStatus status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    public enum GapStatus {
        LOW,
        ADEQUATE,
        EXCESS
    }
}
