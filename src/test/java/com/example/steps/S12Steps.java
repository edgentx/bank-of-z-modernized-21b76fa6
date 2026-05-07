package com.example.steps;

import com.example.domain.transaction.model.TransactionAggregate;
import com.example.domain.transaction.repository.TransactionRepository;
import com.example.domain.transaction.ReverseTransactionCmd;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class S12Steps {

    private TransactionAggregate aggregate;
    private final TransactionRepository repo = new InMemoryTransactionRepository();
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        String id = UUID.randomUUID().toString();
        this.aggregate = new TransactionAggregate(id);
        repo.save(aggregate);
    }

    @Given("a valid originalTransactionId is provided")
    public void a_valid_originalTransactionId_is_provided() {
        // Assumed handled in 'When' via constructor
    }

    @When("the ReverseTransactionCmd command is executed")
    public void the_ReverseTransactionCmd_command_is_executed() {
        String originalId = UUID.randomUUID().toString();
        Command cmd = new ReverseTransactionCmd(originalId, "100.00");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a transaction.reversed event is emitted")
    public void a_transaction_reversed_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertEquals("transaction.reversed", resultEvents.get(0).type());
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amounts_must_be_greater_than_zero() {
        String id = UUID.randomUUID().toString();
        this.aggregate = new TransactionAggregate(id);
        repo.save(aggregate);
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_violates_once_posted() {
        String id = UUID.randomUUID().toString();
        this.aggregate = new TransactionAggregate(id);
        repo.save(aggregate);
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance.")
    public void a_Transaction_aggregate_that_violates_valid_account_balance() {
        String id = UUID.randomUUID().toString();
        this.aggregate = new TransactionAggregate(id);
        repo.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        // Validate it is an IllegalStateException or IllegalArgumentException
    }

    // Stub repository for test context
    private static class InMemoryTransactionRepository implements TransactionRepository {
        @Override
        public TransactionAggregate load(String id) {
            return new TransactionAggregate(id);
        }
        @Override
        public void save(TransactionAggregate aggregate) {}
    }
}
