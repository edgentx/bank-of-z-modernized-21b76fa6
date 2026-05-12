package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.transaction.model.*;
import com.example.domain.transaction.repository.TransactionRepository;
import com.example.mocks.InMemoryTransactionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

/**
 * Step definitions for S-10 (PostDepositCmd). Transaction is single-story,
 * so its aggregate state stays local; the rejection @Then has been moved
 * to {@link CommonSteps} and reads {@link ScenarioContext#thrownException}.
 */
public class S10Steps {

    private final ScenarioContext sc;

    private final TransactionRepository repository = new InMemoryTransactionRepository();
    private TransactionAggregate aggregate;
    private List<DomainEvent> resultEvents;

    private final String validAccountId = "ACC-123-456";
    private final String validCurrency = "USD";
    private final String validTxId = "TX-" + System.currentTimeMillis();
    private final BigDecimal validAmount = new BigDecimal("100.00");

    /**
     * Scenario-tag carried out of @Given to drive @When command parameters.
     * Lets a single @When step service the success, invalid-amount, posted-
     * already, and balance-violation scenarios without duplicate @Given/@When
     * definitions in the glue.
     */
    private enum Mode {VALID, INVALID_AMOUNT, ALREADY_POSTED, BALANCE_VIOLATION}

    private Mode mode = Mode.VALID;

    public S10Steps(ScenarioContext sc) {
        this.sc = sc;
    }

    @Given("a valid Transaction aggregate")
    public void aValidTransactionAggregate() {
        aggregate = new TransactionAggregate(validTxId);
        mode = Mode.VALID;
    }

    // "a valid accountNumber is provided" no-op is supplied by S6Steps and
    // reused here via picocontainer glue (Cucumber would treat two classes
    // owning the same step text as a duplicate-definition error).

    @And("a valid amount is provided")
    public void aValidAmountIsProvided() {
        // Command construction is deferred to @When.
    }

    @And("a valid currency is provided")
    public void aValidCurrencyIsProvided() {
        // Command construction is deferred to @When.
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void aTransactionAggregateWithInvalidAmount() {
        aggregate = new TransactionAggregate(validTxId);
        mode = Mode.INVALID_AMOUNT;
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void aTransactionAggregateThatIsAlreadyPosted() {
        aggregate = new TransactionAggregate(validTxId);
        repository.save(aggregate);
        aggregate.execute(new PostDepositCmd(validTxId, validAccountId, validAmount, validCurrency));
        repository.save(aggregate);
        mode = Mode.ALREADY_POSTED;
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance \\(enforced via aggregate validation).")
    public void aTransactionAggregateThatViolatesBalanceValidation() {
        aggregate = new TransactionAggregate(validTxId);
        mode = Mode.BALANCE_VIOLATION;
    }

    @When("the PostDepositCmd command is executed")
    public void thePostDepositCmdCommandIsExecuted() {
        BigDecimal amount = switch (mode) {
            case INVALID_AMOUNT -> BigDecimal.ZERO;
            case BALANCE_VIOLATION -> new BigDecimal("99999999999.00");
            default -> validAmount;
        };
        PostDepositCmd cmd = new PostDepositCmd(aggregate.id(), validAccountId, amount, validCurrency);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException e) {
            sc.thrownException = e;
        } catch (Exception e) {
            sc.thrownException = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void aDepositPostedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof TransactionPostedEvent);
        TransactionPostedEvent event = (TransactionPostedEvent) resultEvents.get(0);
        Assertions.assertEquals("deposit", event.kind());
        Assertions.assertEquals(validAccountId, event.accountId());
    }
}
