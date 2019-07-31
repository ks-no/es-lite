package no.ks.eslite.esjc;

import com.github.msemys.esjc.EventStore;
import no.ks.eslite.framework.Aggregate;
import no.ks.eslite.framework.AggregateReader;
import no.ks.eslite.framework.EventSerdes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EsjcAggregateReader implements AggregateReader {

    private final EventStore eventStore;
    private EventSerdes deserializer;
    private EsjcStreamIdGenerator esjcStreamIdGenerator;

    public EsjcAggregateReader(EventStore eventStore, EventSerdes deserializer, EsjcStreamIdGenerator esjcStreamIdGenerator) {
        this.eventStore = eventStore;
        this.deserializer = deserializer;
        this.esjcStreamIdGenerator = esjcStreamIdGenerator;
    }

    @Override
    public Aggregate read(UUID aggregateId,final Aggregate aggregate) {
        final List<Aggregate> mutatedaggregate= new ArrayList<>(1);
        try {
            mutatedaggregate.add(0, aggregate);
            eventStore.streamEventsForward(this.esjcStreamIdGenerator.generateStreamId(aggregate.getAggregateType(), aggregateId), 0, 100, true)
                    .filter(e -> !EsjcEventUtil.isIgnorableEvent(e))
                    .forEach(e -> mutatedaggregate.add(0,mutatedaggregate.get(0).apply(mutatedaggregate.get(0), deserializer.deserialize(e.event.data, e.event.eventType), e.event.eventNumber)));
        } catch(IllegalStateException e){
            if("Unexpected read status: StreamNotFound".equals(e.getMessage())){
                return aggregate.withCurrentEventNumber(-1);
            }
            throw e;
        }
        return mutatedaggregate.get(0);
    }
}
