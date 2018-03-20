package no.ks.eslite.esjc;

import com.github.msemys.esjc.EventStore;
import no.ks.eslite.framework.Aggregate;
import no.ks.eslite.framework.AggregateReader;
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
    public Aggregate read(UUID aggregateId, Aggregate aggregate) {
        try {
            eventStore.streamEventsForward(this.esjcStreamIdGenerator.generateStreamId(aggregate.getAggregateType(), aggregateId), 0, 100, true)
                    .map(p -> deserializer.deserialize(p.event.data, p.event.eventType))
                    .forEach(e -> aggregate.apply(aggregate, e));
        } catch(IllegalStateException e){
            if("Unexpected read status: StreamNotFound".equals(e.getMessage())){
                return aggregate;
            }
            throw e;
        }
        return aggregate;
    }
}
