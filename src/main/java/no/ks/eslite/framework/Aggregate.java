package no.ks.eslite.framework;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;

import java.util.List;
import java.util.function.BiFunction;

import static java.lang.String.format;

public abstract class Aggregate<AGGREGATE extends Aggregate, EVENT_TYPE extends Event, CMD_TYPE extends Command> {

    private Map<Class<? extends Event>, BiFunction<Aggregate, Event, Aggregate>> applicators = HashMap.empty();

    @SuppressWarnings("unchecked")
    protected <E extends EVENT_TYPE> void onEvent(Class<E> eventClass, BiFunction<AGGREGATE, E, AGGREGATE> applicatorFunction){
        applicators = applicators.put(eventClass, (BiFunction<Aggregate, Event, Aggregate>) applicatorFunction);
    }

    public Aggregate apply(Aggregate state, Event event){
        return applicators
                .get(event.getClass())
                .getOrElseThrow(() -> new RuntimeException(format("No applicator found for event %s", event.getClass().getSimpleName())))
                .apply(state, event);
    }

}
