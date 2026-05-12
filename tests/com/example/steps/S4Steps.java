package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.DeleteCustomerCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

/**
 * Story-specific step definitions for S-4 (DeleteCustomerCmd).
 * Shared Customer Given/Then steps live in {@link CustomerSharedSteps};
 * scenario state is shared via {@link CustomerSharedContext}.
 */
public class S4Steps {

    private final CustomerSharedContext ctx;
    private final ScenarioContext sc;

    public S4Steps(CustomerSharedContext ctx, ScenarioContext sc) {
        this.ctx = ctx;
        this.sc = sc;
    }

    @When("the DeleteCustomerCmd command is executed")
    public void theDeleteCustomerCmdCommandIsExecuted() {
        try {
            CustomerAggregate agg = ctx.repository.findById(ctx.aggregate.id()).orElseThrow();
            boolean hasActiveAccounts = "cust-active".equals(agg.id());
            agg.execute(new DeleteCustomerCmd(agg.id(), hasActiveAccounts));
            ctx.repository.save(agg);
        } catch (Throwable t) {
            ctx.thrownException = t;
            sc.thrownException = t;
        }
    }

    @Then("a customer.deleted event is emitted")
    public void aCustomerDeletedEventIsEmitted() {
        Assertions.assertNull(ctx.thrownException, "Expected no error, but got: " + ctx.thrownException);
        CustomerAggregate updated = ctx.repository.findById(ctx.aggregate.id()).orElseThrow();
        List<DomainEvent> events = updated.uncommittedEvents();
        Assertions.assertFalse(events.isEmpty(), "Expected events to be emitted");
        Assertions.assertEquals("customer.deleted", events.get(0).type());
    }
}
