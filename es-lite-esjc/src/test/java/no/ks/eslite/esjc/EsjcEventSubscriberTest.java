package no.ks.eslite.esjc;

import com.github.msemys.esjc.CatchUpSubscriptionListener;
import com.github.msemys.esjc.CatchUpSubscriptionSettings;
import com.github.msemys.esjc.EventStore;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EsjcEventSubscriberTest {

    @Test
    void testSubscriberGeneration() {
        EsjcEventSubscriber subscriber;

        EventStore mock = mock(EventStore.class);
        subscriber = new EsjcEventSubscriber(mock);

        String category = UUID.randomUUID().toString();
        long hwm = ThreadLocalRandom.current().nextLong();
        CatchUpSubscriptionListener listener = mock(CatchUpSubscriptionListener.class);
        subscriber.subscribeByCategory(category, hwm, listener);

        verify(mock).subscribeToStreamFrom(eq("$ce-" + category), eq(hwm), any(CatchUpSubscriptionSettings.class), eq(listener));
    }

}