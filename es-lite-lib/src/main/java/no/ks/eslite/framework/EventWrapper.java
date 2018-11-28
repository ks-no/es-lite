package no.ks.eslite.framework;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EventWrapper<T extends Event> {
    private T event;
    private long eventNumber;
}
