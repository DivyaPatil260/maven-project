package com.vodafone.prepaid.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "prepaid_accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrepaidAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 15)
    private String msisdn;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private String planName;

    @Column(nullable = false)
    private String status; // ACTIVE, MIGRATING, MIGRATED, SUSPENDED

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "ACTIVE";
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
