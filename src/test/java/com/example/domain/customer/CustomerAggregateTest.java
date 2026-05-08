package com.example.domain.customer;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.EnrollCustomerCmd;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CustomerAggregateTest {
    @Test
    void testEnrollCustomerSuccess() {
        CustomerAggregate aggregate = new CustomerAggregate("c1");
        var events = aggregate.execute(new EnrollCustomerCmd("c1", "John Doe", "john@example.com", "ID123"));
        assertFalse(events.isEmpty());
        assertTrue(aggregate.isEnrolled());
    }
}