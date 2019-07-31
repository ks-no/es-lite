package no.ks.eslite.framework;

import lombok.NonNull;

import java.util.Optional;

public class EventUpgrader {

    @SuppressWarnings("unchecked")
    static <T extends Event> T upgradeTo(@NonNull final Event event, @NonNull final Class<T> targetClass){
        Event upgraded = event;
        while (!upgraded.getClass().isInstance(targetClass) && upgraded.upgrade() != null)
            upgraded = upgraded.upgrade();

        if (upgraded.getClass().isInstance(targetClass))
            throw new RuntimeException(String.format("Attempted to upgrade event %s to %s, but upgrades where exhausted without arriving at the target class", event.getClass().getName(), targetClass.getName()));
        else
            return (T) upgraded;
    }
}
