package com.example.steps;

import com.example.domain.statement.model.*;
import com.example.domain.shared.DomainException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class S8Steps {

    private StatementAggregate aggregate;
    private GenerateStatementCmd cmd;
    private List<StatementGeneratedEvent> result;
    private Exception thrownException;

    // Helper to create a fresh aggregate
    private StatementAggregate createAggregate(String id) {
        return new StatementAggregate(id);
    }

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = createAggregate("stmt-1");
        // Assume default state is valid for generation
        aggregate.setOpeningBalance(BigDecimal.ZERO);
        aggregate.setClosed(false);
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // We construct the command later, just store state if needed
    }

    @Given("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // We construct the command later
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = createAggregate("stmt-2");
        aggregate.setOpeningBalance(BigDecimal.TEN);
        aggregate.setClosed(true); // Violation: Already closed
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance() {
        aggregate = createAggregate("stmt-3");
        aggregate.setOpeningBalance(BigDecimal.TEN);
        aggregate.setPreviousClosingBalance(BigDecimal.ZERO); // Violation: Mismatch
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        try {
            String account = "acc-123";
            LocalDate periodEnd = LocalDate.now();
            BigDecimal opening = (aggregate.getOpeningBalance() != null) ? aggregate.getOpeningBalance() : BigDecimal.ZERO;
            BigDecimal prevClosing = (aggregate.getPreviousClosingBalance() != null) ? aggregate.getPreviousClosingBalance() : BigDecimal.ZERO;
            
            cmd = new GenerateStatementCmd(aggregate.id(), account, periodEnd, opening, prevClosing);
            
            var events = aggregate.execute(cmd);
            // Assuming the event type is StatementGeneratedEvent based on naming conventions
            if (!events.isEmpty() && events.get(0) instanceof StatementGeneratedEvent) {
                result = List.of((StatementGeneratedEvent) events.get(0));
            } else {
                result = List.of();
            }
        } catch (DomainException | IllegalStateException | IllegalArgumentException e) {
            thrownException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals("statement.generated", result.get(0).type());
        Assertions.assertEquals(aggregate.id(), result.get(0).aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        // Specific message checks could go here based on the scenario context
    }
}
