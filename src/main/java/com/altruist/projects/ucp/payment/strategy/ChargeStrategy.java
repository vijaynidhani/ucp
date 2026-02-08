package com.altruist.projects.ucp.payment.strategy;

/**
 * Strategy interface for calculating charges based on destination country
 */
public interface ChargeStrategy {
    
    /**
     * Calculate charges based on destination country and amount
     * @param destinationCountry The destination country code
     * @param amount The payment amount
     * @return The calculated charges
     */
    Double calculateCharges(String destinationCountry, Double amount);
    
}
