package no.ks.eslite.esjc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.msemys.esjc.EventData;
import com.github.msemys.esjc.EventStore;
import com.github.msemys.esjc.ExpectedVersion;
import no.ks.eslite.framework.Event;
import no.ks.eslite.framework.EventWriter;
import io.vavr.collection.List;

import static no.ks.eslite.framework.EsUtil.generateStreamId;
import static io.vavr.API.unchecked;

public class EsjcEventWriter implements EventWriter {

    private final EventStore eventStore;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EsjcEventWriter(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void write(List<Event> events) {
        events.groupBy(Event::getAggregateId).forEach((aggId, aggEvents) -> eventStore.appendToStream(
                generateStreamId(aggEvents.distinctBy(Event::getAggregateType).single().getAggregateType(), aggId),
                ExpectedVersion.ANY,
                aggEvents.map(aggEvent -> EventData.newBuilder()
                        .jsonData(unchecked(() -> objectMapper.writeValueAsBytes(aggEvent)).get())
                        .type(aggEvent.getClass().getSimpleName())
                        .build()).toJavaList()));
    }
    
}
