package no.ks.eslite.framework;


import java.util.List;
import java.util.UUID;

public interface Command<STATE extends Aggregate> {
    List<Event> execute(STATE state);
    UUID getAggregateId();
    String getAggregateType();
    STATE getAggregate();
}
