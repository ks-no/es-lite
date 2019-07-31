package no.ks.eslite.esjc;

import com.github.msemys.esjc.EventData;
import com.github.msemys.esjc.EventStore;
import com.github.msemys.esjc.ExpectedVersion;
import com.github.msemys.esjc.WriteResult;
import io.vavr.collection.List;
import lombok.extern.slf4j.Slf4j;
import no.ks.eslite.framework.Event;
import no.ks.eslite.framework.EventSerdes;
import no.ks.eslite.framework.EventUtil;
import no.ks.eslite.framework.EventWriter;

import java.util.UUID;

@Slf4j
public class EsjcEventWriter implements EventWriter {

    private final EventStore eventStore;
    private EsjcStreamIdGenerator streamIdGenerator;
    private EventSerdes deserializer;

    public EsjcEventWriter(EventStore eventStore, EsjcStreamIdGenerator streamIdGenerator, EventSerdes deserializer) {
        this.eventStore = eventStore;
        this.streamIdGenerator = streamIdGenerator;
        this.deserializer = deserializer;
    }

    @Override
    public void write(String aggregateType, UUID aggregateId, Long expectedEventNumber, List<Event> events, boolean useOptimisticLocking) {
        String stream = streamIdGenerator.generateStreamId(aggregateType, aggregateId);
        try {
            WriteResult writeResult = eventStore.appendToStream(
                    stream,
                    useOptimisticLocking ? expectedEventNumber : ExpectedVersion.ANY,
                    events.map(aggEvent -> EventData.newBuilder()
                            .jsonData(deserializer.serialize(aggEvent))
                            .type(EventUtil.getEventType(aggEvent.getClass()))
                            .build())
                            .toJavaList())
                    .get();
            log.info("wrote {} events to stream {}, next expected version for this stream is {}", events.size(), stream, writeResult.nextExpectedVersion);
        } catch (Exception e) {
            throw new RuntimeException("Error while appending events to stream " + stream, e);
        }
    }

}
