package no.ks.eslite.esjc.testdomain;

import no.ks.eslite.framework.Event;

public abstract class EmployeeEventType implements Event {

    @Override
    public long getTimestamp() {
        return 0;
    }
}
