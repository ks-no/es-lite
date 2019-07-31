package no.ks.eslite.esjc.testdomain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import no.ks.eslite.framework.EventType;

import java.time.LocalDate;
import java.util.UUID;

@EqualsAndHashCode(callSuper = false)
@Value
@Builder
@EventType("Hired")
public class HiredEvent extends EmployeeEventType {
    long timestamp;
    private UUID aggregateId;
    private LocalDate startDate;
}
