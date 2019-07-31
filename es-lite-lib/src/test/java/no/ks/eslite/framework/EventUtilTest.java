package no.ks.eslite.framework;

import no.ks.eslite.framework.testdomain.HiredEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventUtilTest {
    @Test
    @DisplayName("Test that the event type is correctly retrieved from the event annotation")
    void name() {
        assertEquals("Hired", EventUtil.getEventType(HiredEvent.class));
    }
}