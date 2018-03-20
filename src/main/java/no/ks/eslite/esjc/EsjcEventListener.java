package no.ks.eslite.esjc;

import com.github.msemys.esjc.CatchUpSubscription;
import com.github.msemys.esjc.CatchUpSubscriptionListener;
import com.github.msemys.esjc.ResolvedEvent;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import no.ks.eslite.framework.EventDeserializer;
import no.ks.eslite.framework.Projection;

public class EsjcEventListener implements CatchUpSubscriptionListener{

    private final EventDeserializer deserializer;
    private final Set<Projection> projections;

    public EsjcEventListener(EventDeserializer deserializer, java.util.Set<Projection> projections) {
        this.deserializer = deserializer;
        this.projections = HashSet.ofAll(projections);
    }

    @Override
    public void onEvent(CatchUpSubscription catchUpSubscription, ResolvedEvent resolvedEvent) {
        projections.forEach(p -> p.getProjectors()
                .apply(resolvedEvent.event.eventType)
                .accept(deserializer.deserialize(resolvedEvent.event.data, resolvedEvent.event.eventType)));
    }
}
