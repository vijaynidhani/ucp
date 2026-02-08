package com.altruist.projects.ucp.payment.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.altruist.projects.ucp.payment.dto.PaymentRequest;
import com.altruist.projects.ucp.payment.dto.PaymentResponse;
import com.altruist.projects.ucp.payment.gateway.PaymentGateway;
import com.altruist.projects.ucp.payment.model.Payment;
import com.altruist.projects.ucp.payment.repository.PaymentRepository;
import com.altruist.projects.ucp.payment.strategy.ChargeStrategy;
import com.altruist.projects.ucp.payment.validation.CountryPaymentRuleValidator;
import com.altruist.projects.ucp.payment.validation.CountryPaymentRuleValidator.ValidationResult;

import lombok.extern.slf4j.Slf4j;

/**
 * Payment Facade - Provides a simplified interface for payment processing
 * Implements Facade pattern to hide complexity of gateway selection and charge calculation
 */
@Slf4j
@Service
public class PaymentFacade {
    
    private final Map<String, PaymentGateway> paymentGateways;
    private final ChargeStrategy chargeStrategy;
    private final PaymentRepository paymentRepository;
    private final CountryPaymentRuleValidator ruleValidator;
    
    @Value("${payment.default.country:IN}")
    private String defaultCountry;
    
    public PaymentFacade(List<PaymentGateway> gateways, 
                        ChargeStrategy chargeStrategy,
                        PaymentRepository paymentRepository,
                        CountryPaymentRuleValidator ruleValidator) {
        this.paymentGateways = gateways.stream()
                .collect(Collectors.toMap(
                    PaymentGateway::getGatewayType,
                    Function.identity()
                ));
        this.chargeStrategy = chargeStrategy;
        this.paymentRepository = paymentRepository;
        this.ruleValidator = ruleValidator;
        
        log.info("Payment Facade initialized with gateways: {}", paymentGateways.keySet());
    }
    
    /**
     * Process payment by selecting appropriate gateway and calculating charges
     */
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Processing payment request for: {} via {}", request.getName(), request.getPaymentMethod());
        
        // Set default country if not provided
        if (request.getDestinationCountry() == null || request.getDestinationCountry().isEmpty()) {
            request.setDestinationCountry(defaultCountry);
            log.debug("Using default country: {}", defaultCountry);
        }
        
        // Validate request
        if (request.getPaymentMethod() == null || request.getAmount() == null) {
            return PaymentResponse.builder()
                    .status("FAILED")
                    .message("Payment method and amount are required")
                    .build();
        }
        
        // Validate country-specific rules (amount range and time window)
        ValidationResult validationResult = ruleValidator.validate(
            request.getDestinationCountry(), 
            request.getAmount()
        );
        
        if (!validationResult.isValid()) {
            log.warn("Payment validation failed: {}", validationResult.getErrorMessage());
            return PaymentResponse.builder()
                    .status("FAILED")
                    .message(validationResult.getErrorMessage())
                    .build();
        }
        
        // Select payment gateway based on payment method
        PaymentGateway gateway = selectGateway(request.getPaymentMethod());
        
        if (gateway == null) {
            log.error("No gateway found for payment method: {}", request.getPaymentMethod());
            return PaymentResponse.builder()
                    .status("FAILED")
                    .message("Unsupported payment method: " + request.getPaymentMethod())
                    .build();
        }
        
        // Calculate charges based on destination country
        Double charges = chargeStrategy.calculateCharges(
            request.getDestinationCountry(), 
            request.getAmount()
        );
        
        Double totalAmount = request.getAmount() + charges;
        
        log.info("Calculated charges: {} for country: {}, Total amount: {}", 
                charges, request.getDestinationCountry(), totalAmount);
        
        // Save payment record
        Payment payment = Payment.builder()
                .name(request.getName())
                .toAccount(request.getToAccount())
                .fromAccount(request.getFromAccount())
                .description(request.getDescription())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .destinationCountry(request.getDestinationCountry())
                .timestamp(LocalDateTime.now())
                .build();
        
        Payment savedPayment = paymentRepository.save(payment);
        log.debug("Payment record saved with ID: {}", savedPayment.getId());
        
        // Process payment through selected gateway
        PaymentResponse response = gateway.processPayment(request);
        
        // Update payment record with status and charges
        savedPayment.setStatus(response.getStatus());
        savedPayment.setCharges(charges);
        savedPayment.setTotalAmount(totalAmount);
        paymentRepository.save(savedPayment);
        
        // Enrich response with charges and total amount
        response.setPaymentId(savedPayment.getId());
        response.setCharges(charges);
        response.setTotalAmount(totalAmount);
        
        log.info("Payment processed with status: {}", response.getStatus());
        
        return response;
    }
    
    /**
     * Select payment gateway based on payment method
     */
    private PaymentGateway selectGateway(String paymentMethod) {
        if (paymentMethod == null) {
            return null;
        }
        return paymentGateways.get(paymentMethod.toUpperCase());
    }
    
    /**
     * Get list of available payment gateways
     */
    public List<String> getAvailableGateways() {
        return List.copyOf(paymentGateways.keySet());
    }
    
    /**
     * Get all payment history
     * TODO: Add pagination support for production use to handle large datasets
     * Consider using Pageable parameter and returning Page<Payment>
     */
    public List<Payment> getPaymentHistory() {
        log.info("Fetching all payment history");
        return paymentRepository.findAll();
    }
    
}
