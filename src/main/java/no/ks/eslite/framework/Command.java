package no.ks.eslite.framework;

import java.util.UUID;

public interface Command {
    UUID getAggregateId();

    String getAggregateType();
    Aggregate getAggregate();
}
