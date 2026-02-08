package com.altruist.projects.ucp.payment.gateway;

import org.springframework.stereotype.Component;

import com.altruist.projects.ucp.payment.dto.PaymentRequest;
import com.altruist.projects.ucp.payment.dto.PaymentResponse;


import lombok.extern.slf4j.Slf4j;

/**
 * Apple Pay Payment Gateway Adapter
 */
@Slf4j
@Component
public class ApplePayPaymentGateway implements PaymentGateway {
    
   
    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Processing payment through Apple Pay gateway for account: {}", request.getToAccount());
        
        // Simulate Apple Pay payment processing
        try {
            // Apple Pay-specific processing logic would go here
            log.debug("Apple Pay payment processed successfully");
            
            return PaymentResponse.builder()
                    .status("SUCCESS")
                    .message("Payment processed successfully via Apple Pay")
                    .gatewayUsed("APPLE_PAY")
                    .build();
                    
        } catch (Exception e) {
            log.error("Error processing Apple Pay payment: {}", e.getMessage());
            return PaymentResponse.builder()
                    .status("FAILED")
                    .message("Apple Pay payment failed: " + e.getMessage())
                    .gatewayUsed("APPLE_PAY")
                    .build();
        }
    }
    
    @Override
    public String getGatewayType() {
        return "APPLE_PAY";
    }
    
}
