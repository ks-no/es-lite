package no.ks.eslite.framework;

import no.ks.eslite.esjc.EsjcStreamIdGenerator;

public class CmdHandler {

    private final AggregateReader reader;
    private final EventWriter writer;

    public CmdHandler(EventWriter writer, AggregateReader reader) {
        this.reader = reader;
        this.writer = writer;
    }

    @SuppressWarnings("unchecked")
    public void handle(Command cmd, EsjcStreamIdGenerator esjcStreamIdGenerator) {
        writer.write(cmd.getAggregate().handle(
                reader.read(cmd.getAggregateType(), cmd.getAggregateId(), esjcStreamIdGenerator)
                        .foldLeft(cmd.getAggregate().initState(), (s, e) -> cmd.getAggregate().apply(s, e)),
                cmd));
    }
}
