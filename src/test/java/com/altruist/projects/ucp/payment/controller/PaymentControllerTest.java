package com.altruist.projects.ucp.payment.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.altruist.projects.ucp.payment.dto.PaymentRequest;
import com.altruist.projects.ucp.payment.dto.PaymentResponse;
import com.altruist.projects.ucp.payment.model.Payment;
import com.altruist.projects.ucp.payment.service.PaymentFacade;

class PaymentControllerTest {
    
    @Mock
    private PaymentFacade paymentFacade;
    
    private PaymentController paymentController;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        paymentController = new PaymentController(paymentFacade);
    }
    
    @Test
    void testGetPaymentHistory() {
        // Given
        Payment payment1 = Payment.builder()
                .id(1L)
                .name("John Doe")
                .toAccount("9876543210")
                .fromAccount("1234567890")
                .description("Test payment 1")
                .amount(1000.0)
                .charges(10.0)
                .totalAmount(1010.0)
                .paymentMethod("UPI")
                .status("SUCCESS")
                .destinationCountry("IN")
                .timestamp(LocalDateTime.now())
                .build();
        
        Payment payment2 = Payment.builder()
                .id(2L)
                .name("Jane Doe")
                .toAccount("4111111111111111")
                .fromAccount("9876543210")
                .description("Test payment 2")
                .amount(2000.0)
                .charges(60.0)
                .totalAmount(2060.0)
                .paymentMethod("CARD")
                .status("SUCCESS")
                .destinationCountry("US")
                .timestamp(LocalDateTime.now())
                .build();
        
        List<Payment> payments = Arrays.asList(payment1, payment2);
        when(paymentFacade.getPaymentHistory()).thenReturn(payments);
        
        // When
        ResponseEntity<List<Payment>> response = paymentController.getPaymentHistory();
        
        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("John Doe", response.getBody().get(0).getName());
        assertEquals("Jane Doe", response.getBody().get(1).getName());
        assertEquals(1000.0, response.getBody().get(0).getAmount());
        assertEquals(2000.0, response.getBody().get(1).getAmount());
    }
    
    @Test
    void testGetPaymentHistoryEmpty() {
        // Given
        when(paymentFacade.getPaymentHistory()).thenReturn(List.of());
        
        // When
        ResponseEntity<List<Payment>> response = paymentController.getPaymentHistory();
        
        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
    }
    
    @Test
    void testGetAvailableGateways() {
        // Given
        List<String> gateways = Arrays.asList("UPI", "CARD", "APPLE_PAY");
        when(paymentFacade.getAvailableGateways()).thenReturn(gateways);
        
        // When
        ResponseEntity<List<String>> response = paymentController.getAvailableGateways();
        
        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
    }
    
    @Test
    void testProcessPaymentSuccess() {
        // Given
        PaymentRequest request = PaymentRequest.builder()
                .name("John Doe")
                .toAccount("9876543210")
                .fromAccount("1234567890")
                .description("Test payment")
                .paymentMethod("UPI")
                .amount(1000.0)
                .destinationCountry("IN")
                .build();
        
        PaymentResponse paymentResponse = PaymentResponse.builder()
                .paymentId(1L)
                .status("SUCCESS")
                .message("Payment processed successfully")
                .totalAmount(1010.0)
                .charges(10.0)
                .gatewayUsed("UPI")
                .build();
        
        when(paymentFacade.processPayment(request)).thenReturn(paymentResponse);
        
        // When
        ResponseEntity<PaymentResponse> response = paymentController.processPayment(request);
        
        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("SUCCESS", response.getBody().getStatus());
        assertEquals(1010.0, response.getBody().getTotalAmount());
    }
    
}
