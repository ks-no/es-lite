package no.ks.eslite.esjc.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vavr.API;
import io.vavr.Tuple;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import no.ks.eslite.framework.Event;
import no.ks.eslite.framework.EventSerdes;
import no.ks.eslite.framework.EventType;

import java.io.IOException;
import java.util.Set;

public class JacksonEventSerdes implements EventSerdes {

    private final ObjectMapper objectMapper;
    private final Map<String, Class<? extends Event>> events;

    public JacksonEventSerdes(Set<Class<? extends Event>> events) {
        this(new ObjectMapper().registerModule(new Jdk8Module()).registerModule(new JavaTimeModule()), events);
    }

    public JacksonEventSerdes(ObjectMapper objectMapper, Set<Class<? extends Event>> events) {
        this.objectMapper = objectMapper;
        this.events = HashSet.ofAll(events)
                .peek(p -> {
                    if (!p.isAnnotationPresent(EventType.class))
                        throw new RuntimeException(String.format("The event-class \"%s\" has not been annotated with the EventType annotation", p.getSimpleName()));
                })
                .toMap(API.unchecked(p -> Tuple.of(p.getAnnotation(EventType.class).value(), p)));
    }

    @Override
    public Event deserialize(byte[] eventData, String eventType) {
        try {
            Event event = objectMapper.readValue(
                    eventData,
                    events.get(eventType).getOrElseThrow(() -> new RuntimeException("No class registered for event type " + eventType)));

            Event upgraded = event.upgrade();
            while (upgraded != null) {
                event = upgraded;
                upgraded = event.upgrade();
            }

            return event;
        } catch (IOException e) {
            throw new RuntimeException("Error during deserialization of eventType " + eventType, e);
        }
    }

    @Override
    public byte[] serialize(Event event) {
        try {
            return objectMapper.writeValueAsBytes(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error during serialization of event: " + event);
        }
    }
}
