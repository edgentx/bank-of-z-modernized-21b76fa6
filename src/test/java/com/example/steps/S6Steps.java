package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.UpdateAccountStatusCmd;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S6Steps {

  private AccountAggregate aggregate;
  private UpdateAccountStatusCmd cmd;
  private List<DomainEvent> resultingEvents;
  private Exception caughtException;

  @Given("a valid Account aggregate")
  public void a_valid_Account_aggregate() {
    aggregate = new AccountAggregate("acct-123");
    aggregate.setAccountNumber("ACC-998877");
    aggregate.setStatus("ACTIVE");
  }

  @Given("a valid accountNumber is provided")
  public void a_valid_accountNumber_is_provided() {
    // Handled in context setup or step definition combination
  }

  @Given("a valid newStatus is provided")
  public void a_valid_newStatus_is_provided() {
    // Handled in context setup or step definition combination
  }

  @When("the UpdateAccountStatusCmd command is executed")
  public void the_UpdateAccountStatusCmd_command_is_executed() {
    // Default valid command construction for the happy path
    if (cmd == null) {
        cmd = new UpdateAccountStatusCmd("acct-123", "ACC-998877", "FROZEN", new BigDecimal("500.00"), "CHECKING");
    }
    try {
        resultingEvents = aggregate.execute(cmd);
    } catch (Exception e) {
        caughtException = e;
    }
  }

  @Then("a account.status.updated event is emitted")
  public void a_account_status_updated_event_is_emitted() {
    assertNotNull(resultingEvents);
    assertEquals(1, resultingEvents.size());
    assertEquals("account.status.updated", resultingEvents.get(0).type());
    assertNull(caughtException);
  }

  @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
  public void a_Account_aggregate_that_violates_minimum_balance() {
    aggregate = new AccountAggregate("acct-low");
    aggregate.setStatus("ACTIVE");
    // Balance 50, Min for Savings 100
    cmd = new UpdateAccountStatusCmd("acct-low", "ACC-001", "ACTIVE", new BigDecimal("50.00"), "SAVINGS");
  }

  @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
  public void a_Account_aggregate_that_violates_active_status() {
    aggregate = new AccountAggregate("acct-inactive");
    aggregate.setStatus("INACTIVE_FOR_OPS"); // Simulating a state that forbids ops
    cmd = new UpdateAccountStatusCmd("acct-inactive", "ACC-002", "FROZEN", new BigDecimal("100.00"), "CHECKING");
  }

  @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
  public void a_Account_aggregate_that_violates_immutability() {
    aggregate = new AccountAggregate("acct-immute");
    aggregate.setAccountNumber("ORIGINAL-123");
    // Try to change number
    cmd = new UpdateAccountStatusCmd("acct-immute", "CHANGED-456", "ACTIVE", new BigDecimal("100.00"), "CHECKING");
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(caughtException);
    assertTrue(caughtException instanceof IllegalStateException);
  }
}
