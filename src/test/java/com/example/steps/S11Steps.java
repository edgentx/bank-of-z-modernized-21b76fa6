package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class S11Steps {

    private Transaction transaction;
    private S11Command command;
    private S11Event resultingEvent;
    private Exception domainException;

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        // Setup a standard valid transaction state for a new entry
        transaction = new Transaction();
    }

    @And("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Lazy init command if null, or just set field
        if(command == null) command = new S11Command();
        command.setAccountNumber("ACC-1001");
    }

    @And("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        if(command == null) command = new S11Command();
        command.setAmount(new BigDecimal("50.00"));
    }

    @And("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        if(command == null) command = new S11Command();
        command.setCurrency(Currency.getInstance("USD"));
    }

    // --- Violations Setup ---

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amounts_must_be_greater_than_zero() {
        transaction = new Transaction();
        command = new S11Command();
        command.setAccountNumber("ACC-1001");
        command.setAmount(BigDecimal.ZERO); // Violation
        command.setCurrency(Currency.getInstance("USD"));
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_violates_immutability() {
        // Simulate an existing transaction
        transaction = new Transaction();
        // Force a state where it's already posted
        transaction.markPosted(); 
        
        command = new S11Command();
        command.setAccountNumber("ACC-1001");
        command.setAmount(new BigDecimal("10.00"));
        command.setCurrency(Currency.getInstance("USD"));
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_Transaction_aggregate_that_violates_balance_validation() {
        transaction = new Transaction();
        // Simulate current balance is insufficient (e.g. 0)
        transaction.setCurrentBalance(BigDecimal.ZERO);

        command = new S11Command();
        command.setAccountNumber("ACC-1001");
        command.setAmount(new BigDecimal("100.00")); // Overdraft attempt
        command.setCurrency(Currency.getInstance("USD"));
    }

    // --- Action ---

    @When("the PostWithdrawalCmd command is executed")
    public void the_PostWithdrawalCmd_command_is_executed() {
        try {
            resultingEvent = transaction.execute(command);
        } catch (IllegalArgumentException | IllegalStateException e) {
            domainException = e;
        }
    }

    // --- Outcomes ---

    @Then("a withdrawal.posted event is emitted")
    public void a_withdrawal_posted_event_is_emitted() {
        assertNotNull(resultingEvent, "Event should not be null");
        assertEquals("withdrawal.posted", resultingEvent.getType());
        assertEquals(command.getAccountNumber(), resultingEvent.getAccountNumber());
        assertEquals(command.getAmount(), resultingEvent.getAmount());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(domainException, "Expected domain exception to be thrown");
        assertNull(resultingEvent, "Event should be null when command is rejected");
    }
}
