package no.ks.eslite.esjc;

import com.github.msemys.esjc.CatchUpSubscriptionListener;
import com.github.msemys.esjc.EventStore;

public class EsjcEventSubscriber {

    private static final String CATEGORY_STREAM = "";
    private final EventStore eventStore;
    private final Long hwm;
    private final CatchUpSubscriptionListener listener;

    public EsjcEventSubscriber(EventStore eventStore, Long hwm, CatchUpSubscriptionListener listener) {
        this.eventStore = eventStore;
        this.hwm = hwm;
        this.listener = listener;
    }

    public void subscribe(){
        eventStore.subscribeToStreamFrom(CATEGORY_STREAM, hwm, listener);
    }
}
