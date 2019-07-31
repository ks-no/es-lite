package no.ks.eslite.esjc.testdomain;

import no.ks.eslite.framework.Projection;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StartDatesProjection extends Projection {
    private Map<UUID, LocalDate> startDates = new HashMap<>();

    {
        project(HiredEvent.class, (e) -> startDates.put(e.getAggregateId(), e.getStartDate()));
    }

    public LocalDate getStartDate(UUID aggregateId){
        return startDates.get(aggregateId);
    }
}
