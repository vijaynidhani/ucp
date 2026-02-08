package com.altruist.projects.ucp.payment.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String toAccount;
    private String fromAccount;
    private String description;
    private Double amount;
    private Double charges;
    private Double totalAmount;
    private String paymentMethod;
    private String status;
    private String destinationCountry;
    private LocalDateTime timestamp;
    
}
