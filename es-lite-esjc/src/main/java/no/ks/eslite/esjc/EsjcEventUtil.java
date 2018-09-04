package no.ks.eslite.esjc;

import com.github.msemys.esjc.ResolvedEvent;

final class EsjcEventUtil {
    private EsjcEventUtil() { }

    static boolean isIgnorableEvent(ResolvedEvent resolvedEvent) {
        return resolvedEvent.event == null
                || resolvedEvent.event.eventType == null
                || resolvedEvent.event.eventType.isEmpty()
                || resolvedEvent.event.eventType.startsWith("$");
    }
}
