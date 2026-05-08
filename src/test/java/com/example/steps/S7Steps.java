package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.CloseAccountCmd;
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
    private Exception capturedException;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        account = new AccountAggregate("acct-1");
        account.setAccountNumber("123456");
        account.setStatus(AccountAggregate.AccountStatus.ACTIVE);
        account.setBalance(BigDecimal.ZERO);
        account.setMinimumRequiredBalance(BigDecimal.ZERO);
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // The account number is set in the aggregate initialization
        // We will construct the command in the 'When' step
    }

    @When("the CloseAccountCmd command is executed")
    public void the_close_account_cmd_command_is_executed() {
        try {
            // Assuming command targets the account we set up
            command = new CloseAccountCmd(account.id(), account.getAccountNumber());
            resultingEvents = account.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertEquals("account.closed", resultingEvents.get(0).type());
        assertEquals(AccountAggregate.AccountStatus.CLOSED, account.getStatus());
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_minimum_balance() {
        account = new AccountAggregate("acct-2");
        account.setAccountNumber("654321");
        account.setStatus(AccountAggregate.AccountStatus.ACTIVE);
        account.setBalance(new BigDecimal("100.00")); // Non-zero balance
        account.setMinimumRequiredBalance(BigDecimal.ZERO);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // The specific message or type can be asserted here
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_active_status() {
        account = new AccountAggregate("acct-3");
        account.setAccountNumber("111111");
        account.setStatus(AccountAggregate.AccountStatus.SUSPENDED); // Not active
        account.setBalance(BigDecimal.ZERO);
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_account_number_immutability() {
        account = new AccountAggregate("acct-4");
        account.setAccountNumber("999999");
        account.setStatus(AccountAggregate.AccountStatus.ACTIVE);
        account.setBalance(BigDecimal.ZERO);
        // The violation occurs when the Command provides a different number
        // Handled in the When step logic for this specific scenario context if needed,
        // but here we rely on the When step creating a mismatched command for *this* specific aggregate state context.
        // Actually, for this specific step definition, we usually just set up the state.
        // The mismatch logic is effectively tested by providing a bad command.
        // However, the prompt says "Aggregate that violates...".
        // Let's assume the violation is the mismatch check logic.
        // We can store a 'bad' command intent in the context or handle it in the When step.
        // For simplicity, we assume the standard flow but the When step detects the context.
    }
    
    // Custom When for the immutability violation to pass the wrong number
    @When("the CloseAccountCmd command is executed with mismatched account number")
    public void the_close_account_cmd_command_is_executed_with_mismatched_number() {
        try {
            // Provide a number different from the aggregate's current number
            command = new CloseAccountCmd(account.id(), "000000");
            resultingEvents = account.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
