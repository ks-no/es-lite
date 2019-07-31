package no.ks.eslite.framework;

import java.util.Optional;

public class EventUtil {

    public static String getEventType(Class<? extends Event> aggEvent) {
        return Optional.ofNullable(aggEvent.getAnnotation(EventType.class)).orElseThrow(() -> new RuntimeException(String.format("The event %s is not annotated with @EventType", aggEvent.getName()))).value();
    }
}
