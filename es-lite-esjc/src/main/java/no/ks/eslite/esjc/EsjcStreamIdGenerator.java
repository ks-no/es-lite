package no.ks.eslite.akka;

import java.util.UUID;

@FunctionalInterface
public interface EsjcStreamIdGenerator {
    String generateStreamId(String aggregateType, UUID aggregateId);
}
