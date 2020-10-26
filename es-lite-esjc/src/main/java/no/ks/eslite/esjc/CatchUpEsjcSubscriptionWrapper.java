package no.ks.eslite.esjc;

import com.github.msemys.esjc.CatchUpSubscription;
import lombok.NonNull;

public class CatchUpEsjcSubscriptionWrapper implements EsjcSubscription {

    private final CatchUpSubscription subscription;

    public CatchUpEsjcSubscriptionWrapper(@NonNull CatchUpSubscription subscription) {
        this.subscription = subscription;
    }

    @Override
    public long lastProcessedEvent() {
        return subscription.lastProcessedEventNumber();
    }

    @Override
    public String streamId() {
        return subscription.streamId;
    }
}
