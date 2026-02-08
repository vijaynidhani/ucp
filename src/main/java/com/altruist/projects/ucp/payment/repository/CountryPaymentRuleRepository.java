package com.altruist.projects.ucp.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.altruist.projects.ucp.payment.model.CountryPaymentRule;

@Repository
public interface CountryPaymentRuleRepository extends JpaRepository<CountryPaymentRule, Long> {
    
    Optional<CountryPaymentRule> findByCountryCodeAndEnabledTrue(String countryCode);
    
}
