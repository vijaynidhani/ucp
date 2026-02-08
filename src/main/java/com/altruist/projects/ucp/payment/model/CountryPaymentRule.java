package com.altruist.projects.ucp.payment.model;

import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Country Payment Rule entity to store payment validation rules per country
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "country_payment_rules")
public class CountryPaymentRule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String countryCode;
    
    // Amount range validation
    private Double minAmount;
    private Double maxAmount;
    
    // Time-based validation (daytime operation hours)
    private LocalTime operationStartTime; // e.g., 08:00
    private LocalTime operationEndTime;   // e.g., 20:00
    
    private String timezone; // e.g., "Asia/Kolkata", "America/New_York"
    
    private Boolean enabled;
    
    private String description;
}
