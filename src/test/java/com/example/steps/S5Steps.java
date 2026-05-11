package com.example.steps;

import com.example.domain.account.model.*;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S5Steps {

    private AccountAggregate aggregate;
    private Exception caughtException;
    private List<com.example.domain.shared.DomainEvent> resultEvents;

    // Helper for valid defaults
    private OpenAccountCmd.Builder validCmd() {
        return new OpenAccountCmd.Builder(
            "acc-123",
            "cust-456",
            "SAVINGS",
            new BigDecimal("100.00"),
            "10-20-30"
        );
    }

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        aggregate = new AccountAggregate("acc-123");
        caughtException = null;
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Handled in the When step via Builder defaults
    }

    @And("a valid accountType is provided")
    public void aValidAccountTypeIsProvided() {
        // Handled in the When step via Builder defaults
    }

    @And("a valid initialDeposit is provided")
    public void aValidInitialDepositIsProvided() {
        // Handled in the When step via Builder defaults
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        // Handled in the When step via Builder defaults
    }

    // --- Violation Scenarios Setup ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesBalance() {
        aggregate = new AccountAggregate("acc-violate-balance");
        // The violation for opening implies a negative initial deposit (simulating a constraint violation)
        // or a deposit too low for the type. We'll use negative for strict failure.
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesActiveStatus() {
        aggregate = new AccountAggregate("acc-violate-status");
        // To test this invariant in the context of 'Open', we simulate an attempt to open an already open/active account
        // by pre-generating the number, which simulates an existing state in this in-memory model.
        aggregate.execute(new OpenAccountCmd("acc-violate-status", "cust-1", "CHECKING", BigDecimal.ZERO, "00-00-00"));
        aggregate.clearEvents(); // Clear the events from the setup so the test only checks the second command
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutableNumber() {
        aggregate = new AccountAggregate("acc-violate-number");
        // Similar to above, we open it once to set the number, then the test will try to open it again.
        aggregate.execute(new OpenAccountCmd("acc-violate-number", "cust-1", "CHECKING", BigDecimal.TEN, "00-00-00"));
        aggregate.clearEvents();
    }

    // --- Actions ---

    @When("the OpenAccountCmd command is executed")
    public void theOpenAccountCmdCommandIsExecuted() {
        try {
            // This logic handles the context of which scenario is running based on the state set in Given.
            // If we are in the "Violate Balance" scenario, we pass a bad deposit.
            if (aggregate.getId().equals("acc-violate-balance")) {
                 resultEvents = aggregate.execute(new OpenAccountCmd("acc-violate-balance", "cust-1", "SAVINGS", new BigDecimal("-50.00"), "10-20-30"));
            } else {
                // Standard execution (Success, or pre-set state violation)
                resultEvents = aggregate.execute(validCmd().build());
            }
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // --- Outcomes ---

    @Then("a account.opened event is emitted")
    public void aAccountOpenedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals("account.opened", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException);
        // Verify it's a domain logic exception (IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(
            caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
            "Expected domain error, got: " + caughtException.getClass().getSimpleName()
        );
    }
}
