package no.ks.eslite.framework.testdomain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import no.ks.eslite.framework.Aggregate;

import java.time.LocalDate;
import java.util.UUID;

@EqualsAndHashCode(callSuper = false)
@Builder(toBuilder = true)
@Value
public class Employee extends Aggregate<Employee, EmployeeEventType> {

    {
        onEvent(HiredEvent.class, (a, e) -> a.toBuilder()
                .aggregateId(e.getAggregateId())
                .startDate(e.getStartDate())
                .build());

        onEvent(StartDateChangedEvent.class, (a, e) -> a.toBuilder()
                .startDate(e.getNewStartDate())
                .build());
    }

    private UUID aggregateId;
    private LocalDate startDate;

    @Override
    public String getAggregateType() {
        return "employee";
    }
}
