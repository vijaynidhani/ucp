package com.altruist.projects.ucp.payment.config;

import java.time.LocalTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.altruist.projects.ucp.payment.model.CountryPaymentRule;
import com.altruist.projects.ucp.payment.repository.CountryPaymentRuleRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuration to initialize default country payment rules
 */
@Slf4j
@Configuration
public class CountryPaymentRuleConfig {
    
    @Bean
    CommandLineRunner initCountryRules(CountryPaymentRuleRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                log.info("Initializing default country payment rules...");
                
                List<CountryPaymentRule> defaultRules = List.of(
                    // India - INR rules
                    CountryPaymentRule.builder()
                            .countryCode("IN")
                            .minAmount(100.0)
                            .maxAmount(200000.0)
                            .operationStartTime(LocalTime.of(6, 0))  // 6:00 AM
                            .operationEndTime(LocalTime.of(22, 0))   // 10:00 PM
                            .timezone("Asia/Kolkata")
                            .enabled(true)
                            .description("India payment rules: ₹100 - ₹200,000, 6AM-10PM IST")
                            .build(),
                    
                    // United States - USD rules
                    CountryPaymentRule.builder()
                            .countryCode("US")
                            .minAmount(10.0)
                            .maxAmount(10000.0)
                            .operationStartTime(LocalTime.of(8, 0))  // 8:00 AM
                            .operationEndTime(LocalTime.of(20, 0))   // 8:00 PM
                            .timezone("America/New_York")
                            .enabled(true)
                            .description("USA payment rules: $10 - $10,000, 8AM-8PM EST")
                            .build(),
                    
                    // United Kingdom - GBP rules
                    CountryPaymentRule.builder()
                            .countryCode("GB")
                            .minAmount(5.0)
                            .maxAmount(5000.0)
                            .operationStartTime(LocalTime.of(7, 0))  // 7:00 AM
                            .operationEndTime(LocalTime.of(21, 0))   // 9:00 PM
                            .timezone("Europe/London")
                            .enabled(true)
                            .description("UK payment rules: £5 - £5,000, 7AM-9PM GMT")
                            .build(),
                    
                    // Singapore - SGD rules
                    CountryPaymentRule.builder()
                            .countryCode("SG")
                            .minAmount(20.0)
                            .maxAmount(50000.0)
                            .operationStartTime(LocalTime.of(8, 0))  // 8:00 AM
                            .operationEndTime(LocalTime.of(23, 0))   // 11:00 PM
                            .timezone("Asia/Singapore")
                            .enabled(true)
                            .description("Singapore payment rules: S$20 - S$50,000, 8AM-11PM SGT")
                            .build(),
                    
                    // Australia - AUD rules
                    CountryPaymentRule.builder()
                            .countryCode("AU")
                            .minAmount(10.0)
                            .maxAmount(15000.0)
                            .operationStartTime(LocalTime.of(7, 0))  // 7:00 AM
                            .operationEndTime(LocalTime.of(22, 0))   // 10:00 PM
                            .timezone("Australia/Sydney")
                            .enabled(true)
                            .description("Australia payment rules: A$10 - A$15,000, 7AM-10PM AEST")
                            .build()
                );
                
                repository.saveAll(defaultRules);
                log.info("Initialized {} country payment rules", defaultRules.size());
            } else {
                log.info("Country payment rules already exist, skipping initialization");
            }
        };
    }
}
