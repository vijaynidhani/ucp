package com.altruist.projects.ucp.payment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.altruist.projects.ucp.payment.model.CountryPaymentRule;
import com.altruist.projects.ucp.payment.repository.CountryPaymentRuleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for managing country payment rules
 */
@Slf4j
@RestController
@RequestMapping("/api/country-rules")
@RequiredArgsConstructor
public class CountryPaymentRuleController {
    
    private final CountryPaymentRuleRepository ruleRepository;
    
    /**
     * Get all country payment rules
     */
    @GetMapping
    public ResponseEntity<List<CountryPaymentRule>> getAllRules() {
        log.info("Fetching all country payment rules");
        List<CountryPaymentRule> rules = ruleRepository.findAll();
        return ResponseEntity.ok(rules);
    }
    
    /**
     * Get rule by country code
     */
    @GetMapping("/{countryCode}")
    public ResponseEntity<CountryPaymentRule> getRuleByCountry(@PathVariable String countryCode) {
        log.info("Fetching rule for country: {}", countryCode);
        return ruleRepository.findByCountryCodeAndEnabledTrue(countryCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Create new country payment rule
     */
    @PostMapping
    public ResponseEntity<CountryPaymentRule> createRule(@RequestBody CountryPaymentRule rule) {
        log.info("Creating new payment rule for country: {}", rule.getCountryCode());
        CountryPaymentRule savedRule = ruleRepository.save(rule);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRule);
    }
    
    /**
     * Update existing country payment rule
     */
    @PutMapping("/{id}")
    public ResponseEntity<CountryPaymentRule> updateRule(
            @PathVariable Long id, 
            @RequestBody CountryPaymentRule rule) {
        log.info("Updating payment rule with ID: {}", id);
        
        return ruleRepository.findById(id)
                .map(existingRule -> {
                    rule.setId(id);
                    CountryPaymentRule updated = ruleRepository.save(rule);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
