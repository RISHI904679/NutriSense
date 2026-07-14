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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "login_history")
public class LoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "login_time", insertable = false, updatable = false)
    private Instant loginTime;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "device_name", length = 255)
    private String deviceName;

    @Column(name = "browser_name", length = 100)
    private String browserName;

    @Column(name = "operating_system", length = 100)
    private String operatingSystem;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_status", nullable = false, length = 10)
    private LoginStatus loginStatus;

    public enum LoginStatus {
        SUCCESS,
        FAILED
    }
}
