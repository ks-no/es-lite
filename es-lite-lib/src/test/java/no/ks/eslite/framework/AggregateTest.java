package no.ks.eslite.framework;

import no.ks.eslite.framework.testdomain.Employee;
import no.ks.eslite.framework.testdomain.HiredEvent;
import no.ks.eslite.framework.testdomain.StartDateChangedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AggregateTest {

    @Test
    @DisplayName("Test that the state of the aggregate changes in accordance with the applied event")
    void testApplyEvent() {
        Employee aggregate = Employee.builder().build();
        UUID aggregateId = UUID.randomUUID();
        LocalDate startDate = LocalDate.now();

        aggregate = (Employee) aggregate.apply(aggregate, HiredEvent.builder().aggregateId(aggregateId).startDate(startDate).build(), 0);

        assertEquals(aggregateId, aggregate.getAggregateId());
        assertEquals(startDate, aggregate.getStartDate());
        assertEquals(0, aggregate.getCurrentEventNumber());
    }

    @Test
    @DisplayName("Test that we can apply multiple events")
    void testApplyMultiple() {
        Employee aggregate = Employee.builder().build();
        UUID aggregateId = UUID.randomUUID();
        LocalDate startDate = LocalDate.now();
        LocalDate newStartDate = startDate.plusDays(1);

        aggregate = (Employee) aggregate.apply(aggregate, HiredEvent.builder().aggregateId(aggregateId).startDate(startDate).build(), 0);
        aggregate = (Employee) aggregate.apply(aggregate, StartDateChangedEvent.builder().aggregateId(aggregateId).newStartDate(newStartDate).build(), 1);

        assertEquals(aggregateId, aggregate.getAggregateId());
        assertEquals(newStartDate, aggregate.getStartDate());
        assertEquals(1, aggregate.getCurrentEventNumber());
    }
}