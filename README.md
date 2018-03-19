# es-lite
```java
@Configuration
public class EventsourcingConfiguration {

    @Bean
    public EventStore eventStore(EvenStoreProperties conf) {
        return EventStoreBuilder.newBuilder()
                .clusterNodeUsingGossipSeeds(cluster -> cluster
                        .gossipSeedEndpoints(conf.getHosts().stream().map(p -> new InetSocketAddress(p, 2113)).collect(Collectors.toList())))
                .useSslConnection()
                .userCredentials(conf.getUser(), conf.getPassword())
                .build();
    }

    @Bean
    @SuppressWarnings("unchecked")
    public EventDeserializer eventDeserializer(Set<Event> events){
        return new JacksonEventDeserializer(events.stream().map(Event::getClass).collect(Collectors.toSet()));
    }

    @Bean
    public CmdHandler cmdHandler(EventStore eventStore, EventDeserializer deserializer){
        return new CmdHandler(
                new EsjcEventWriter(eventStore, (aggregateType, aggregateId) -> String.format("authorization_%s_%s", aggregateType, aggregateId)),
                new EsjcAggregateReader(eventStore, deserializer));
    }

    @Bean
    public EsjcEventSubscriber esjcEventSubscriber(EventStore eventStore, EventDeserializer deserializer, Set<Projection> projections){
        return new EsjcEventSubscriber(eventStore, 0L, new EsjcEventListener(deserializer, projections));
    }

}
```
