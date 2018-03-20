package no.ks.eslite.framework;

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
        writer.write(aggregate.getAggregateType(), aggregate.getCurrentEventNumber(), cmd.execute(aggregate));
    }
}
