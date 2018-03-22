package no.ks.eslite.esjc;

import com.github.msemys.esjc.CatchUpSubscription;
import com.github.msemys.esjc.CatchUpSubscriptionListener;
import com.github.msemys.esjc.ResolvedEvent;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import lombok.extern.slf4j.Slf4j;
import no.ks.eslite.framework.EventDeserializer;
import no.ks.eslite.framework.Projection;

@Slf4j
public class EsjcEventProjector implements CatchUpSubscriptionListener{

    private final EventDeserializer deserializer;
    private final Set<Projection> projections;

    public EsjcEventProjector(EventDeserializer deserializer, java.util.Set<Projection> projections) {
        this.deserializer = deserializer;
        this.projections = HashSet.ofAll(projections);
    }

    @Override
    public void onEvent(CatchUpSubscription catchUpSubscription, ResolvedEvent resolvedEvent) {
        log.info("Event {}@{}: {}({}) received", resolvedEvent.originalEventNumber(), resolvedEvent.originalStreamId(), resolvedEvent.event.eventType, resolvedEvent.event.eventId);
        projections.forEach(p -> p.getProjectors()
                .apply(resolvedEvent.event.eventType)
                .accept(deserializer.deserialize(resolvedEvent.event.data, resolvedEvent.event.eventType)));
    }
}
