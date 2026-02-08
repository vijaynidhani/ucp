package com.altruist.projects.ucp.payment.validation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.altruist.projects.ucp.payment.model.CountryPaymentRule;
import com.altruist.projects.ucp.payment.repository.CountryPaymentRuleRepository;
import com.altruist.projects.ucp.payment.validation.CountryPaymentRuleValidator.ValidationResult;

@ExtendWith(MockitoExtension.class)
class CountryPaymentRuleValidatorTest {
    
    @Mock
    private CountryPaymentRuleRepository ruleRepository;
    
    @InjectMocks
    private CountryPaymentRuleValidator validator;
    
    private CountryPaymentRule indiaRule;
    
    @BeforeEach
    void setUp() {
        indiaRule = CountryPaymentRule.builder()
                .countryCode("IN")
                .minAmount(100.0)
                .maxAmount(200000.0)
                .operationStartTime(LocalTime.of(6, 0))
                .operationEndTime(LocalTime.of(22, 0))
                .timezone("Asia/Kolkata")
                .enabled(true)
                .build();
    }
    
    @Test
    void testValidateAmount_WithinRange_Success() {
        // Given
        when(ruleRepository.findByCountryCodeAndEnabledTrue("IN"))
                .thenReturn(Optional.of(indiaRule));
        
        // When
        ValidationResult result = validator.validate("IN", 5000.0);
        
        // Then
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }
    
    @Test
    void testValidateAmount_BelowMinimum_Failure() {
        // Given
        when(ruleRepository.findByCountryCodeAndEnabledTrue("IN"))
                .thenReturn(Optional.of(indiaRule));
        
        // When
        ValidationResult result = validator.validate("IN", 50.0);
        
        // Then
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains("below minimum"));
    }
    
    @Test
    void testValidateAmount_AboveMaximum_Failure() {
        // Given
        when(ruleRepository.findByCountryCodeAndEnabledTrue("IN"))
                .thenReturn(Optional.of(indiaRule));
        
        // When
        ValidationResult result = validator.validate("IN", 300000.0);
        
        // Then
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains("exceeds maximum"));
    }
    
    @Test
    void testValidateAmount_NoRuleForCountry_Success() {
        // Given
        when(ruleRepository.findByCountryCodeAndEnabledTrue("XX"))
                .thenReturn(Optional.empty());
        
        // When
        ValidationResult result = validator.validate("XX", 1000.0);
        
        // Then
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }
    
    @Test
    void testValidateAmount_EdgeCase_MinAmount_Success() {
        // Given
        when(ruleRepository.findByCountryCodeAndEnabledTrue("IN"))
                .thenReturn(Optional.of(indiaRule));
        
        // When
        ValidationResult result = validator.validate("IN", 100.0);
        
        // Then
        assertTrue(result.isValid());
    }
    
    @Test
    void testValidateAmount_EdgeCase_MaxAmount_Success() {
        // Given
        when(ruleRepository.findByCountryCodeAndEnabledTrue("IN"))
                .thenReturn(Optional.of(indiaRule));
        
        // When
        ValidationResult result = validator.validate("IN", 200000.0);
        
        // Then
        assertTrue(result.isValid());
    }
}
