package com.example.api.customer;

import com.example.api.customer.dto.CustomerResponse;
import com.example.api.customer.dto.CustomerSummary;
import com.example.api.customer.dto.EnrollCustomerRequest;
import com.example.api.customer.dto.UpdateCustomerDetailsRequest;
import com.example.application.customer.CustomerAppService;
import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.DeleteCustomerCmd;
import com.example.infrastructure.mongo.account.AccountMongoDataRepository;
import com.example.infrastructure.mongo.customer.CustomerMongoDataRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/customers", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Customer Management", description = "Enroll, update, and delete bank customers")
public class CustomerController {

  private final CustomerAppService service;
  private final CustomerMongoDataRepository customers;
  private final AccountMongoDataRepository accounts;

  public CustomerController(
      CustomerAppService service,
      CustomerMongoDataRepository customers,
      AccountMongoDataRepository accounts) {
    this.service = service;
    this.customers = customers;
    this.accounts = accounts;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Enroll a new customer")
  public ResponseEntity<CustomerResponse> enroll(@Valid @RequestBody EnrollCustomerRequest request) {
    CustomerAggregate agg = service.enroll(request.toCommand());
    return ResponseEntity.status(201).body(CustomerResponse.from(agg));
  }

  @PutMapping(value = "/{customerId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Update customer contact details")
  public CustomerResponse updateDetails(
      @PathVariable String customerId,
      @Valid @RequestBody UpdateCustomerDetailsRequest request) {
    return CustomerResponse.from(service.updateDetails(request.toCommand(customerId)));
  }

  @DeleteMapping("/{customerId}")
  @Operation(summary = "Delete a customer (must have no active accounts)")
  public ResponseEntity<Void> delete(
      @PathVariable String customerId,
      @RequestParam(defaultValue = "false") boolean hasActiveAccounts) {
    service.delete(new DeleteCustomerCmd(customerId, hasActiveAccounts));
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  @Operation(summary = "Search customers")
  public ResponseEntity<Page<CustomerSummary>> search(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String accountNumber,
      @PageableDefault(page = 0, size = 25) Pageable pageable) {
    Pageable bounded = pageable.getPageSize() > 100
        ? PageRequest.of(pageable.getPageNumber(), 100, pageable.getSort())
        : pageable;

    if (accountNumber != null && !accountNumber.isBlank()) {
      Page<CustomerSummary> page = accounts.findById(accountNumber.trim())
          .flatMap(account -> customers.findById(account.getCustomerId()))
          .<Page<CustomerSummary>>map(customer ->
              new PageImpl<>(List.of(CustomerSummary.from(customer)), bounded, 1))
          .orElseGet(() -> Page.empty(bounded));
      return ResponseEntity.ok(page);
    }

    Page<CustomerSummary> page = (name == null || name.isBlank()
        ? customers.findAll(bounded)
        : customers.findByFullNameContainingIgnoreCase(name.trim(), bounded))
        .map(CustomerSummary::from);
    return ResponseEntity.ok(page);
  }

  @GetMapping("/{customerId}")
  @Operation(summary = "Fetch a customer by id")
  public CustomerResponse find(@PathVariable String customerId) {
    return CustomerResponse.from(service.findById(customerId));
  }
}
