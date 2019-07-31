package no.ks.eslite.framework;

public interface EventSerdes {
    Event deserialize(byte[] eventData, String eventType);
    byte[] serialize(Event event);
}
