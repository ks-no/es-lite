package no.ks.eslite.esjc;

import com.github.msemys.esjc.CatchUpSubscription;
import com.github.msemys.esjc.CatchUpSubscriptionListener;
import com.github.msemys.esjc.CatchUpSubscriptionSettings;
import com.github.msemys.esjc.EventStore;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EsjcEventSubscriberTest {

    @Test
    void testSubscriberGeneration() {
        EsjcEventSubscriber subscriber;

        final EventStore eventstoreMock = mock(EventStore.class);
        final CatchUpSubscription catchUpSubscription = mock(CatchUpSubscription.class);
        when(eventstoreMock.subscribeToStreamFrom(anyString(), anyLong(), any(CatchUpSubscriptionSettings.class), any(CatchUpSubscriptionListener.class)))
                .thenReturn(catchUpSubscription);
        subscriber = new EsjcEventSubscriber(eventstoreMock);

        String category = UUID.randomUUID().toString();
        long hwm = ThreadLocalRandom.current().nextLong();
        CatchUpSubscriptionListener listener = mock(CatchUpSubscriptionListener.class);
        subscriber.subscribeByCategory(category, hwm, listener);

        verify(eventstoreMock).subscribeToStreamFrom(eq("$ce-" + category), eq(hwm), any(CatchUpSubscriptionSettings.class), eq(listener));
        verifyNoMoreInteractions(catchUpSubscription);
    }

}