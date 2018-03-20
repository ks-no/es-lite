package no.ks.eslite.framework;


import java.util.List;

public interface EventWriter {

    void write(String aggregateType, List<Event> events);
}
