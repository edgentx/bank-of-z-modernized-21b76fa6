package com.example.domain.customer;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.DeleteCustomerCmd;
import com.example.domain.customer.model.EnrollCustomerCmd;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CustomerAggregateTest {

    @Test
    public void testEnrollCustomerSuccess() {
        var c = new CustomerAggregate("cust-1");
        var cmd = new EnrollCustomerCmd("cust-1", "John Doe", "john@example.com", "GOV123");
        var events = c.execute(cmd);

        Assertions.assertFalse(events.isEmpty());
        Assertions.assertTrue(c.isEnrolled());
        Assertions.assertEquals("John Doe", c.getFullName());
    }

    @Test
    public void testEnrollCustomerThrowsOnInvalidEmail() {
        var c = new CustomerAggregate("cust-1");
        Assertions.assertThrows(IllegalArgumentException.class, () -> c.execute(new EnrollCustomerCmd("cust-1", "John Doe", "invalid-email", "GOV123")));
    }

    @Test
    public void testEnrollCustomerThrowsOnDuplicate() {
        var c = new CustomerAggregate("cust-1");
        c.execute(new EnrollCustomerCmd("cust-1", "John Doe", "john@example.com", "GOV123"));
        Assertions.assertThrows(IllegalStateException.class, () -> c.execute(new EnrollCustomerCmd("cust-1", "Jane Doe", "jane@example.com", "GOV456")));
    }
}
