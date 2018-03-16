package no.ks.eslite.esjc;

import com.github.msemys.esjc.EventStore;
import no.ks.eslite.framework.AggregateReader;
import no.ks.eslite.framework.Event;
import no.ks.eslite.framework.EventDeserializer;
import io.vavr.collection.List;

import java.util.UUID;

import static no.ks.eslite.framework.EsUtil.generateStreamId;

public class EsjcAggregateReader implements AggregateReader {

    private final EventStore eventStore;
    private EventDeserializer deserializer;

    public EsjcAggregateReader(EventStore eventStore, EventDeserializer deserializer) {
        this.eventStore = eventStore;
        this.deserializer = deserializer;
    }

    @Override
    public List<Event> read(String aggregateType, UUID aggregateId) {
        return List.ofAll(eventStore.streamEventsForward(generateStreamId(aggregateType, aggregateId), 0, 100, true)
                .map(p -> deserializer.deserialize(p.event.data, p.event.eventType)));
    }
}
