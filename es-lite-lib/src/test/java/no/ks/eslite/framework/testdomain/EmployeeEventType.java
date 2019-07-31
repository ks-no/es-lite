package no.ks.eslite.framework.testdomain;

import no.ks.eslite.framework.Event;
import no.ks.eslite.framework.EventType;

public abstract class EmployeeEventType implements Event {

    @Override
    public long getTimestamp() {
        return 0;
    }
}
