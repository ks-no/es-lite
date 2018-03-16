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
    public EventDeserializer eventDeserializer(){
        return new JacksonEventDeserializer((Set<Class<? extends Event>>) List.ofAll(new Reflections("no.ks.fiks.autorisasjon").getSubTypesOf(Event.class))
                .filter(p -> p.isAnnotationPresent(EventType.class)));
    }

    @Bean
    public CmdHandler cmdHandler(EventStore eventStore, EventDeserializer deserializer){
        return new CmdHandler(new EsjcEventWriter(eventStore), new EsjcAggregateReader(eventStore, deserializer));
    }

    @Bean
    public EsjcEventSubscriber esjcEventSubscriber(EventStore eventStore, EventDeserializer deserializer, Set<Projection> projections){
        return new EsjcEventSubscriber(eventStore, 0L, new EsjcEventListener(deserializer, projections));
    }

}
```
