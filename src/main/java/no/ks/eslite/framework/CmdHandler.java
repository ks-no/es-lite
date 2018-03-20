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
        writer.write(cmd.execute(reader.read(cmd.getAggregateType(), cmd.getAggregateId())
                        .foldLeft(cmd.getAggregate().initState(), (s, e) -> cmd.getAggregate().apply(s, e))));
    }
}
