package no.ks.eslite.framework;

import no.ks.eslite.framework.testdomain.HiredEvent;
import no.ks.eslite.framework.testdomain.StartDatesProjection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProjectionTest {

    @Test
    @DisplayName("test that a projection can handle incoming events and mutate its state accordingly")
    void name() {
        StartDatesProjection projection = new StartDatesProjection();
        UUID aggregateId = UUID.randomUUID();
        LocalDate startDate = LocalDate.now();

        projection.accept(new EventWrapper<>(HiredEvent.builder().aggregateId(aggregateId).startDate(startDate).build(), 0));

        assertEquals(startDate, projection.getStartDate(aggregateId));
    }
}