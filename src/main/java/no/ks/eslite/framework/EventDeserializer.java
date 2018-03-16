package no.ks.eslite.framework;

public interface EventDeserializer {
    Event deserialize(byte[] eventData, String eventType);
}
