package no.ks.eslite.framework;

import io.vavr.collection.List;
import no.ks.eslite.esjc.EsjcStreamIdGenerator;

import java.util.UUID;

public interface AggregateReader {
    List<Event> read(String aggregateType, UUID aggregateId);
}
