package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.CustomerDetailsUpdatedEvent;
import com.example.domain.customer.model.UpdateCustomerDetailsCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

/**
 * Story-specific step definitions for S-3 (UpdateCustomerDetailsCmd).
 * Shared Customer Given/Then steps live in {@link CustomerSharedSteps};
 * scenario state is shared via {@link CustomerSharedContext}.
 */
public class S3Steps {

    private final CustomerSharedContext ctx;
    private final ScenarioContext sc;

    public S3Steps(CustomerSharedContext ctx, ScenarioContext sc) {
        this.ctx = ctx;
        this.sc = sc;
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theUpdateCustomerDetailsCmdCommandIsExecuted() {
        try {
            CustomerAggregate agg = ctx.repository.findById(ctx.aggregate.id()).orElse(ctx.aggregate);
            agg.execute(new UpdateCustomerDetailsCmd(agg.id(), "newmail@example.com", "12-34-56"));
            ctx.repository.save(agg);
        } catch (Throwable t) {
            ctx.thrownException = t;
            sc.thrownException = t;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void aCustomerDetailsUpdatedEventIsEmitted() {
        Assertions.assertNull(ctx.thrownException, "Expected no error, but got: " + ctx.thrownException);
        CustomerAggregate updated = ctx.repository.findById(ctx.aggregate.id()).orElseThrow();
        List<DomainEvent> events = updated.uncommittedEvents();
        Assertions.assertFalse(events.isEmpty(), "Expected events to be emitted");
        DomainEvent event = events.get(0);
        Assertions.assertEquals("customer.details.updated", event.type());
        Assertions.assertInstanceOf(CustomerDetailsUpdatedEvent.class, event);
    }
}
