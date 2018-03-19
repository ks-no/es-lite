package no.ks.eslite.esjc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.msemys.esjc.EventData;
import com.github.msemys.esjc.EventStore;
import com.github.msemys.esjc.ExpectedVersion;
import io.vavr.collection.List;
import no.ks.eslite.framework.Event;
import no.ks.eslite.framework.EventWriter;

import static io.vavr.API.unchecked;

public class EsjcEventWriter implements EventWriter {

    private final EventStore eventStore;
    private EsjcStreamIdGenerator streamIdGenerator;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EsjcEventWriter(EventStore eventStore, EsjcStreamIdGenerator streamIdGenerator) {
        this.eventStore = eventStore;
        this.streamIdGenerator = streamIdGenerator;
    }

    @Override
    public void write(List<Event> events) {
        events.groupBy(Event::getAggregateId).forEach((aggId, aggEvents) -> eventStore.appendToStream(
                streamIdGenerator.generateStreamId(aggEvents.distinctBy(Event::getAggregateType).single().getAggregateType(), aggId),
                ExpectedVersion.ANY,
                aggEvents.map(aggEvent -> EventData.newBuilder()
                        .jsonData(unchecked(() -> objectMapper.writeValueAsBytes(aggEvent)).get())
                        .type(aggEvent.getClass().getSimpleName())
                        .build()).toJavaList()));
    }
    
}
