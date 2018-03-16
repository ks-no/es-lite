package no.ks.eslite.framework;

import io.vavr.collection.List;

public interface EventWriter {
    void write(List<Event> events);
}
