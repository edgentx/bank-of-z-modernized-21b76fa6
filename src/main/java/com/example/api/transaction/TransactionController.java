package com.example.api.transaction;

import com.example.api.transaction.dto.PostDepositRequest;
import com.example.api.transaction.dto.PostWithdrawalRequest;
import com.example.api.transaction.dto.ReverseTransactionRequest;
import com.example.api.transaction.dto.TransactionResponse;
import com.example.application.transaction.TransactionAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Transaction Processing", description = "Post deposits, withdrawals, and reversals")
public class TransactionController {

  private final TransactionAppService service;

  public TransactionController(TransactionAppService service) {
    this.service = service;
  }

  @PostMapping(value = "/deposits", consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Post a deposit transaction")
  public ResponseEntity<TransactionResponse> postDeposit(@Valid @RequestBody PostDepositRequest request) {
    return ResponseEntity.status(201).body(TransactionResponse.from(service.postDeposit(request.toCommand())));
  }

  @PostMapping(value = "/withdrawals", consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Post a withdrawal transaction")
  public ResponseEntity<TransactionResponse> postWithdrawal(@Valid @RequestBody PostWithdrawalRequest request) {
    return ResponseEntity.status(201).body(TransactionResponse.from(service.postWithdrawal(request.toCommand())));
  }

  @PostMapping(value = "/{transactionId}/reversal", consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Reverse a posted transaction")
  public TransactionResponse reverse(
      @PathVariable String transactionId,
      @Valid @RequestBody ReverseTransactionRequest request) {
    return TransactionResponse.from(service.reverse(transactionId, request.toCommand(transactionId)));
  }

  @GetMapping("/{transactionId}")
  @Operation(summary = "Fetch a transaction by id")
  public TransactionResponse find(@PathVariable String transactionId) {
    return TransactionResponse.from(service.findById(transactionId));
  }
}
