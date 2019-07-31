package no.ks.eslite.framework;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import lombok.NonNull;

import java.util.function.BiFunction;

public abstract class Aggregate<AGGREGATE extends Aggregate, EVENT_TYPE extends Event> {

    private Map<String, BiFunction<Aggregate, Event, Aggregate>> applicators = HashMap.empty();
    private long currentEventNumber;

    @SuppressWarnings("unchecked")
    protected <E extends EVENT_TYPE> void onEvent(Class<E> eventClass, BiFunction<AGGREGATE, E, AGGREGATE> applicatorFunction) {
        applicators = applicators.put(
                EventUtil.getEventType(eventClass),
                (a, e) -> applicatorFunction.apply((AGGREGATE) a, EventUpgrader.upgradeTo(e, eventClass))
        );
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

    public Aggregate apply(@NonNull final Aggregate currentState, @NonNull final Event event, final long eventNumber) {
        return applicators.get(EventUtil.getEventType(event.getClass()))
                .map(p -> p.apply(currentState, event))
                .getOrElse(currentState)
                .withCurrentEventNumber(eventNumber);
    }

}
