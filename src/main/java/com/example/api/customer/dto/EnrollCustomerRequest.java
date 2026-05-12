package com.example.api.customer.dto;

import com.example.domain.customer.model.EnrollCustomerCmd;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EnrollCustomerRequest(
    @NotBlank String customerId,
    @NotBlank String fullName,
    @NotBlank @Email String email,
    @NotBlank String governmentId
) {
  public EnrollCustomerCmd toCommand() {
    return new EnrollCustomerCmd(customerId, fullName, email, governmentId);
  }
}
