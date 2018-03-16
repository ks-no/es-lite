package no.ks.eslite.framework;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;

import java.util.function.BiFunction;

import static java.lang.String.format;

public abstract class Aggregate<T extends AggregateState, EVENT_TYPE extends Event, CMD_TYPE extends Command> {

    private Map<Class<? extends Event>, BiFunction<T, Event, T>> applicators = HashMap.empty();

    private Map<Class<? extends Command>, BiFunction<T, Command, List<Event>>> handlers = HashMap.empty();

    @SuppressWarnings("unchecked")
    protected <E extends EVENT_TYPE> void onEvent(Class<E> eventClass, BiFunction<T, E, T> applicatorFunction){
        applicators = applicators.put(eventClass, (BiFunction<T, Event, T>) applicatorFunction);
    }

    @SuppressWarnings("unchecked")
    protected <C extends CMD_TYPE> void onCmd(Class<C> cmdClass, BiFunction<T, C, List<Event>> handlerFunction){
        handlers = handlers.put(cmdClass, (BiFunction<T, Command, List<Event>>) handlerFunction);
    }

    public abstract T initState();

    public T apply(T state, Event event){
        return applicators
                .get(event.getClass())
                .getOrElseThrow(() -> new RuntimeException(format("No applicator found for event %s", event.getClass().getSimpleName())))
                .apply(state, event);
    }

    public List<Event> handle(T state, Command cmd){
        return handlers
                .get(cmd.getClass())
                .getOrElseThrow(() -> new RuntimeException(format("No handler found for event %s", cmd.getClass().getSimpleName())))
                .apply(state, cmd);
    }
}
