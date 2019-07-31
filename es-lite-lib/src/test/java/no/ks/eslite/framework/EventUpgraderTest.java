package no.ks.eslite.framework;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventUpgraderTest {

    @Test
    @DisplayName("Test that the upgrade method of an event is executed, and that the upgraded event is returned")
    void testUpgrade() {
        UUID aggregateId = UUID.randomUUID();
        NewEvent newEvent = EventUpgrader.upgradeTo(new OldEvent(aggregateId), NewEvent.class);
        assertEquals(aggregateId, newEvent.getAggregateId());
    }

    private class OldEvent implements Event {
        private UUID aggregateId;

        OldEvent(UUID aggregateId) {
            this.aggregateId = aggregateId;
        }

        @Override
        public UUID getAggregateId() {
            return aggregateId;
        }

        @Override
        public long getTimestamp() {
            return 0;
        }

        @Override
        public Event upgrade() {
            return new NewEvent(aggregateId);
        }
    }

    private class NewEvent implements Event{
        private UUID aggregateId;

        NewEvent(UUID aggregateId) {
            this.aggregateId = aggregateId;
        }

        @Override
        public UUID getAggregateId() {
            return aggregateId;
        }

        @Override
        public long getTimestamp() {
            return 0;
        }

    }
}