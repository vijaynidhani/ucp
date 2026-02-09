/**
 * Domain model classes for the UCP Payment system.
 * 
 * <p>This package contains JPA entity classes that represent the core domain model
 * for payment processing and business rules.</p>
 * 
 * <h2>Entity Classes:</h2>
 * <ul>
 *   <li>{@link com.altruist.projects.ucp.payment.model.Payment} - Payment transaction entity</li>
 *   <li>{@link com.altruist.projects.ucp.payment.model.CountryPaymentRule} - Country-specific payment validation rules</li>
 * </ul>
 * 
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Persistent storage of payment transactions</li>
 *   <li>Country-based payment rule configuration</li>
 *   <li>Amount validation and time-based restrictions</li>
 * </ul>
 * 
 * @since 1.0
 * @version 0.0.1-SNAPSHOT
 */
package com.altruist.projects.ucp.payment.model;
