package com.altruist.projects.ucp.payment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.altruist.projects.ucp.payment.dto.PaymentRequest;
import com.altruist.projects.ucp.payment.dto.PaymentResponse;
import com.altruist.projects.ucp.payment.model.Payment;
import com.altruist.projects.ucp.payment.service.PaymentFacade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentFacade paymentFacade;
    
    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest request) {
        log.info("Received payment request for: {}", request.getName());
        
        PaymentResponse response = paymentFacade.processPayment(request);
        
        if ("SUCCESS".equals(response.getStatus())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @GetMapping("/gateways")
    public ResponseEntity<List<String>> getAvailableGateways() {
        log.info("Fetching available payment gateways");
        return ResponseEntity.ok(paymentFacade.getAvailableGateways());
    }
    
    @GetMapping("/history")
    public ResponseEntity<List<Payment>> getPaymentHistory() {
        log.info("Fetching payment history");
        List<Payment> history = paymentFacade.getPaymentHistory();
        return ResponseEntity.ok(history);
    }
    
}
