package com.example.domain.customer;
import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.CustomerEnrolledEvent;
import com.example.domain.customer.model.EnrollCustomerCmd;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryCustomerRepository;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CustomerAggregateTest {
  @Test void enrollHappyPathEmitsEvent() {
    var c = new CustomerAggregate("cust-1");
    List<DomainEvent> events = c.execute(new EnrollCustomerCmd("cust-1", "Jane Doe", "jane@example.com", "GOV123"));
    assertEquals(1, events.size());
    assertInstanceOf(CustomerEnrolledEvent.class, events.get(0));
    assertTrue(c.isEnrolled());
    assertEquals(1, c.getVersion());
  }
  @Test void enrollRejectsBlankFullName() {
    var c = new CustomerAggregate("cust-2");
    assertThrows(IllegalArgumentException.class, () -> c.execute(new EnrollCustomerCmd("cust-2", "", "x@x.com", "GOV")));
  }
  @Test void enrollRejectsInvalidEmail() {
    var c = new CustomerAggregate("cust-3");
    assertThrows(IllegalArgumentException.class, () -> c.execute(new EnrollCustomerCmd("cust-3", "Name", "no-at", "GOV")));
  }
  @Test void enrollTwiceRejected() {
    var c = new CustomerAggregate("cust-4");
    c.execute(new EnrollCustomerCmd("cust-4", "Name", "n@n.com", "GOV"));
    assertThrows(IllegalStateException.class, () -> c.execute(new EnrollCustomerCmd("cust-4", "Other", "o@o.com", "GOV2")));
  }
  @Test void mockRepositoryRoundTrip() {
    var repo = new InMemoryCustomerRepository();
    var c = new CustomerAggregate("cust-5");
    c.execute(new EnrollCustomerCmd("cust-5", "Sample", "s@s.com", "GOV5"));
    repo.save(c);
    assertEquals("Sample", repo.findById("cust-5").orElseThrow().getFullName());
  }
}
