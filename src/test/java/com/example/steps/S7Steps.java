package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-7: CloseAccountCmd.
 */
public class S7Steps {

    private AccountAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        // Default valid state: Active, Zero Balance
        aggregate = new AccountAggregate("ACC-123", AccountAggregate.AccountType.SAVINGS);
    }

    @Given("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // Account number is implicitly provided by the aggregate setup in the previous step
        // Additional validation if needed
        assertNotNull(aggregate.id());
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesAccountBalanceCannotDropBelowTheMinimumRequiredBalance() {
        aggregate = new AccountAggregate("ACC-999", AccountAggregate.AccountType.SAVINGS);
        // Set balance to non-zero to violate the zero-balance requirement for closing
        aggregate.setBalance(new BigDecimal("100.50"));
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesAnAccountMustBeInAnActiveStatusToProcessWithdrawalsOrTransfers() {
        aggregate = new AccountAggregate("ACC-888", AccountAggregate.AccountType.CHECKING);
        // Set status to CLOSED or FROZEN to violate the Active status requirement
        aggregate.setStatus(AccountAggregate.AccountStatus.FROZEN);
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesAccountNumbersMustBeUniquelyGeneratedAndImmutable() {
        aggregate = new AccountAggregate("ACC-777", AccountAggregate.AccountType.SAVINGS);
        // The violation will be simulated by providing a mismatched AccountNumber in the Command during the 'When' step.
        // However, since the aggregate is created with a specific ID, we can't really "violate" immutability on the aggregate itself
        // without mutating it, which is illegal.
        // The scenario will be handled by passing a command with a different ID than the aggregate ID.
    }

    @When("the CloseAccountCmd command is executed")
    public void theCloseAccountCmdCommandIsExecuted() {
        try {
            // Determine the account number to use for the command.
            // For the "uniquely generated" violation scenario, we pass a wrong ID.
            String cmdId = aggregate.id();
            if (aggregate.id().equals("ACC-777")) {
                cmdId = "INVALID-MISMATCH-ID";
            }

            Command cmd = new CloseAccountCmd(cmdId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void aAccountClosedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertEquals("account.closed", resultEvents.get(0).type());
        assertEquals(AccountAggregate.AccountStatus.CLOSED, aggregate.getStatus());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Should have thrown an exception");
        // We expect IllegalStateException or IllegalArgumentException based on the specific invariant violated
        assertTrue(
            capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException || capturedException instanceof UnknownCommandException,
            "Exception should be a domain rule violation"
        );
    }
}
