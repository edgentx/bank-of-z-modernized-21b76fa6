package com.example.steps;

import com.example.domain.account.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class S6Steps {

    private AccountAggregate aggregate;
    private Command lastCommand;
    private Throwable thrownException;
    private List<DomainEvent> resultingEvents;

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        aggregate = new AccountAggregate("ACC-123", "ACC-123", AccountType.CHECKING);
        // Simulate aggregate being hydrated from event store with an initial state
        aggregate.apply(new AccountOpenedEvent("ACC-123", "ACC-123", AccountType.CHECKING, Instant.now()));
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Step setup logic is combined in the 'When' step construction or context setup
        // For this scenario, we assume the command is constructed with valid data in the When step.
    }

    @Given("a valid newStatus is provided")
    public void a_valid_newStatus_is_provided() {
        // Step setup logic
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void the_UpdateAccountStatusCmd_command_is_executed() {
        lastCommand = new UpdateAccountStatusCmd("ACC-123", AccountStatus.FROZEN);
        try {
            resultingEvents = aggregate.execute(lastCommand);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void a_account_status_updated_event_is_emitted() {
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertFalse(resultingEvents.isEmpty());
        Assertions.assertEquals("account.status.updated", resultingEvents.get(0).type());
    }

    // --- Rejection Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_Account_aggregate_that_violates_minimum_balance() {
        aggregate = new AccountAggregate("ACC-LOW", "ACC-LOW", AccountType.CHECKING);
        aggregate.apply(new AccountOpenedEvent("ACC-LOW", "ACC-LOW", AccountType.CHECKING, Instant.now()));
        // Force state to have a balance that would prevent closure/update if invariant checked against balance
        // Note: The command logic checks for Balance invariant. Here we set up aggregate.
        // The UpdateAccountStatusCmd doesn't inherently change balance, but S-6 AC implies an invariant check.
        // Assuming the invariant is checked: maybe status update to CLOSED requires zero balance?
        // The AC says: "Account balance cannot drop below minimum".
        // This might imply the command payload *could* trigger a balance change, OR we are testing a specific status (e.g. CLOSED)
        // that enforces a balance check.
        aggregate.setBalance(BigDecimal.ONE); // Low balance
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_Account_aggregate_that_violates_active_status() {
        aggregate = new AccountAggregate("ACC-INA", "ACC-INA", AccountType.CHECKING);
        aggregate.apply(new AccountOpenedEvent("ACC-INA", "ACC-INA", AccountType.CHECKING, Instant.now()));
        // Set status to FROZEN explicitly for the scenario context
        aggregate.setStatus(AccountStatus.FROZEN);
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_Account_aggregate_that_violates_immutability() {
        aggregate = new AccountAggregate("ACC-123", "ACC-123", AccountType.CHECKING);
        aggregate.apply(new AccountOpenedEvent("ACC-123", "ACC-123", AccountType.CHECKING, Instant.now()));
        // We will try to issue a command that implies changing the account number logic if that were part of the command,
        // but UpdateAccountStatusCmd only takes status.
        // However, to satisfy the AC "Rejected with domain error", we might check if the command contains a new AccountNumber
        // and reject it.
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException || 
                            thrownException instanceof IllegalStateException || 
                            thrownException instanceof UnsupportedOperationException);
    }

}
