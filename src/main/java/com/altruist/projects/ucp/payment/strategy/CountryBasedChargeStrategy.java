package com.altruist.projects.ucp.payment.strategy;

import org.springframework.stereotype.Component;

/**
 * Default implementation of ChargeStrategy
 * Calculates charges based on destination country
 */
@Component
public class CountryBasedChargeStrategy implements ChargeStrategy {
    
    @Override
    public Double calculateCharges(String destinationCountry, Double amount) {
        if (destinationCountry == null || amount == null) {
            return 0.0;
        }
        
        // Template for country-based charges
        return switch (destinationCountry.toUpperCase()) {
            case "IN", "INDIA" -> amount * 0.01; // 1% for domestic (India)
            case "US", "USA" -> amount * 0.03; // 3% for USA
            case "UK", "GB" -> amount * 0.025; // 2.5% for UK
            case "EU", "EUR" -> amount * 0.028; // 2.8% for Europe
            default -> amount * 0.035; // 3.5% for other countries
        };
    }
    
}
