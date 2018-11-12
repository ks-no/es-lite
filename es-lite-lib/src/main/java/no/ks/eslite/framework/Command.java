package no.ks.eslite.framework;


import java.util.List;
import java.util.UUID;

public interface Command<A extends Aggregate> {
    List<Event> execute(A aggregate);
    UUID getAggregateId();
    A getAggregate();
    default boolean useOptimisticLocking(){
        return true;
    }
}
