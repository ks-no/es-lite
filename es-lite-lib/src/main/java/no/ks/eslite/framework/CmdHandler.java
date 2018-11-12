package no.ks.eslite.framework;

import io.vavr.collection.List;

public class CmdHandler {

    private final AggregateReader reader;
    private final EventWriter writer;

    public CmdHandler(EventWriter writer, AggregateReader reader) {
        this.reader = reader;
        this.writer = writer;
    }

    @SuppressWarnings("unchecked")
    public void handle(Command cmd) {
        Aggregate aggregate = reader.read(cmd.getAggregateId(), cmd.getAggregate());
        writer.write(aggregate.getAggregateType(), cmd.getAggregateId(), aggregate.getCurrentEventNumber(), List.ofAll(cmd.execute(aggregate)), cmd.useOptimisticLocking());
    }
}
