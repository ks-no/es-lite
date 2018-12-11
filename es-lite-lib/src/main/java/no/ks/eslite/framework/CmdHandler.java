package no.ks.eslite.framework;

import io.vavr.collection.List;

public class CmdHandler<T extends Aggregate> {

    private final AggregateReader reader;
    private final EventWriter writer;

    public CmdHandler(EventWriter writer, AggregateReader reader) {
        this.reader = reader;
        this.writer = writer;
    }

    @SuppressWarnings("unchecked")
    public T handle(Command<T> cmd) {
        T aggregate = (T) reader.read(cmd.getAggregateId(), cmd.getAggregate());
        List<Event> events = List.ofAll(cmd.execute(aggregate));
        writer.write(aggregate.getAggregateType(), cmd.getAggregateId(), aggregate.getCurrentEventNumber(), events, cmd.useOptimisticLocking());
        return events.foldLeft(aggregate, (a, e) -> (T) a.apply(a, e, Long.MIN_VALUE));
    }
}
