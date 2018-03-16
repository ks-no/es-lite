package no.ks.eslite.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.API;
import io.vavr.Tuple;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import no.ks.eslite.framework.Event;
import no.ks.eslite.framework.EventDeserializer;
import no.ks.eslite.framework.EventType;

import java.io.IOException;
import java.util.Set;

public class JacksonEventDeserializer implements EventDeserializer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Class<? extends Event>> events;

    public JacksonEventDeserializer(Set<Class<? extends Event>> events) {
        this.events = HashSet.ofAll(events)
                .toMap(API.unchecked(p -> Tuple.of(p.getClass().getAnnotation(EventType.class).value(), p)));
    }

    @Override
    public Event deserialize(byte[] eventData, String eventType) {
        try {
            return objectMapper.readValue(
                    eventData,
                    events.get(eventType).getOrElseThrow(() -> new RuntimeException("No class registered for event type " + eventType)));
        } catch (IOException e) {
            throw new RuntimeException("Error during deserialization of eventType " + eventType, e);
        }
    }
}
