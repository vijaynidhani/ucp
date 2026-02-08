package com.altruist.projects.ucp.payment.validation;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.altruist.projects.ucp.payment.model.CountryPaymentRule;
import com.altruist.projects.ucp.payment.repository.CountryPaymentRuleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service to validate payments against country-specific rules
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CountryPaymentRuleValidator {
    
    private final CountryPaymentRuleRepository ruleRepository;
    
    /**
     * Validate payment against country-specific rules
     * @return ValidationResult with success status and error message if failed
     */
    public ValidationResult validate(String countryCode, Double amount) {
        log.debug("Validating payment for country: {}, amount: {}", countryCode, amount);
        
        Optional<CountryPaymentRule> ruleOpt = ruleRepository.findByCountryCodeAndEnabledTrue(countryCode);
        
        if (ruleOpt.isEmpty()) {
            log.debug("No rules found for country: {}, allowing payment", countryCode);
            return ValidationResult.success();
        }
        
        CountryPaymentRule rule = ruleOpt.get();
        
        // Validate amount range
        ValidationResult amountResult = validateAmount(rule, amount);
        if (!amountResult.isValid()) {
            return amountResult;
        }
        
        // Validate time window
        ValidationResult timeResult = validateTimeWindow(rule);
        if (!timeResult.isValid()) {
            return timeResult;
        }
        
        log.info("Payment validation successful for country: {}", countryCode);
        return ValidationResult.success();
    }
    
    /**
     * Validate if amount is within allowed range for the country
     */
    private ValidationResult validateAmount(CountryPaymentRule rule, Double amount) {
        if (rule.getMinAmount() != null && amount < rule.getMinAmount()) {
            String message = String.format(
                "Payment amount %.2f is below minimum allowed %.2f for country %s",
                amount, rule.getMinAmount(), rule.getCountryCode()
            );
            log.warn(message);
            return ValidationResult.failure(message);
        }
        
        if (rule.getMaxAmount() != null && amount > rule.getMaxAmount()) {
            String message = String.format(
                "Payment amount %.2f exceeds maximum allowed %.2f for country %s",
                amount, rule.getMaxAmount(), rule.getCountryCode()
            );
            log.warn(message);
            return ValidationResult.failure(message);
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate if current time is within operational hours for the country
     */
    private ValidationResult validateTimeWindow(CountryPaymentRule rule) {
        if (rule.getOperationStartTime() == null || rule.getOperationEndTime() == null) {
            return ValidationResult.success(); // No time restriction
        }
        
        // Get current time in the country's timezone
        ZoneId zoneId = ZoneId.of(rule.getTimezone());
        ZonedDateTime nowInCountry = ZonedDateTime.now(zoneId);
        LocalTime currentTime = nowInCountry.toLocalTime();
        
        LocalTime startTime = rule.getOperationStartTime();
        LocalTime endTime = rule.getOperationEndTime();
        
        boolean isWithinOperationHours = !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime);
        
        if (!isWithinOperationHours) {
            String message = String.format(
                "Payment not allowed at this time for country %s. Operation hours: %s - %s (Current time: %s %s)",
                rule.getCountryCode(), startTime, endTime, currentTime, rule.getTimezone()
            );
            log.warn(message);
            return ValidationResult.failure(message);
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validation result wrapper
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class ValidationResult {
        private boolean valid;
        private String errorMessage;
        
        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }
        
        public static ValidationResult failure(String message) {
            return new ValidationResult(false, message);
        }
    }
}
