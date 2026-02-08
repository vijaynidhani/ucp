package com.altruist.projects.ucp.payment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.altruist.projects.ucp.payment.dto.PaymentRequest;
import com.altruist.projects.ucp.payment.dto.PaymentResponse;
import com.altruist.projects.ucp.payment.gateway.ApplePayPaymentGateway;
import com.altruist.projects.ucp.payment.gateway.CardPaymentGateway;
import com.altruist.projects.ucp.payment.gateway.UpiPaymentGateway;
import com.altruist.projects.ucp.payment.model.Payment;
import com.altruist.projects.ucp.payment.repository.PaymentRepository;
import com.altruist.projects.ucp.payment.strategy.CountryBasedChargeStrategy;
import com.altruist.projects.ucp.payment.validation.CountryPaymentRuleValidator;
import com.altruist.projects.ucp.payment.validation.CountryPaymentRuleValidator.ValidationResult;

class PaymentFacadeTest {
    
    @Mock
    private PaymentRepository paymentRepository;
    
    @Mock
    private CountryPaymentRuleValidator ruleValidator;
    
    private PaymentFacade paymentFacade;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        UpiPaymentGateway upiGateway = new UpiPaymentGateway();
        CardPaymentGateway cardGateway = new CardPaymentGateway();
        ApplePayPaymentGateway applePayGateway = new ApplePayPaymentGateway();
        CountryBasedChargeStrategy chargeStrategy = new CountryBasedChargeStrategy();
        
        // Mock successful validation by default
        when(ruleValidator.validate(any(), any())).thenReturn(ValidationResult.success());
        
        paymentFacade = new PaymentFacade(
            Arrays.asList(upiGateway, cardGateway, applePayGateway),
            chargeStrategy,
            paymentRepository,
            ruleValidator
        );
        
        // Set default country using reflection since @Value won't be injected in tests
        ReflectionTestUtils.setField(paymentFacade, "defaultCountry", "IN");
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
    void testProcessPaymentWithApplePay() {
        // Given
        Payment savedPayment = Payment.builder()
                .id(3L)
                .name("Alice Smith")
                .toAccount("apple123456")
                .fromAccount("1234567890")
                .description("Apple Pay payment")
                .build();
        
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
        
        PaymentRequest request = PaymentRequest.builder()
                .name("Alice Smith")
                .toAccount("apple123456")
                .fromAccount("1234567890")
                .description("Apple Pay payment")
                .paymentMethod("APPLE_PAY")
                .amount(1500.0)
                .destinationCountry("GB")
                .build();
        
        // When
        PaymentResponse response = paymentFacade.processPayment(request);
        
        // Then
        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals("APPLE_PAY", response.getGatewayUsed());
        assertEquals(3L, response.getPaymentId());
        assertEquals(37.5, response.getCharges()); // 2.5% of 1500
        assertEquals(1537.5, response.getTotalAmount());
    }
    
    @Test
    void testProcessPaymentWithDefaultCountry() {
        // Given
        Payment savedPayment = Payment.builder()
                .id(4L)
                .name("Test User")
                .toAccount("9876543210")
                .fromAccount("1234567890")
                .description("Payment without country")
                .build();
        
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
        
        PaymentRequest request = PaymentRequest.builder()
                .name("Test User")
                .toAccount("9876543210")
                .fromAccount("1234567890")
                .description("Payment without country")
                .paymentMethod("UPI")
                .amount(1000.0)
                .destinationCountry(null) // No country specified
                .build();
        
        // When
        PaymentResponse response = paymentFacade.processPayment(request);
        
        // Then
        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals("UPI", response.getGatewayUsed());
        assertEquals(4L, response.getPaymentId());
        assertEquals(10.0, response.getCharges()); // 1% of 1000 (default to IN)
        assertEquals(1010.0, response.getTotalAmount());
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
        assertEquals(3, gateways.size());
        assertTrue(gateways.contains("UPI"));
        assertTrue(gateways.contains("CARD"));
        assertTrue(gateways.contains("APPLE_PAY"));
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
                .build();
        
        Payment payment2 = Payment.builder()
                .id(2L)
                .name("Jane Doe")
                .toAccount("4111111111111111")
                .fromAccount("9876543210")
                .description("Test payment 2")
                .build();
        
        when(paymentRepository.findAll()).thenReturn(Arrays.asList(payment1, payment2));
        
        // When
        var history = paymentFacade.getPaymentHistory();
        
        // Then
        assertNotNull(history);
        assertEquals(2, history.size());
        assertEquals("John Doe", history.get(0).getName());
        assertEquals("Jane Doe", history.get(1).getName());
    }
    
}
