package no.ks.eslite.framework;

import lombok.NonNull;

import java.util.Optional;

public class EventUtil {

    public static Event upgrade(@NonNull final Event e) {
        Event upgraded = e;
        while (upgraded.upgrade() != null){
            upgraded = upgraded.upgrade();
        }
        return upgraded;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Event> T upgradeTo(@NonNull final Event event, @NonNull final Class<T> targetClass){
        Event upgraded = event;
        while (!upgraded.getClass().isInstance(targetClass) && upgraded.upgrade() != null)
            upgraded = upgraded.upgrade();

        if (upgraded.getClass().isInstance(targetClass))
            throw new RuntimeException(String.format("Attempted to upgrade event %s to %s, but upgrades where exhausted without arriving at the target class", event.getClass().getName(), targetClass.getName()));
        else
            return (T) upgraded;
    }

    public static String getEventType(Class<? extends Event> aggEvent) {
        return Optional.ofNullable(aggEvent.getAnnotation(EventType.class)).orElseThrow(() -> new RuntimeException(String.format("The event %s is not annotated with @EventType", aggEvent.getName()))).value();
    }
}
