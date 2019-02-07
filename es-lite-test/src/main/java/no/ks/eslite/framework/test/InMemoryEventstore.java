package no.ks.eslite.framework.test;

import io.vavr.collection.List;
import no.ks.eslite.framework.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class InMemoryEventstore {
    private final AtomicLong eventCounter = new AtomicLong(0);
    private final Map<UUID, List<EventWrapper<Event>>> aggregates = new HashMap<>();
    private final Set<Projection> projections = new HashSet<>();

    private final Consumer<Long> hwmConsumer;

    public InMemoryEventstore(long hwm, Consumer<Long> hwmConsumer) {
        eventCounter.set(hwm);
        this.hwmConsumer = hwmConsumer;
    }

    public InMemoryEventstore() {
        hwmConsumer = hwm -> {};
    }

    public <T extends Projection> T registerProjection(T projection) {
        register(projection);
        return projection;
    }

    public InMemoryEventstore register(Projection projection) {
        projections.add(projection);
        aggregates.values().stream()
                .map(List::asJava)
                .flatMap(Collection::stream)
                .forEach(e -> projections.forEach(p -> p.accept(e)));
        projections.forEach(p -> p.getOnLive().forEach(Runnable::run));
        return this;
    }

    public java.util.List<EventWrapper<Event>> getEvents(UUID aggregateId) {
        return aggregates.getOrDefault(aggregateId, List.empty()).asJava();
    }

    public Set<UUID> getAggregateIds() {
        return aggregates.keySet();
    }

    public <T extends Aggregate> T hydrateAggregate(UUID aggregateId, T aggregate) {
        return (T) read(aggregateId, aggregate);
    }

    public <T extends Aggregate> CmdHandler<T> getCmdHandler() {
        return new CmdHandler<>(this::write, this::read);
    }

    private Aggregate read(UUID aggregateId, Aggregate aggregate) {
        return aggregates.getOrDefault(aggregateId, List.empty())
                .foldLeft(aggregate, (a, e) -> a.apply(a, e.getEvent(), e.getEventNumber()));
    }

    private void write(String aggregateType, UUID aggregateId, Long expectedEventNumber, List<Event> events, boolean useOptimisticLocking) {
        List<EventWrapper<Event>> eventWrappers = events.map(e -> EventWrapper.builder().event(e).eventNumber(eventCounter.getAndIncrement()).build());
        eventWrappers.map(EventWrapper::getEventNumber).forEach(hwmConsumer);

        aggregates.put(aggregateId, aggregates.getOrDefault(aggregateId, List.empty()).appendAll(eventWrappers));
        projections.forEach(projection -> eventWrappers.forEach(projection::accept));
    }
}
