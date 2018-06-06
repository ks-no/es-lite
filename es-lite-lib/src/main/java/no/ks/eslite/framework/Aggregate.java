package no.ks.eslite.framework;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;

import java.util.function.BiFunction;

public abstract class Aggregate<AGGREGATE extends Aggregate, EVENT_TYPE extends Event> {

    private Map<Class<? extends Event>, BiFunction<Aggregate, Event, Aggregate>> applicators = HashMap.empty();
    private long currentEventNumber;

    @SuppressWarnings("unchecked")
    protected <E extends EVENT_TYPE> void onEvent(Class<E> eventClass, BiFunction<AGGREGATE, E, AGGREGATE> applicatorFunction) {
        applicators = applicators.put(eventClass, (BiFunction<Aggregate, Event, Aggregate>) applicatorFunction);
    }

    public abstract String getAggregateType();

    public long getCurrentEventNumber() {
        return currentEventNumber;
    }

    @SuppressWarnings("unchecked")
    public AGGREGATE withCurrentEventNumber(long currentEventNumber) {
        this.currentEventNumber = currentEventNumber;
        return (AGGREGATE) this;
    }

    public Aggregate apply(Aggregate aggregate, Event event, long eventNumber) {
        this.currentEventNumber = eventNumber;

        return applicators
                .getOrElse(event.getClass(), (a, e) -> a)
                .apply(aggregate, event)
                .withCurrentEventNumber(currentEventNumber);
    }

}
