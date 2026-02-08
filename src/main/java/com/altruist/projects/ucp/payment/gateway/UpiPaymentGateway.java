package com.altruist.projects.ucp.payment.gateway;

import org.springframework.stereotype.Component;

import com.altruist.projects.ucp.payment.dto.PaymentRequest;
import com.altruist.projects.ucp.payment.dto.PaymentResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * UPI Payment Gateway Adapter
 */
@Slf4j
@Component
public class UpiPaymentGateway implements PaymentGateway {
    
   
    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Processing payment through UPI gateway for account: {}", request.getToAccount());
        
        // Simulate UPI payment processing
        try {
            // UPI-specific processing logic would go here
            log.debug("UPI payment processed successfully");
            
            return PaymentResponse.builder()
                    .status("SUCCESS")
                    .message("Payment processed successfully via UPI")
                    .gatewayUsed("UPI")
                    .build();
                    
        } catch (Exception e) {
            log.error("Error processing UPI payment: {}", e.getMessage());
            return PaymentResponse.builder()
                    .status("FAILED")
                    .message("UPI payment failed: " + e.getMessage())
                    .gatewayUsed("UPI")
                    .build();
        }
    }
    
    @Override
    public String getGatewayType() {
        return "UPI";
    }
    
}
