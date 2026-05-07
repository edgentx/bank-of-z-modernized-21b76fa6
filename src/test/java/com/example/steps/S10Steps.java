package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public class S10Steps {

    private static final String VALID_CURRENCY = "USD";
    private static final String VALID_ACCOUNT = "ACC-001";

    private Transaction aggregate;
    private DomainConfig config;
    private PostDepositCmd command;
    private Exception thrownException;

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        config = new DomainConfig(BigDecimal.valueOf(1000000), BigDecimal.valueOf(5000));
        aggregate = new Transaction(UUID.randomUUID(), VALID_ACCOUNT, config);
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Handled in context setup, ensuring command uses this if/when created
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // Handled in context setup
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // Handled in context setup
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amounts_greater_than_zero() {
        config = new DomainConfig(BigDecimal.valueOf(1000000), BigDecimal.valueOf(5000));
        aggregate = new Transaction(UUID.randomUUID(), VALID_ACCOUNT, config);
        // The violation will be in the command, not the aggregate state, for amount validation
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted")
    public void a_Transaction_aggregate_that_violates_transactions_cannot_be_altered() {
        config = new DomainConfig(BigDecimal.valueOf(1000000), BigDecimal.valueOf(5000));
        aggregate = new Transaction(UUID.randomUUID(), VALID_ACCOUNT, config);
        
        // Simulate posted state
        PostDepositCmd postCmd = new PostDepositCmd(VALID_ACCOUNT, new BigDecimal("100.00"), Currency.getInstance(VALID_CURRENCY));
        aggregate.execute(postCmd);
        aggregate.markPosted(); // Mutate state to represent persistence
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance")
    public void a_Transaction_aggregate_that_violates_valid_account_balance() {
        // Setup config with a cap that will be exceeded
        config = new DomainConfig(BigDecimal.valueOf(100), BigDecimal.valueOf(5000));
        aggregate = new Transaction(UUID.randomUUID(), VALID_ACCOUNT, config);
        
        // Set current balance high enough that adding would fail if logic checked current balance, 
        // OR just rely on the command amount exceeding the max balance.
        // Assuming aggregate starts at 0, we test the deposit amount > max_balance constraint.
    }

    @When("the PostDepositCmd command is executed")
    public void the_PostDepositCmd_command_is_executed() {
        try {
            // Determine parameters based on scenario context (simplified for this example)
            String scenario = Hooks.currentScenario.get();
            
            String account = VALID_ACCOUNT;
            BigDecimal amount = new BigDecimal("100.00");
            Currency curr = Currency.getInstance(VALID_CURRENCY);

            if (scenario.contains("amounts must be greater than zero")) {
                amount = BigDecimal.ZERO;
            } else if (scenario.contains("valid account balance")) {
                amount = new BigDecimal("200.00"); // > 100 limit configured in Given
            }
            
            // If aggregate is already posted (violating immutable state), attempting to execute again
            // should fail based on aggregate state check.

            command = new PostDepositCmd(account, amount, curr);
            aggregate.execute(command);
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            this.thrownException = e;
        } catch (DomainException e) {
            this.thrownException = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void a_deposit_posted_event_is_emitted() {
        Assertions.assertFalse(aggregate.getEvents().isEmpty());
        Assertions.assertTrue(aggregate.getEvents().get(0) instanceof DepositPostedEvent);
        
        DepositPostedEvent event = (DepositPostedEvent) aggregate.getEvents().get(0);
        Assertions.assertEquals(command.amount(), event.amount());
        Assertions.assertEquals(command.accountNumber(), event.accountNumber());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        // Ideally assert specific message or type based on the invariant
    }

    // Hooks class for context (omitted from single file constraint, but implied)
    static class Hooks {
        static ThreadLocal<String> currentScenario = new ThreadLocal<>();
    }
}
