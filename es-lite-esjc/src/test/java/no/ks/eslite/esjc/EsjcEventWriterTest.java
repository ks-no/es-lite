package no.ks.eslite.esjc;

import com.github.msemys.esjc.EventStore;
import com.github.msemys.esjc.Position;
import com.github.msemys.esjc.WriteResult;
import io.vavr.collection.List;
import no.ks.eslite.esjc.jackson.JacksonEventSerdes;
import no.ks.eslite.esjc.testdomain.HiredEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static java.util.Collections.singleton;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class EsjcEventWriterTest {

    @Test
    @DisplayName("Test at esjc writer blir korrekt invokert under skriving av hendelser til aggregat")
    void testWrite() {
        EventStore eventStoreMock = mock(EventStore.class);
        when(eventStoreMock.appendToStream(anyString(), anyLong(), anyCollection()))
                .thenReturn(CompletableFuture.completedFuture(new WriteResult(0, new Position(0L, 0L))));

        EsjcEventWriter esjcEventWriter = new EsjcEventWriter(eventStoreMock, (t, id) -> t + "." + id, new JacksonEventSerdes(singleton(HiredEvent.class)));
        String aggregateType = UUID.randomUUID().toString();

        UUID aggregateId = UUID.randomUUID();

        LocalDate startDate = LocalDate.now();

        esjcEventWriter.write(aggregateType, aggregateId, 0L, List.of(HiredEvent.builder()
                .aggregateId(aggregateId)
                .startDate(startDate)
                .build()), true);

        verify(eventStoreMock).appendToStream(eq(aggregateType + "." + aggregateId), eq(0L), anyCollection());
    }
}