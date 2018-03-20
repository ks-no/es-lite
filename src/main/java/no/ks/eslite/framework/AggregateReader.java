package no.ks.eslite.framework;

import java.util.UUID;

public interface AggregateReader {
    Aggregate read(UUID aggregateId, Aggregate aggregate);
}
