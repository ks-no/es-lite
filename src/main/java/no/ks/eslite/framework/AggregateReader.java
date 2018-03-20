package no.ks.eslite.framework;

import java.util.UUID;

public interface AggregateReader {
    Aggregate read(String aggregateType, UUID aggregateId, Aggregate aggregate);
}
