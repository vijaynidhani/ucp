package com.altruist.projects.ucp.payment.gateway;

import org.springframework.stereotype.Component;

import com.altruist.projects.ucp.payment.dto.PaymentRequest;
import com.altruist.projects.ucp.payment.dto.PaymentResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Card Payment Gateway Adapter
 */
@Slf4j
@Component
public class CardPaymentGateway implements PaymentGateway {
    
    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Processing payment through CARD gateway for account: {}", request.getToAccount());
        
        // Simulate card payment processing
        try {
            // Card-specific processing logic would go here
            log.debug("Card payment processed successfully");
            
            return PaymentResponse.builder()
                    .status("SUCCESS")
                    .message("Payment processed successfully via Card")
                    .gatewayUsed("CARD")
                    .build();
                    
        } catch (Exception e) {
            log.error("Error processing card payment: {}", e.getMessage());
            return PaymentResponse.builder()
                    .status("FAILED")
                    .message("Card payment failed: " + e.getMessage())
                    .gatewayUsed("CARD")
                    .build();
        }
    }
    
    @Override
    public String getGatewayType() {
        return "CARD";
    }
    
}
