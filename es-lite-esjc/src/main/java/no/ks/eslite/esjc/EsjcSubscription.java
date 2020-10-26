package no.ks.eslite.esjc;

public interface EsjcSubscription {

    long lastProcessedEvent();

    String streamId();
}
