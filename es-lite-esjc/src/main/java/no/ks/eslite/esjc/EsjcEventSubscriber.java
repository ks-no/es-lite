package no.ks.eslite.esjc;

import com.github.msemys.esjc.CatchUpSubscriptionListener;
import com.github.msemys.esjc.CatchUpSubscriptionSettings;
import com.github.msemys.esjc.EventStore;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EsjcEventSubscriber {

    private final EventStore eventStore;

    public EsjcEventSubscriber(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    public EsjcSubscription subscribeByCategory(String category, Long hwm, CatchUpSubscriptionListener listener) {
        var subscription = eventStore.subscribeToStreamFrom("$ce-" + category, hwm, CatchUpSubscriptionSettings.newBuilder().resolveLinkTos(true).build(), listener);
        log.info("Subscription initiated from event number {} on category projection {}", hwm, category);
        return new CatchUpEsjcSubscriptionWrapper(subscription);
    }
}
