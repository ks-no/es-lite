package no.ks.eslite.esjc;

import java.util.UUID;

@FunctionalInterface
public interface EsjcStreamIdGenerator {
    String generateStreamId(String aggregateType, UUID aggregateId);
}
