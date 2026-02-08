package com.altruist.projects.ucp.payment.gateway;

import com.altruist.projects.ucp.payment.dto.PaymentRequest;
import com.altruist.projects.ucp.payment.dto.PaymentResponse;

/**
 * Payment Gateway interface following Adapter pattern
 */
public interface PaymentGateway {
    
    /**
     * Process payment through the specific gateway
     */
    PaymentResponse processPayment(PaymentRequest request);
    
    /**
     * Get the gateway type
     */
    String getGatewayType();
    
}
