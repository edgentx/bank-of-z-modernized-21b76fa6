package com.example.domain.customer.model;
import com.example.domain.shared.Command;
public record EnrollCustomerCmd(String customerId, String fullName, String email, String governmentId) implements Command {}
