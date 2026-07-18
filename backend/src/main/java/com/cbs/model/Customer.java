package com.cbs.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    private String phone;

    @Enumerated(EnumType.STRING)
    private CustomerType type;

    @Enumerated(EnumType.STRING)
    private RiskProfile riskProfile;

    private String address;

    @Column(name = "kyc_status")
    @Enumerated(EnumType.STRING)
    private KycStatus kycStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (kycStatus == null) kycStatus = KycStatus.PENDING;
        if (riskProfile == null) riskProfile = RiskProfile.LOW;
    }

    public enum CustomerType { INDIVIDUAL, CORPORATE }
    public enum RiskProfile { LOW, MEDIUM, HIGH }
    public enum KycStatus { PENDING, VERIFIED, REJECTED }
}
