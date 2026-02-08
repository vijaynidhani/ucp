package com.altruist.projects.ucp.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.altruist.projects.ucp.payment.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
}
