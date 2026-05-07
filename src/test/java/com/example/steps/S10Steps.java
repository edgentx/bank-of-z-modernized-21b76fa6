package com.example.steps;

import com.example.domain.PostDepositCmd;
import com.example.domain.Transaction;
import com.example.domain.TransactionId;
import com.example.domain.Money;
import com.example.domain.AccountNumber;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class S10Steps {

    private Transaction transaction;
    private PostDepositCmd command;
    private Exception domainException;

    @Given("a valid Transaction aggregate")
    public void a_valid_transaction_aggregate() {
        transaction = new Transaction(TransactionId.generate());
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // We will construct the command object in the 'When' clause or store fields here
        // For simplicity in this structure, we construct the command in the When block
        // or use defaults.
        if (this.command == null) {
            this.command = new PostDepositCmd(AccountNumber.of("123456789"), Money.of(new BigDecimal("100.00"), "USD"));
        } else {
            // Update existing command if needed, though normally we'd build it step by step
        }
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        if (this.command == null) {
            this.command = new PostDepositCmd(AccountNumber.of("123456789"), Money.of(new BigDecimal("100.00"), "USD"));
        }
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        if (this.command == null) {
            this.command = new PostDepositCmd(AccountNumber.of("123456789"), Money.of(new BigDecimal("100.00"), "USD"));
        }
    }

    @When("the PostDepositCmd command is executed")
    public void the_post_deposit_cmd_command_is_executed() {
        try {
            // If command is null (positive path), create it.
            if (this.command == null) {
                this.command = new PostDepositCmd(AccountNumber.of("123456789"), Money.of(new BigDecimal("100.00"), "USD"));
            }
            transaction.execute(this.command);
        } catch (IllegalArgumentException | IllegalStateException e) {
            this.domainException = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void a_deposit_posted_event_is_emitted() {
        assertNotNull(transaction.getUncommittedEvents());
        assertFalse(transaction.getUncommittedEvents().isEmpty());
        assertTrue(transaction.getUncommittedEvents().stream()
                .anyMatch(e -> "deposit.posted".equals(e.getEventType())));
    }

    // --- Negative Scenarios ---

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_transaction_aggregate_that_violates_transaction_amounts_must_be_greater_than_zero() {
        transaction = new Transaction(TransactionId.generate());
        // Prepare command with invalid amount
        this.command = new PostDepositCmd(
                AccountNumber.of("123456789"),
                Money.of(new BigDecimal("-50.00"), "USD") // Negative amount
        );
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_transaction_aggregate_that_violates_transactions_cannot_be_altered_or_deleted_once_posted() {
        // Create a transaction
        transaction = new Transaction(TransactionId.generate());
        // Post an initial valid transaction
        transaction.execute(new PostDepositCmd(AccountNumber.of("123456789"), Money.of(new BigDecimal("100.00"), "USD")));
        transaction.markEventsAsCommitted();

        // Try to modify (re-execute) the same transaction logic on the same aggregate instance
        this.command = new PostDepositCmd(AccountNumber.of("123456789"), Money.of(new BigDecimal("200.00"), "USD"));
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_transaction_aggregate_that_violates_a_transaction_must_result_in_a_valid_account_balance() {
        transaction = new Transaction(TransactionId.generate());
        // Assume the domain logic validates against a cap or specific balance rules
        // We set up the command to trigger this validation failure
        // For example, depositing a massive amount that violates a "MaxBalance" rule
        this.command = new PostDepositCmd(
                AccountNumber.of("123456789"),
                Money.of(new BigDecimal("99999999999.00"), "USD") // Exceeds hypothetical max
        );
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(domainException, "Expected domain exception to be thrown");
        assertTrue(domainException instanceof IllegalArgumentException || domainException instanceof IllegalStateException);
    }
}
