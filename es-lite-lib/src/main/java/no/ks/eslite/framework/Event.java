package no.ks.eslite.framework;

import java.util.UUID;

public interface Event {
    UUID getAggregateId();
    long getTimestamp();
    default Event upgrade(){
        return null;
    }
}
