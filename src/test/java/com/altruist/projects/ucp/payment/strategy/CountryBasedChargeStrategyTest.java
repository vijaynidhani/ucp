package com.altruist.projects.ucp.payment.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CountryBasedChargeStrategyTest {
    
    private CountryBasedChargeStrategy chargeStrategy;
    
    @BeforeEach
    void setUp() {
        chargeStrategy = new CountryBasedChargeStrategy();
    }
    
    @Test
    void testCalculateChargesForIndia() {
        Double charges = chargeStrategy.calculateCharges("IN", 1000.0);
        assertEquals(10.0, charges); // 1% of 1000
    }
    
    @Test
    void testCalculateChargesForUSA() {
        Double charges = chargeStrategy.calculateCharges("US", 1000.0);
        assertEquals(30.0, charges); // 3% of 1000
    }
    
    @Test
    void testCalculateChargesForUK() {
        Double charges = chargeStrategy.calculateCharges("UK", 1000.0);
        assertEquals(25.0, charges); // 2.5% of 1000
    }
    
    @Test
    void testCalculateChargesForEurope() {
        Double charges = chargeStrategy.calculateCharges("EU", 1000.0);
        assertEquals(28.0, charges); // 2.8% of 1000
    }
    
    @Test
    void testCalculateChargesForOtherCountries() {
        Double charges = chargeStrategy.calculateCharges("AU", 1000.0);
        assertEquals(35.0, charges); // 3.5% of 1000 (default)
    }
    
    @Test
    void testCalculateChargesWithNullCountry() {
        Double charges = chargeStrategy.calculateCharges(null, 1000.0);
        assertEquals(0.0, charges);
    }
    
    @Test
    void testCalculateChargesWithNullAmount() {
        Double charges = chargeStrategy.calculateCharges("IN", null);
        assertEquals(0.0, charges);
    }
    
}
