package no.ks.eslite.framework;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
public abstract class Projection {
    private Map<String, Consumer<EventWrapper>> projectors = HashMap.empty();
    private Runnable onLive;

    final public Option<Runnable> getOnLive(){
        return Option.of(onLive);
    }

    final public Map<String, Consumer<EventWrapper>> getProjectors() {
        return projectors;
    }

    protected <T extends Event> void project(Class<T> eventClass, Consumer<T> consumer) {
        projectWrapper(eventClass, e -> consumer.accept(e.getEvent()));
    }

    @SuppressWarnings("unchecked")
    protected <T extends Event> void projectWrapper(Class<T> eventClass, Consumer<EventWrapper<T>> consumer) {
        projectors = projectors.put(EventUtil.getEventType(eventClass), e -> {
            log.info("Event {} on aggregate {} received by projection {}", EventUtil.getEventType(eventClass), e.getEvent().getAggregateId(), getClass().getSimpleName());
            consumer.accept((EventWrapper<T>)e);
        });
    }

    public <T extends Event> void accept(EventWrapper wrapper){
        projectors
                .filter(p -> p._1.equals(EventUtil.getEventType(wrapper.getEvent().getClass())))
                .map(p -> p._2)
                .forEach(p -> p.accept(wrapper));
    }

    protected void onLive(Runnable onLive){
        this.onLive = onLive;
    }

}
