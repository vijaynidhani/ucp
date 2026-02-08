package com.altruist.projects.ucp.payment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.altruist.projects.ucp.payment.dto.PaymentRequest;
import com.altruist.projects.ucp.payment.dto.PaymentResponse;
import com.altruist.projects.ucp.payment.gateway.CardPaymentGateway;
import com.altruist.projects.ucp.payment.gateway.UpiPaymentGateway;
import com.altruist.projects.ucp.payment.model.Payment;
import com.altruist.projects.ucp.payment.repository.PaymentRepository;
import com.altruist.projects.ucp.payment.strategy.CountryBasedChargeStrategy;

class PaymentFacadeTest {
    
    @Mock
    private PaymentRepository paymentRepository;
    
    private PaymentFacade paymentFacade;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        UpiPaymentGateway upiGateway = new UpiPaymentGateway();
        CardPaymentGateway cardGateway = new CardPaymentGateway();
        CountryBasedChargeStrategy chargeStrategy = new CountryBasedChargeStrategy();
        
        paymentFacade = new PaymentFacade(
            Arrays.asList(upiGateway, cardGateway),
            chargeStrategy,
            paymentRepository
        );
    }
    
    @Test
    void testProcessPaymentWithUPI() {
        // Given
        Payment savedPayment = Payment.builder()
                .id(1L)
                .name("John Doe")
                .toAccount("9876543210")
                .fromAccount("1234567890")
                .description("Test payment")
                .build();
        
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
        
        PaymentRequest request = PaymentRequest.builder()
                .name("John Doe")
                .toAccount("9876543210")
                .fromAccount("1234567890")
                .description("Test payment")
                .paymentMethod("UPI")
                .amount(1000.0)
                .destinationCountry("IN")
                .build();
        
        // When
        PaymentResponse response = paymentFacade.processPayment(request);
        
        // Then
        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals("UPI", response.getGatewayUsed());
        assertEquals(1L, response.getPaymentId());
        assertEquals(10.0, response.getCharges()); // 1% of 1000
        assertEquals(1010.0, response.getTotalAmount());
    }
    
    @Test
    void testProcessPaymentWithCard() {
        // Given
        Payment savedPayment = Payment.builder()
                .id(2L)
                .name("Jane Doe")
                .toAccount("4111111111111111")
                .fromAccount("1234567890")
                .description("Card payment")
                .build();
        
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
        
        PaymentRequest request = PaymentRequest.builder()
                .name("Jane Doe")
                .toAccount("4111111111111111")
                .fromAccount("1234567890")
                .description("Card payment")
                .paymentMethod("CARD")
                .amount(2000.0)
                .destinationCountry("US")
                .build();
        
        // When
        PaymentResponse response = paymentFacade.processPayment(request);
        
        // Then
        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals("CARD", response.getGatewayUsed());
        assertEquals(2L, response.getPaymentId());
        assertEquals(60.0, response.getCharges()); // 3% of 2000
        assertEquals(2060.0, response.getTotalAmount());
    }
    
    @Test
    void testProcessPaymentWithInvalidMethod() {
        // Given
        PaymentRequest request = PaymentRequest.builder()
                .name("Test User")
                .toAccount("123456")
                .fromAccount("654321")
                .description("Invalid payment")
                .paymentMethod("INVALID")
                .amount(500.0)
                .destinationCountry("IN")
                .build();
        
        // When
        PaymentResponse response = paymentFacade.processPayment(request);
        
        // Then
        assertNotNull(response);
        assertEquals("FAILED", response.getStatus());
        assertEquals("Unsupported payment method: INVALID", response.getMessage());
    }
    
    @Test
    void testGetAvailableGateways() {
        // When
        var gateways = paymentFacade.getAvailableGateways();
        
        // Then
        assertNotNull(gateways);
        assertEquals(2, gateways.size());
        assertEquals(true, gateways.contains("UPI"));
        assertEquals(true, gateways.contains("CARD"));
    }
    
}
