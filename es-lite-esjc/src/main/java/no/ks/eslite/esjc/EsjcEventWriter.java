package no.ks.eslite.esjc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.msemys.esjc.EventData;
import com.github.msemys.esjc.EventStore;
import com.github.msemys.esjc.WriteResult;
import io.vavr.collection.List;
import lombok.extern.slf4j.Slf4j;
import no.ks.eslite.framework.Event;
import no.ks.eslite.framework.EventType;
import no.ks.eslite.framework.EventUtil;
import no.ks.eslite.framework.EventWriter;

import java.util.Optional;
import java.util.UUID;

import static io.vavr.API.unchecked;

@Slf4j
public class EsjcEventWriter implements EventWriter {

    private final EventStore eventStore;
    private EsjcStreamIdGenerator streamIdGenerator;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EsjcEventWriter(EventStore eventStore, EsjcStreamIdGenerator streamIdGenerator) {
        this.eventStore = eventStore;
        this.streamIdGenerator = streamIdGenerator;
    }

    @Override
    public void write(String aggregateType, UUID aggregateId, Long expectedEventNumber, List<Event> events) {
        String stream = streamIdGenerator.generateStreamId(aggregateType, aggregateId);
        try {
            WriteResult writeResult = eventStore.appendToStream(
                    stream,
                    expectedEventNumber,
                    events.map(aggEvent -> EventData.newBuilder()
                            .jsonData(unchecked(() -> objectMapper.writeValueAsBytes(aggEvent)).get())
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
