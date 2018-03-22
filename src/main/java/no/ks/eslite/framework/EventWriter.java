package no.ks.eslite.framework;


import io.vavr.collection.List;

import java.util.UUID;

public interface EventWriter {

    void write(String aggregateType, UUID aggregateId, Long expectedEventNumber, List<Event> events);
}
