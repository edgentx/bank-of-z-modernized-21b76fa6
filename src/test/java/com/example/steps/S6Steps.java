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

public class S6Steps {

    private AccountAggregate aggregate;
    private String accountNumber;
    private String newStatus;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        this.aggregate = new AccountAggregate("acc-123", AccountType.SAVINGS, "ACTIVE", new BigDecimal("100.00"));
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesMinimumBalance() {
        // Savings min balance is 100.00. Creating with 50.00 implies a violation or a state that would trigger rejection if we tried to close it.
        // For this command, the rejection is usually based on the Action. UpdateStatus is a transition.
        // If we try to freeze/close an account below min balance, it might be rejected.
        // We will set up the aggregate such that a check fails.
        this.aggregate = new AccountAggregate("acc-min", AccountType.SAVINGS, "ACTIVE", new BigDecimal("50.00"));
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesActiveStatus() {
        // Creating an account that is already FROZEN
        this.aggregate = new AccountAggregate("acc-frozen", AccountType.SAVINGS, "FROZEN", new BigDecimal("500.00"));
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutableAccountNumber() {
        // The command provides an accountNumber that must match the aggregate's ID or be validated.
        // If the command attempts to change the number, it's a violation.
        // We will test by providing a mismatched account number in the 'When' step, but here we init the aggregate.
        this.aggregate = new AccountAggregate("acc-123", AccountType.SAVINGS, "ACTIVE", new BigDecimal("100.00"));
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        this.accountNumber = "acc-123";
    }

    @And("a valid newStatus is provided")
    public void aValidNewStatusIsProvided() {
        this.newStatus = "FROZEN";
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void theUpdateAccountStatusCmdCommandIsExecuted() {
        // Context for violation scenarios
        if (aggregate.getAccountNumber().equals("acc-min")) {
            // Scenario 2: Trying to close an account with insufficient balance
            this.accountNumber = "acc-min";
            this.newStatus = "CLOSED";
        } else if (aggregate.getAccountNumber().equals("acc-frozen")) {
            // Scenario 3: Trying to process a withdrawal on a frozen account (simulated via status update)
            // Or rather, trying to move away from ACTIVE? 
            // The prompt says "must be in Active to process...". This command updates status.
            // Perhaps trying to set status to ACTIVE fails? No.
            // Let's assume the command logic handles this.
            this.accountNumber = "acc-frozen";
            this.newStatus = "ACTIVE"; // Trying to unfreeze might be allowed, but let's assume the scenario tests a transition that isn't allowed or implies an invalid state check.
            // Re-reading: "must be in Active... to process". This is an invariant.
            // If we try to update status of an inactive account (maybe to close it?), it might fail.
            this.newStatus = "CLOSED";
        } else if (aggregate.getAccountNumber().equals("acc-123") && "acc-123".equals(aggregate.getAccountNumber())) {
            // Scenario 4: Immutable ID violation
            // We pass a DIFFERENT accountNumber in the command than the aggregate ID
            this.accountNumber = "acc-456"; 
            this.newStatus = "FROZEN";
        }

        try {
            var cmd = new UpdateAccountStatusCmd(this.accountNumber, this.newStatus);
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void aAccountStatusUpdatedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof AccountStatusUpdatedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        // We expect either IllegalState or IllegalArgument depending on the specific invariant
        Assertions.assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}