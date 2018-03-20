package no.ks.eslite.esjc;

import com.github.msemys.esjc.EventStore;
import io.vavr.collection.List;
import no.ks.eslite.framework.AggregateReader;
import no.ks.eslite.framework.Event;
import no.ks.eslite.framework.EventDeserializer;

import java.util.UUID;

public class EsjcAggregateReader implements AggregateReader {

    private final EventStore eventStore;
    private EventDeserializer deserializer;
    private EsjcStreamIdGenerator esjcStreamIdGenerator;

    public EsjcAggregateReader(EventStore eventStore, EventDeserializer deserializer, EsjcStreamIdGenerator esjcStreamIdGenerator) {
        this.eventStore = eventStore;
        this.deserializer = deserializer;
        this.esjcStreamIdGenerator = esjcStreamIdGenerator;
    }

    @Override
    public List<Event> read(String aggregateType, UUID aggregateId) {

        return List.ofAll(eventStore.streamEventsForward(this.esjcStreamIdGenerator.generateStreamId(aggregateType, aggregateId), 0, 100, true)
                .map(p -> deserializer.deserialize(p.event.data, p.event.eventType)));
    }
}
