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
@EventType("StartDatePushedBack")
public class StartDateChangedEvent extends EmployeeEventType {
    private UUID aggregateId;
    private LocalDate newStartDate;
}
