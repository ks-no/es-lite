# es-lite
```java
@Configuration
public class EventsourcingConfiguration {

    @Bean
    public EventStore eventStore(EvenStoreProperties conf) {
        return EventStoreBuilder.newBuilder()
                .singleNodeAddress(conf.getHosts().stream().findAny().get(), 1113)
                .userCredentials(conf.getUser(), conf.getPassword())
                .build();
    }

    @Bean
    public EventDeserializer eventDeserializer() {
        return new JacksonEventDeserializer(List.ofAll(new Reflections("no.ks.fiks").getSubTypesOf(Event.class))
                .filter(p -> p.isAnnotationPresent(EventType.class))
                .toJavaSet());
    }

    @Bean
    public CmdHandler cmdHandler(EventStore eventStore, EventDeserializer deserializer) {
        EsjcStreamIdGenerator streamIdGenerator = (aggregateType, aggregateId) -> String.format("no.ks.fiks.autorisasjon-%s-%s", aggregateType, aggregateId);

        return new CmdHandler(
                new EsjcEventWriter(eventStore, streamIdGenerator),
                new EsjcAggregateReader(eventStore, deserializer, streamIdGenerator));
    }

}


@Component
public class ProjectionInitializer {

    private EsjcEventSubscriber subscriber;
    private EventDeserializer deserializer;
    private Set<Projection> projections;
    private HwmRepo hwmRepo;

    @Autowired
    public ProjectionInitializer(EventStore subscriber, EventDeserializer deserializer, Set<Projection> projections, HwmRepo hwmRepo) {
        this.subscriber = new EsjcEventSubscriber(subscriber);
        this.deserializer = deserializer;
        this.projections = projections;
        this.hwmRepo = hwmRepo;
    }

    @EventListener({ApplicationReadyEvent.class})
    void contextRefreshedEvent() {
        subscriber.subscribeByCategory(
                "no.ks.fiks",
                hwmRepo.get().orElse(null),
                new EsjcEventProjector(deserializer, projections, (hwm) -> hwmRepo.update(hwm)));
    }
}



```
