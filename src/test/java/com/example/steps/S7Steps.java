package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.account.model.AccountClosedEvent;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S7Steps {

  private AccountAggregate aggregate;
  private String accountNumber;
  private Exception capturedException;
  private List<DomainEvent> resultEvents;

  @Given("a valid Account aggregate")
  public void a_valid_Account_aggregate() {
    accountNumber = "123456789";
    aggregate = new AccountAggregate(accountNumber);
  }

  @Given("a valid accountNumber is provided")
  public void a_valid_accountNumber_is_provided() {
    // accountNumber already set in previous step
    assertNotNull(accountNumber);
  }

  @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
  public void a_account_aggregate_that_violates_balance() {
    // Setup aggregate with non-zero balance
    accountNumber = "987654321";
    aggregate = new AccountAggregate(accountNumber);
  }

  @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
  public void a_account_aggregate_that_violates_status() {
    // Setup aggregate with inactive status (e.g. already closed or suspended)
    accountNumber = "111111111";
    aggregate = new AccountAggregate(accountNumber);
  }

  @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
  public void a_account_aggregate_that_violates_immutability() {
    // Simulate a scenario where the command ID doesn't match aggregate ID
    accountNumber = "999999999";
    aggregate = new AccountAggregate(accountNumber);
  }

  @When("the CloseAccountCmd command is executed")
  public void the_CloseAccountCmd_command_is_executed() {
    // Determine context based on previous Given steps to construct the right Command
    // We inspect the aggregate state or context to decide what command to send.
    // This allows us to test both success and failure paths.
    
    String cmdAccountNumber = accountNumber;
    String status = "ACTIVE";
    BigDecimal balance = BigDecimal.ZERO;
    String type = "SAVINGS";

    // Adjust parameters based on which 'Given' block ran
    if (accountNumber.equals("987654321")) {
      // Violation: Balance > 0
      balance = new BigDecimal("100.50");
    } else if (accountNumber.equals("111111111")) {
      // Violation: Not Active
      status = "SUSPENDED";
    } else if (accountNumber.equals("999999999")) {
      // Violation: Immutable ID mismatch
      cmdAccountNumber = "000000000"; // Send command for wrong account
    }

    try {
      CloseAccountCmd cmd = new CloseAccountCmd(cmdAccountNumber, status, balance, type);
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  @Then("a account.closed event is emitted")
  public void a_account_closed_event_is_emitted() {
    assertNotNull(resultEvents, "Expected events to be emitted, but got null");
    assertEquals(1, resultEvents.size(), "Expected exactly one event");
    assertTrue(resultEvents.get(0) instanceof AccountClosedEvent, "Expected AccountClosedEvent");
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(capturedException, "Expected an exception to be thrown");
    // Domain errors manifest as IllegalStateException or IllegalArgumentException
    assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
  }
}
