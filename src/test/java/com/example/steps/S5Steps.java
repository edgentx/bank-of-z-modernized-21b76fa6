package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountOpenedEvent;
import com.example.domain.account.model.OpenAccountCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S5Steps {

  private AccountAggregate aggregate;
  private OpenAccountCmd cmd;
  private List<DomainEvent> resultEvents;
  private Exception capturedException;

  @Given("a valid Account aggregate")
  public void a_valid_Account_aggregate() {
    // Valid new aggregate in NONE state
    aggregate = new AccountAggregate("acc-123");
  }

  @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
  public void a_account_aggregate_violates_minimum_balance() {
    aggregate = new AccountAggregate("acc-low-balance");
    // Valid inputs
    cmd = new OpenAccountCmd("acc-low-balance", "cust-1", "SAVINGS", new BigDecimal("5.00"), "123456");
  }

  @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
  public void a_account_aggregate_violates_active_status() {
    aggregate = new AccountAggregate("acc-already-active");
    // We simulate an existing active account by creating it via the logic immediately
    // Since we cannot easily set state without a constructor variant or method, 
    // we rely on the logic: if we run the command twice, the second time it will fail.
    // However, the step definition sets up the context. 
    // Here we create a valid command, but the aggregate setup implies we are in an invalid state for opening.
    // But 'OpenAccountCmd' expects status NONE. If we set status to ACTIVE manually (if we had a method), it would fail.
    // Since we can't set private fields, we will rely on the logic that executing on an already opened aggregate fails.
    // But the test case setup is tricky. Let's assume the aggregate provided is valid, but the context implies the business rule check.
    // Actually, for "OpenAccountCmd rejected... Active status...", this likely means we try to open an account that is already active.
    aggregate = new AccountAggregate("acc-active");
    // Pre-condition: We create it once to make it active (simulating the violation state)
    var initCmd = new OpenAccountCmd("acc-active", "cust-1", "SAVINGS", new BigDecimal("100"), "123456");
    aggregate.execute(initCmd);
    
    // Now setup the command for the scenario
    cmd = new OpenAccountCmd("acc-active", "cust-1", "SAVINGS", new BigDecimal("100"), "123456");
  }

  @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
  public void a_account_aggregate_violates_unique_number() {
    aggregate = new AccountAggregate("acc-duplicate");
    // Similar to above, we simulate a 'duplicate' by having the account already exist.
    var initCmd = new OpenAccountCmd("acc-duplicate", "cust-1", "SAVINGS", new BigDecimal("100"), "123456");
    aggregate.execute(initCmd);
    
    cmd = new OpenAccountCmd("acc-duplicate", "cust-2", "SAVINGS", new BigDecimal("100"), "123456");
  }

  @And("a valid customerId is provided")
  public void a_valid_customerId_is_provided() {
    if (cmd == null) cmd = new OpenAccountCmd("acc-123", "cust-123", null, null, null);
    // The command is constructed fully in the 'When' or 'And' steps usually. 
    // We'll defer full construction or patch it. 
  }

  @And("a valid accountType is provided")
  public void a_valid_accountType_is_provided() {
    // Patch command if needed, or just placeholder
  }

  @And("a valid initialDeposit is provided")
  public void a_valid_initialDeposit_is_provided() {
    // Patch command if needed
  }

  @And("a valid sortCode is provided")
  public void a_valid_sortCode_is_provided() {
    // Finalize command for success scenario if not already set
    if ("acc-123".equals(aggregate.id())) {
        cmd = new OpenAccountCmd("acc-123", "cust-123", "SAVINGS", new BigDecimal("100.00"), "123456");
    }
  }

  @When("the OpenAccountCmd command is executed")
  public void the_OpenAccountCmd_command_is_executed() {
    try {
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  @Then("a account.opened event is emitted")
  public void a_account_opened_event_is_emitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof AccountOpenedEvent);
    
    AccountOpenedEvent event = (AccountOpenedEvent) resultEvents.get(0);
    assertEquals("account.opened", event.type());
    assertEquals("acc-123", event.aggregateId());
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(capturedException);
    // In Java domain, domain errors are often IllegalArgumentExceptions or IllegalStateExceptions
    assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
  }
}