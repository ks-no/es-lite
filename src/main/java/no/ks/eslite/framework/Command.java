package no.ks.eslite.framework;


import java.util.List;
import java.util.UUID;

public interface Command {
    List<Event> execute(AggregateState state);
    UUID getAggregateId();
    String getAggregateType();
    Aggregate getAggregate();
}
