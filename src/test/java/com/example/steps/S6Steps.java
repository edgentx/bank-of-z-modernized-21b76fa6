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
    private String accountNumber;
    private String newStatus;
    private List<DomainEvent> events;
    private Exception exception;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        accountNumber = "ACC-123";
        aggregate = new AccountAggregate(accountNumber);
    }

    @Given("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // accountNumber already initialized in aValidAccountAggregate
    }

    @Given("a valid newStatus is provided")
    public void aValidNewStatusIsProvided() {
        newStatus = "Frozen";
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void theUpdateAccountStatusCmdCommandIsExecuted() {
        try {
            UpdateAccountStatusCmd cmd = new UpdateAccountStatusCmd(accountNumber, newStatus);
            events = aggregate.execute(cmd);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void aAccountStatusUpdatedEventIsEmitted() {
        Assertions.assertNull(exception);
        Assertions.assertNotNull(events);
        Assertions.assertEquals(1, events.size());
        Assertions.assertEquals("account.status.updated", events.get(0).type());
        Assertions.assertEquals(accountNumber, events.get(0).aggregateId());
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesBalanceConstraint() {
        aggregate = new AccountAggregate("ACC-LOW");
        aggregate.setAccountType("Savings");
        aggregate.setBalance(new BigDecimal("50.00")); // Min is 100.00
        accountNumber = "ACC-LOW";
        newStatus = "Closed";
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception instanceof IllegalStateException);
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesActiveStatusConstraint() {
        aggregate = new AccountAggregate("ACC-INACTIVE");
        aggregate.setStatus("Frozen");
        accountNumber = "ACC-INACTIVE";
        newStatus = "Closed"; // Logic checks if current status allows closing (simulating active check)
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutableNumber() {
        aggregate = new AccountAggregate("ACC-ORIGINAL");
        accountNumber = "ACC-IMMUTABLE-HACK"; // Mismatched number
        newStatus = "Frozen";
    }
}
