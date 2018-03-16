package no.ks.eslite.framework;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;

import java.util.function.Consumer;

public abstract class Projection {
    private static Map<String, Consumer<Event>> projectors = HashMap.empty();

    @SuppressWarnings("unchecked")
    protected static <T extends Event> void project(Class<T> eventClass, Consumer<T> consumer){
        projectors = projectors.put(eventClass.getAnnotation(EventType.class).value(), (Consumer<Event>) consumer);
    }

    final public Map<String, Consumer<Event>> getProjectors() {
        return projectors;
    }
}
