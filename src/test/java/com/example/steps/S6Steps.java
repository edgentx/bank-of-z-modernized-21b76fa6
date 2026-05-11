package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.UpdateAccountStatusCmd;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S6Steps {

  private AccountAggregate aggregate;
  private UpdateAccountStatusCmd cmd;
  private List<DomainEvent> resultEvents;
  private Exception caughtException;

  @Given("a valid Account aggregate")
  public void a_valid_account_aggregate() {
    aggregate = new AccountAggregate("ACC-123");
    aggregate.setBalance(BigDecimal.valueOf(1000));
  }

  @Given("a valid accountNumber is provided")
  public void a_valid_account_number_is_provided() {
    // Handled in the When clause construction
  }

  @Given("a valid newStatus is provided")
  public void a_valid_new_status_is_provided() {
    // Handled in the When clause construction
  }

  @When("the UpdateAccountStatusCmd command is executed")
  public void the_update_account_status_cmd_command_is_executed() {
    try {
      // Default valid values if not overridden by specific scenarios
      String accNum = (aggregate != null) ? aggregate.getAccountNumber() : "ACC-123";
      cmd = new UpdateAccountStatusCmd(accNum, "Frozen");
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      caughtException = e;
    }
  }

  @Then("a account.status.updated event is emitted")
  public void a_account_status_updated_event_is_emitted() {
    Assertions.assertNotNull(resultEvents);
    Assertions.assertEquals(1, resultEvents.size());
    Assertions.assertEquals("account.status.updated", resultEvents.get(0).type());
  }

  // --- Rejection Scenarios ---

  @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
  public void a_account_aggregate_that_violates_balance() {
    aggregate = new AccountAggregate("ACC-LOW");
    aggregate.setBalance(BigDecimal.valueOf(50)); // Below min 100
    aggregate.setAccountType("Standard");
  }

  @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
  public void a_account_aggregate_that_violates_active_status_requirement() {
    aggregate = new AccountAggregate("ACC-ACTIVE");
    aggregate.setBalance(BigDecimal.valueOf(1000));
    aggregate.setStatus("Active");
    // This is a precondition violation simulation. In a real system, we might check pending transactions.
    // Here we assume the 'Frozen' transition triggers the check described in the story.
  }

  @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
  public void a_account_aggregate_that_violates_immutability() {
    aggregate = new AccountAggregate("ACC-ORIGINAL");
    // The violation is simulated by sending a command with a DIFFERENT account number than the aggregate ID
  }

  @When("the UpdateAccountStatusCmd command is executed")
  public void the_command_is_executed_for_rejection() {
    try {
      if (aggregate.getAccountNumber().equals("ACC-ORIGINAL")) {
        // Simulate immutability violation by hacking the command construction for this specific scenario logic
        cmd = new UpdateAccountStatusCmd("ACC-DIFFERENT", "Closed");
      } else if (aggregate.getBalance().compareTo(new BigDecimal("100")) < 0) {
         // This will trigger the balance check in the aggregate logic defined in the Domain Code section
         // assuming the logic in AccountAggregate enforces it.
         // Re-using the standard constructor for the balance scenario to hit the validation inside.
         cmd = new UpdateAccountStatusCmd(aggregate.getAccountNumber(), "Frozen");
      } else {
         cmd = new UpdateAccountStatusCmd(aggregate.getAccountNumber(), "Frozen");
      }
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      caughtException = e;
    }
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    Assertions.assertNotNull(caughtException, "Expected an exception but command succeeded");
    Assertions.assertTrue(caughtException instanceof IllegalStateException);
  }
}