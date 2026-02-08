package com.altruist.projects.ucp.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    
    private Long paymentId;
    private String status;
    private String message;
    private Double totalAmount;
    private Double charges;
    private String gatewayUsed;
    
}
