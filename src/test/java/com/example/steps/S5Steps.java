package com.example.steps;

import com.example.domain.account.model.*;
import com.example.domain.account.repository.AccountRepository;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class S5Steps {

    private AccountAggregate aggregate;
    private AccountRepository repository = new InMemoryAccountRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Helper class mimicking the pattern used in other tests
    private static class InMemoryAccountRepository implements AccountRepository {
        @Override
        public AccountAggregate save(AccountAggregate aggregate) {
            return aggregate;
        }
        @Override
        public AccountAggregate findById(String id) {
            return null;
        }
    }

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        String id = UUID.randomUUID().toString();
        aggregate = new AccountAggregate(id);
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // State stored for the command execution
    }

    @And("a valid accountType is provided")
    public void aValidAccountTypeIsProvided() {
        // State stored for the command execution
    }

    @And("a valid initialDeposit is provided")
    public void aValidInitialDepositIsProvided() {
        // State stored for the command execution
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        // State stored for the command execution
    }

    @When("the OpenAccountCmd command is executed")
    public void theOpenAccountCmdCommandIsExecuted() {
        // Using valid defaults for the happy path scenario
        executeCommand(new OpenAccountCmd(
            aggregate.id(),
            "cust-123",
            "CHECKING",
            new BigDecimal("150.00"),
            "10-20-30",
            "ACTIVE"
        ));
    }

    @Then("a account.opened event is emitted")
    public void aAccountOpenedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof AccountOpenedEvent);
        Assertions.assertEquals("account.opened", resultEvents.get(0).type());
    }

    // --- Negative Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesMinimumBalance() {
        String id = UUID.randomUUID().toString();
        aggregate = new AccountAggregate(id);
    }

    // Reuse When from above

    @When("the command is executed with insufficient funds")
    public void executeWithInsufficientFunds() {
        // Minimum for CHECKING is 100.00. Using 50.00.
        executeCommand(new OpenAccountCmd(
            aggregate.id(),
            "cust-123",
            "CHECKING",
            new BigDecimal("50.00"),
            "10-20-30",
            "ACTIVE"
        ));
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesActiveStatus() {
        String id = UUID.randomUUID().toString();
        aggregate = new AccountAggregate(id);
    }

    @When("the command is executed with invalid status")
    public void executeWithInvalidStatus() {
        // Passing PENDING or INACTIVE as status should fail
        executeCommand(new OpenAccountCmd(
            aggregate.id(),
            "cust-123",
            "CHECKING",
            new BigDecimal("150.00"),
            "10-20-30",
            "PENDING"
        ));
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutableId() {
        // We simulate an aggregate that is already initialized.
        // We do this by creating a command that triggers the state change to immutable,
        // and then trying to execute it again. 
        // However, since we are in a test step, we can manually instantiate the aggregate and 
        // force the internal state if we had setters, or we rely on the command execution flow.
        
        // Strategy: Create aggregate, execute a valid OpenAccountCmd to set immutable flag=true.
        String id = UUID.randomUUID().toString();
        aggregate = new AccountAggregate(id);
        
        // First execution succeeds
        Command cmd = new OpenAccountCmd(id, "cust-123", "SAVINGS", new BigDecimal("20.00"), "10-20-30", "ACTIVE");
        aggregate.execute(cmd);
        aggregate.markEventsCommitted(); // Clear events so we don't see them in the second run assertion
        
        // The aggregate is now effectively immutable.
    }

    @When("the command is executed on existing aggregate")
    public void executeOnExistingAggregate() {
         // Try to open the account again on the same aggregate instance
         executeCommand(new OpenAccountCmd(
            aggregate.id(),
            "cust-123",
            "CHECKING",
            new BigDecimal("150.00"),
            "10-20-30",
            "ACTIVE"
        ));
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        Assertions.assertTrue(
            capturedException instanceof IllegalArgumentException || 
            capturedException instanceof IllegalStateException
        );
    }

    // --- Helper Methods ---

    private void executeCommand(Command cmd) {
        try {
            this.resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            this.capturedException = e;
        }
    }
}