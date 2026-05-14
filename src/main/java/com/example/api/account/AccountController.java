package com.example.api.account;

import com.example.api.account.dto.AccountSummary;
import com.example.api.account.dto.AccountResponse;
import com.example.api.account.dto.OpenAccountRequest;
import com.example.api.account.dto.UpdateAccountStatusRequest;
import com.example.application.account.AccountAppService;
import com.example.domain.account.model.AccountStatus;
import com.example.domain.account.model.CloseAccountCmd;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Account Management", description = "Open, update, and close bank accounts")
public class AccountController {

  private final AccountAppService service;

  public AccountController(AccountAppService service) {
    this.service = service;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Open a new account")
  public ResponseEntity<AccountResponse> open(@Valid @RequestBody OpenAccountRequest request) {
    return ResponseEntity.status(201).body(AccountResponse.from(service.open(request.toCommand())));
  }

  @PatchMapping(value = "/{accountId}/status", consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Update account status")
  public AccountResponse updateStatus(
      @PathVariable String accountId,
      @Valid @RequestBody UpdateAccountStatusRequest request) {
    return AccountResponse.from(service.updateStatus(accountId, request.toCommand(accountId)));
  }

  @DeleteMapping("/{accountId}")
  @Operation(summary = "Close an account")
  public ResponseEntity<Void> close(@PathVariable String accountId) {
    service.close(accountId, new CloseAccountCmd(accountId));
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  @Operation(summary = "List accounts")
  public ResponseEntity<Page<AccountSummary>> list(
      @RequestParam(required = false) String accountNumber,
      @RequestParam(required = false) String customerId,
      @RequestParam(required = false) AccountStatus status,
      @PageableDefault(page = 0, size = 25) Pageable pageable) {
    Pageable bounded = pageable.getPageSize() > 100
        ? PageRequest.of(pageable.getPageNumber(), 100, pageable.getSort())
        : pageable;
    return ResponseEntity.ok(service.list(accountNumber, customerId, status, bounded)
        .map(AccountSummary::from));
  }

  @GetMapping("/{accountId}")
  @Operation(summary = "Fetch an account by id")
  public AccountResponse find(@PathVariable String accountId) {
    return AccountResponse.from(service.findById(accountId));
  }
}
