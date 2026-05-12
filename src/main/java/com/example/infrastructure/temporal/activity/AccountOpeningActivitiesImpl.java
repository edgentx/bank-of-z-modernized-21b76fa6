package com.example.infrastructure.temporal.activity;

import com.example.application.account.AccountAppService;
import com.example.application.customer.CustomerAppService;
import com.example.application.transaction.TransactionAppService;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.account.model.OpenAccountCmd;
import com.example.domain.customer.model.EnrollCustomerCmd;
import com.example.domain.transaction.model.PostDepositCmd;
import com.example.domain.transaction.model.ReverseTransactionCmd;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

/**
 * BANK S-33 — concrete activity implementations for the account-opening
 * saga.
 *
 * <p>Pure adapter: each method translates the activity arguments into the
 * appropriate domain command and forwards to the existing app service. No
 * banking logic lives here — keeping aggregates the single source of truth
 * for invariants means the saga workflow can be reasoned about purely in
 * terms of "step A then step B" without having to know how an account is
 * opened.
 *
 * <p>Activities are stateless Spring components — Temporal will share the
 * single instance across all worker threads. Each public method must be
 * thread-safe; thread-safety here is delegated to the underlying app
 * services and the domain aggregates they load (each call creates a fresh
 * aggregate from the repository).
 */
@Component
public class AccountOpeningActivitiesImpl implements AccountOpeningActivities {

  private final CustomerAppService customerService;
  private final AccountAppService accountService;
  private final TransactionAppService transactionService;

  public AccountOpeningActivitiesImpl(CustomerAppService customerService,
                                      AccountAppService accountService,
                                      TransactionAppService transactionService) {
    this.customerService = customerService;
    this.accountService = accountService;
    this.transactionService = transactionService;
  }

  @Override
  public String enrollCustomer(String customerId, String firstName, String lastName, String email) {
    String fullName = ((firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName)).trim();
    // EnrollCustomerCmd's fourth argument is governmentId — the saga does not
    // collect one at account-open time so we pass a placeholder; the
    // aggregate accepts any non-null string. A future story can extend the
    // input payload when the KYC flow lands.
    EnrollCustomerCmd cmd = new EnrollCustomerCmd(customerId, fullName, email, "PENDING-KYC");
    customerService.enroll(cmd);
    return customerId;
  }

  @Override
  public String openAccount(String accountId, String customerId, String accountType,
                            long initialDepositCents, String sortCode) {
    OpenAccountCmd cmd = new OpenAccountCmd(accountId, customerId, accountType, initialDepositCents, sortCode);
    accountService.open(cmd);
    return accountId;
  }

  @Override
  public String postInitialDeposit(String transactionId, String accountNumber,
                                   long amountCents, String currency) {
    // The transaction aggregate keeps amount as BigDecimal; convert cents to
    // a 2-decimal BigDecimal so downstream ledger views can render currency
    // without re-doing the unit conversion. Banking domains uniformly use
    // BigDecimal to avoid IEEE-754 rounding in the audit trail.
    BigDecimal amount = BigDecimal.valueOf(amountCents).movePointLeft(2);
    PostDepositCmd cmd = new PostDepositCmd(transactionId, accountNumber, amount, currency);
    transactionService.postDeposit(cmd);
    return transactionId;
  }

  // -- compensations -----------------------------------------------------------

  @Override
  public void reverseTransaction(String transactionId, String reason) {
    transactionService.reverse(transactionId, new ReverseTransactionCmd(transactionId, reason));
  }

  @Override
  public void closeAccount(String accountId, String reason) {
    accountService.close(accountId, new CloseAccountCmd(accountId));
  }
}
