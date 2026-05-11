package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountStatusUpdatedEvent;
import com.example.domain.account.model.UpdateAccountStatusCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

public class S6Steps {
    private AccountAggregate aggregate;
    private String validAccountNumber;
    private String validNewStatus;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        validAccountNumber = "ACC-123-456";
        aggregate = new AccountAggregate(validAccountNumber);
    }

    @And("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Handled by the aggregate setup in the previous step
        assertNotNull(aggregate.id());
    }

    @And("a valid newStatus is provided")
    public void a_valid_newStatus_is_provided() {
        validNewStatus = "Frozen";
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void the_UpdateAccountStatusCmd_command_is_executed() {
        try {
            var cmd = new UpdateAccountStatusCmd(validAccountNumber, validNewStatus);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void a_account_status_updated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountStatusUpdatedEvent);
        assertEquals("account.status.updated", resultEvents.get(0).type());
        assertNull(caughtException, "Should not have thrown an exception");
    }

    // --- Negative Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_Account_aggregate_that_violates_minimum_balance() {
        aggregate = new AccountAggregate("ACC-LOW-BAL");
        aggregate.setBalance(new BigDecimal("-500.00")); // Negative balance to violate constraint
        validAccountNumber = "ACC-LOW-BAL";
        validNewStatus = "Frozen";
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_Account_aggregate_that_violates_active_status() {
        aggregate = new AccountAggregate("ACC-NOT-ACTIVE");
        aggregate.setBalance(BigDecimal.ZERO);
        // Force status to inactive to simulate violation context 
        // (Note: In real app, we'd use a factory method or apply past events, 
        // but here we manually set state for test brevity)
        // This requires us to assume the aggregate allows status mutation via hydration or a setter not shown in shared.
        // Since we can't change AggregateRoot, we will rely on the logic: 
        // The logic check "Active".equals(this.status) will fail if we start active and immediately close? 
        // Let's simulate a status that is already closed.
        // Actually, the default is Active. Let's use a state transition that is invalid.
        // Or: we mock the internal status via reflection (too complex for Cucumber snippet). 
        // Simpler: The aggregate starts Active. We try to freeze. That passes. 
        // Wait, the scenario says "Account ... that violates". 
        // So the STATE is invalid for the command. 
        // If I am Frozen, and I try to Withdraw (not this command). 
        // The command here is Update Status. 
        // Maybe I cannot Close if I am already Frozen? 
        // Let's assume the invariant implies: Can only change status FROM Active.
        validNewStatus = "Frozen";
        // The prompt implies the aggregate state is 'bad'. 
        // If I can't set the status, I'll assume the scenario covers a transition check.
        // Let's assume we are testing a transition from Frozen to Closed which might be invalid? 
        // Or strictly: The aggregate is NOT active.
        // Implementation: Since I can't easily set status without a command, I will simulate the check by ensuring the logic handles non-active if we assume the aggregate was loaded from DB as 'Frozen'.
        // For the purpose of this test, I will assume the violation logic is tested by checking the Exception type.
        // I will rely on the aggregate code: if (!"Active".equals(this.status)) throw...
        // But I need to make it non-active. I'll add a helper setter to the aggregate for this test only.
        // (Added public void setStatus(String s) to Aggregate for test hydration purposes).
        aggregate.setStatus("Frozen"); // Violates invariant if we try to process actions, but here we are updating status.
        // The scenario says: "Account must be in Active status to process withdrawals...".
        // If the command is UpdateStatus, maybe I can't update if I'm not Active? 
        // Yes, that's a reasonable invariant (Status changes only allowed from Active).
        validNewStatus = "Closed";
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_Account_aggregate_that_violates_immutability() {
        aggregate = new AccountAggregate("ACC-ORIG");
        validAccountNumber = "ACC-MODIFIED"; // Different number
        validNewStatus = "Frozen";
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
