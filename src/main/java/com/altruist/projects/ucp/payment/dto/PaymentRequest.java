package com.altruist.projects.ucp.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    
    private String name;
    private String toAccount;
    private String fromAccount;
    private String description;
    @Builder.Default
    private String destinationCountry = "IN"; // Default to India
    private String paymentMethod; // "UPI" or "CARD" or "APPLE_PAY"
    private Double amount;
    
}
