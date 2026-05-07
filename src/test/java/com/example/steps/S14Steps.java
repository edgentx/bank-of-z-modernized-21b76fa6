package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.transaction.model.CompleteTransferCmd;
import com.example.domain.transaction.model.TransferAggregate;
import com.example.domain.transaction.model.TransferCompletedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S14Steps {

    private TransferAggregate aggregate;
    private CompleteTransferCmd command;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid Transfer aggregate")
    public void aValidTransferAggregate() {
        aggregate = new TransferAggregate("TX-123");
    }

    @And("a valid transferReference is provided")
    public void aValidTransferReferenceIsProvided() {
        // Setup valid command details
        command = new CompleteTransferCmd(
                "TX-123",
                "ACC-100",
                "ACC-200",
                new BigDecimal("100.00"),
                new BigDecimal("500.00"),
                true // Atomic state valid
        );
    }

    @Given("a Transfer aggregate that violates: Source and destination accounts cannot be the same.")
    public void aTransferAggregateThatViolatesSourceAndDestinationAccountsCannotBeTheSame() {
        aggregate = new TransferAggregate("TX-FAIL-SAME");
        command = new CompleteTransferCmd(
                "TX-FAIL-SAME",
                "ACC-100",
                "ACC-100", // Same account
                new BigDecimal("50.00"),
                new BigDecimal("1000.00"),
                true
        );
    }

    @Given("a Transfer aggregate that violates: Transfer amount must not exceed the available balance of the source account.")
    public void aTransferAggregateThatViolatesTransferAmountMustNotExceedTheAvailableBalanceOfTheSourceAccount() {
        aggregate = new TransferAggregate("TX-FAIL-NSF");
        command = new CompleteTransferCmd(
                "TX-FAIL-NSF",
                "ACC-100",
                "ACC-200",
                new BigDecimal("1000.00"), // Amount > Balance
                new BigDecimal("50.00"),   // Balance
                true
        );
    }

    @Given("a Transfer aggregate that violates: A transfer must succeed or fail atomically for both accounts involved.")
    public void aTransferAggregateThatViolatesATransferMustSucceedOrFailAtomicallyForBothAccountsInvolved() {
        aggregate = new TransferAggregate("TX-FAIL-ATOM");
        command = new CompleteTransferCmd(
                "TX-FAIL-ATOM",
                "ACC-100",
                "ACC-200",
                new BigDecimal("50.00"),
                new BigDecimal("100.00"),
                false // Atomicity check failed
        );
    }

    @When("the CompleteTransferCmd command is executed")
    public void theCompleteTransferCmdCommandIsExecuted() {
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a transfer.completed event is emitted")
    public void aTransferCompletedEventIsEmitted() {
        Assertions.assertNull(thrownException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should have emitted exactly one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof TransferCompletedEvent, "Event should be TransferCompletedEvent");

        TransferCompletedEvent event = (TransferCompletedEvent) resultEvents.get(0);
        Assertions.assertEquals("transfer.completed", event.type());
        Assertions.assertEquals("TX-123", event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException, "Expected an exception to be thrown");
        Assertions.assertTrue(
                thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException,
                "Exception should be a domain logic violation (IllegalArgumentException or IllegalStateException)"
        );
        Assertions.assertTrue(aggregate.uncommittedEvents().isEmpty(), "No events should be recorded on failure");
    }
}
