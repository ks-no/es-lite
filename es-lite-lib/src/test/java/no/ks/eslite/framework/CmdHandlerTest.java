package no.ks.eslite.framework;

import io.vavr.collection.List;
import no.ks.eslite.framework.testdomain.Employee;
import no.ks.eslite.framework.testdomain.HireCmd;
import no.ks.eslite.framework.testdomain.HiredEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CmdHandlerTest {

    @Test
    @DisplayName("Test that we can apply a cmd to a uninitialized aggregate, and that the derived state is returned")
    void testApplyCmd() {
        UUID aggregateId = UUID.randomUUID();

        AggregateReader readerMock = mock(AggregateReader.class);
        when(readerMock.read(eq(aggregateId), any()))
                .thenReturn(Employee.builder().build());

        CmdHandler<Employee> cmdHandler = new CmdHandler<>(mock(EventWriter.class), readerMock);

        LocalDate startDate = LocalDate.now();
        Employee employee = cmdHandler.handle(HireCmd.builder()
                .aggregateId(aggregateId)
                .startDate(startDate)
                .build());

        assertEquals(aggregateId, employee.getAggregateId());
        assertEquals(startDate, employee.getStartDate());
    }

    @Test
    @DisplayName("Test that the cmd handler throws an exception if invariants specified in the cmd are not met")
    void testInvariant() {
        UUID aggregateId = UUID.randomUUID();

        AggregateReader readerMock = mock(AggregateReader.class);
        when(readerMock.read(eq(aggregateId), any()))
                .thenReturn(Employee.builder().aggregateId(UUID.randomUUID()).build());

        CmdHandler<Employee> cmdHandler = new CmdHandler<>(mock(EventWriter.class), readerMock);

        LocalDate startDate = LocalDate.now();
        assertThrows(IllegalStateException.class, () -> cmdHandler.handle(HireCmd.builder()
                .aggregateId(aggregateId)
                .startDate(startDate)
                .build()));
    }

    @Test
    @DisplayName("Test that the new aggregate state is applied to the event-writer")
    void testWrite() {
        UUID aggregateId = UUID.randomUUID();

        AggregateReader readerMock = mock(AggregateReader.class);
        when(readerMock.read(eq(aggregateId), any()))
                .thenReturn(Employee.builder().build());

        EventWriter writerMock = mock(EventWriter.class);
        CmdHandler<Employee> cmdHandler = new CmdHandler<>(writerMock, readerMock);

        LocalDate startDate = LocalDate.now();
        Employee employee = cmdHandler.handle(HireCmd.builder()
                .aggregateId(aggregateId)
                .startDate(startDate)
                .build());

        Class<List<Event>> events =
                (Class<List<Event>>)(Class) io.vavr.collection.List.class;

        final ArgumentCaptor<List<Event>> captor = ArgumentCaptor.forClass(events);
        verify(writerMock)
                .write(eq(employee.getAggregateType()), eq(aggregateId), eq(0L), captor.capture(), eq(true));

        assertEquals(aggregateId, captor.getValue().single().getAggregateId());
        assertEquals(startDate, ((HiredEvent)captor.getValue().single()).getStartDate());
    }
}