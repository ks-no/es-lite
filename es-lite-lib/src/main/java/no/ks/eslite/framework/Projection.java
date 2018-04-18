package no.ks.eslite.framework;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
public abstract class Projection {
    private Map<String, Consumer<Event>> projectors = HashMap.empty();
    private Runnable onLive;

    final public Option<Runnable> getOnLive(){
        return Option.of(onLive);
    }

    final public Map<String, Consumer<Event>> getProjectors() {
        return projectors;
    }

    @SuppressWarnings("unchecked")
    protected  <T extends Event> void project(Class<T> eventClass, Consumer<T> consumer){
        projectors = projectors.put(eventClass.getAnnotation(EventType.class).value(), (e) -> {
            log.info("Event {} on aggregate {} received by projection {}", e.getClass().getAnnotation(EventType.class), e.getAggregateId(), getClass().getSimpleName());
            consumer.accept((T) e);
        });
    }

    protected void onLive(Runnable onLive){
        this.onLive = onLive;
    }

}
