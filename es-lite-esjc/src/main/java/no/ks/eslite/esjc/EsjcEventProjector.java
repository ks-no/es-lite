package no.ks.eslite.esjc;

import com.github.msemys.esjc.CatchUpSubscription;
import com.github.msemys.esjc.CatchUpSubscriptionListener;
import com.github.msemys.esjc.ResolvedEvent;
import com.github.msemys.esjc.SubscriptionDropReason;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import lombok.extern.slf4j.Slf4j;
import no.ks.eslite.framework.EventDeserializer;
import no.ks.eslite.framework.EventWrapper;
import no.ks.eslite.framework.Projection;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Slf4j
public class EsjcEventProjector implements CatchUpSubscriptionListener{

    private final EventDeserializer deserializer;
    private final Set<Projection> projections;
    private Consumer<Long> hwmUpdater;
    private BiConsumer<SubscriptionDropReason, Exception> onClose;

    public EsjcEventProjector(EventDeserializer deserializer, java.util.Set<Projection> projections, Consumer<Long> hwmUpdater, BiConsumer<SubscriptionDropReason, Exception> onClose) {
        this.deserializer = deserializer;
        this.projections = HashSet.ofAll(projections);
        this.hwmUpdater = hwmUpdater;
        this.onClose = onClose;
    }

    @Override
    public void onEvent(CatchUpSubscription catchUpSubscription, ResolvedEvent resolvedEvent) {
        if(!resolvedEvent.isResolved()){
            log.info("Event not resolved: {} {}", resolvedEvent.originalEventNumber(), resolvedEvent.originalStreamId());
            return;
        }
        if (EsjcEventUtil.isIgnorableEvent(resolvedEvent)) {
            log.info("Event ignored: {} {}", resolvedEvent.originalEventNumber(), resolvedEvent.originalStreamId());
            return;
        }
        log.info("Event {}@{}: {}({}) received", resolvedEvent.originalEventNumber(), resolvedEvent.originalStreamId(), resolvedEvent.event.eventType, resolvedEvent.event.eventId);
        projections.forEach(p -> p.accept(EventWrapper.builder()
                .event(deserializer.deserialize(resolvedEvent.event.data, resolvedEvent.event.eventType))
                .eventNumber(resolvedEvent.originalEventNumber())
                .build()));
        hwmUpdater.accept(resolvedEvent.originalEventNumber());
    }

    @Override
    public void onClose(CatchUpSubscription subscription, SubscriptionDropReason reason, Exception exception) {
        log.error("I'm dead: " + reason, exception);
        onClose.accept(reason, exception);
    }

    @Override
    public void onLiveProcessingStarted(CatchUpSubscription subscription) {
        log.info("We're live!");
        projections
                .flatMap(Projection::getOnLive)
                .forEach(Runnable::run);
    }
}

