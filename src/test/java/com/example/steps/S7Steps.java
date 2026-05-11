package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.account.model.AccountClosedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S7Steps {

    private AccountAggregate account;
    private CloseAccountCmd command;
    private List<DomainEvent> resultingEvents;
    private Exception thrownException;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        account = new AccountAggregate("ACC-VALID-001", BigDecimal.ZERO);
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Command is created with the valid account number from the aggregate
        // (Handled in the 'When' step to ensure we use the correct aggregate context)
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_balance() {
        account = new AccountAggregate("ACC-INVALID-BAL", new BigDecimal("100.50"));
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_status() {
        account = new AccountAggregate("ACC-INVALID-STATUS", BigDecimal.ZERO);
        account.setStatus(AccountAggregate.Status.CLOSED); // Force inactive state
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_immutability() {
        // The aggregate itself is valid, but the command used later will attempt to use a different number
        account = new AccountAggregate("ACC-ORIG-001", BigDecimal.ZERO);
    }

    @When("the CloseAccountCmd command is executed")
    public void the_close_account_cmd_command_is_executed() {
        try {
            // If we are testing the immutability violation, use a mismatched ID
            if (account != null && "ACC-ORIG-001".equals(account.getAccountNumber()) && account.getStatus() == AccountAggregate.Status.ACTIVE && account.getBalance().compareTo(BigDecimal.ZERO) == 0) {
                command = new CloseAccountCmd("ACC-FAKE-001");
            } else {
                // Otherwise use the correct ID from the aggregate
                command = new CloseAccountCmd(account.getAccountNumber());
            }
            
            resultingEvents = account.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof AccountClosedEvent);
        assertEquals("account.closed", resultingEvents.get(0).type());
        assertEquals(account.getAccountNumber(), resultingEvents.get(0).aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
