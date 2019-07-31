package no.ks.eslite.esjc.testdomain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import no.ks.eslite.framework.Event;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = false)
@Builder
@Value
public class HireCmd extends EmployeeCmd {
    private UUID aggregateId;
    private LocalDate startDate;

    @Override
    public List<Event> execute(Employee aggregate) {
        if (aggregate.getAggregateId() != null)
            throw new IllegalStateException("The employee has already been created!");
        else
            return Collections.singletonList(HiredEvent.builder()
                    .aggregateId(aggregateId)
                    .startDate(startDate)
                    .build());
    }
}
