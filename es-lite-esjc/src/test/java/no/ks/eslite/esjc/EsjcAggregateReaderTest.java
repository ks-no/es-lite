package no.ks.eslite.esjc;

import com.github.msemys.esjc.EventStore;
import com.github.msemys.esjc.ResolvedEvent;
import com.github.msemys.esjc.operation.StreamNotFoundException;
import com.github.msemys.esjc.proto.EventStoreClientMessages;
import com.google.protobuf.ByteString;
import no.ks.eslite.esjc.jackson.JacksonEventSerdes;
import no.ks.eslite.esjc.testdomain.Employee;
import no.ks.eslite.esjc.testdomain.HiredEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EsjcAggregateReaderTest {

    @Test
    @DisplayName("Test that the reader retrieves incoming events from the event-store")
    void testReadEvents() throws UnsupportedEncodingException {
        JacksonEventSerdes eventSerdes = new JacksonEventSerdes(singleton(HiredEvent.class));

        EventStore eventStoreMock = mock(EventStore.class);
        UUID aggregateId = UUID.randomUUID();
        LocalDate startDate = LocalDate.now();

        when(eventStoreMock.streamEventsForward(anyString(), anyLong(), anyInt(), anyBoolean()))
                .thenReturn(Stream.of(new ResolvedEvent(EventStoreClientMessages.ResolvedIndexedEvent.newBuilder()
                        .setEvent(EventStoreClientMessages.EventRecord.newBuilder()
                                .setData(ByteString.copyFrom(eventSerdes.serialize(HiredEvent.builder()
                                        .aggregateId(aggregateId)
                                        .startDate(startDate)
                                        .build())))
                                .setDataContentType(1)
                                .setEventStreamId(UUID.randomUUID().toString())
                                .setEventNumber(0)
                                .setEventId(ByteString.copyFrom(UUID.randomUUID().toString(), "UTF-8"))
                                .setEventType("Hired")
                                .setMetadataContentType(1)
                                .build())
                        .build())));

        EsjcAggregateReader reader = new EsjcAggregateReader(eventStoreMock, eventSerdes, (t, id) -> t + "." + id);
        Employee aggregate = (Employee) reader.read(UUID.randomUUID(), Employee.builder().build());
        assertNotNull(aggregate);
        assertEquals(aggregateId, aggregate.getAggregateId());
        assertEquals(startDate, aggregate.getStartDate());
    }

    @Test
    @DisplayName("Test that the reader returns an empty aggregate if no stream is found")
    void testReadEmptyAggregate() throws UnsupportedEncodingException {
        JacksonEventSerdes eventSerdes = new JacksonEventSerdes(singleton(HiredEvent.class));

        EventStore eventStoreMock = mock(EventStore.class);

        when(eventStoreMock.streamEventsForward(anyString(), anyLong(), anyInt(), anyBoolean()))
                .thenThrow(new StreamNotFoundException("some message"));

        EsjcAggregateReader reader = new EsjcAggregateReader(eventStoreMock, eventSerdes, (t, id) -> t + "." + id);
        Employee aggregate = (Employee) reader.read(UUID.randomUUID(), Employee.builder().build());
        assertNotNull(aggregate);
        assertEquals(-1, aggregate.getCurrentEventNumber());
    }
}