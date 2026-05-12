package com.example.api.customer.dto;

import com.example.domain.customer.model.UpdateCustomerDetailsCmd;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateCustomerDetailsRequest(
    @NotBlank @Email String emailAddress,
    @NotBlank String sortCode
) {
  public UpdateCustomerDetailsCmd toCommand(String customerId) {
    return new UpdateCustomerDetailsCmd(customerId, emailAddress, sortCode);
  }
}
