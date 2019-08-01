package no.ks.eslite.esjc;

import com.github.msemys.esjc.CatchUpSubscription;
import com.github.msemys.esjc.ResolvedEvent;
import com.github.msemys.esjc.proto.EventStoreClientMessages;
import com.google.protobuf.ByteString;
import no.ks.eslite.esjc.jackson.JacksonEventSerdes;
import no.ks.eslite.esjc.testdomain.HiredEvent;
import no.ks.eslite.esjc.testdomain.StartDatesProjection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class EsjcEventProjectorTest {

    @Test
    @DisplayName("Test that a registered projection is updated when a subscribed event is received")
    void name() throws UnsupportedEncodingException {
        JacksonEventSerdes serdes = new JacksonEventSerdes(Collections.singleton(HiredEvent.class));

        StartDatesProjection startDatesProjection = new StartDatesProjection();
        EsjcEventProjector eventProjector = new EsjcEventProjector(serdes, Collections.singleton(startDatesProjection), h -> {}, (r, e) -> {});

        UUID aggregateId = UUID.randomUUID();
        LocalDate startDate = LocalDate.now();

        EventStoreClientMessages.EventRecord hired = EventStoreClientMessages.EventRecord.newBuilder()
                .setData(ByteString.copyFrom(serdes.serialize(HiredEvent.builder()
                        .aggregateId(aggregateId)
                        .startDate(startDate)
                        .build())))
                .setDataContentType(1)
                .setEventStreamId(UUID.randomUUID().toString())
                .setEventNumber(0)
                .setEventId(ByteString.copyFrom(UUID.randomUUID().toString(), "UTF-8"))
                .setEventType("Hired")
                .setMetadataContentType(1)
                .build();

        eventProjector.onEvent(mock(CatchUpSubscription.class), new ResolvedEvent(EventStoreClientMessages.ResolvedIndexedEvent.newBuilder()
                .setLink(hired)
                .setEvent(hired)
                .build()));

        assertEquals(startDate, startDatesProjection.getStartDate(aggregateId));
    }
}