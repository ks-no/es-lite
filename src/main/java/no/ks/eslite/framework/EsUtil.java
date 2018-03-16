package no.ks.eslite.framework;

import java.util.UUID;

import static java.lang.String.format;

public class EsUtil {

    public static String generateStreamId(String aggregateType, UUID aggregateId) {
        return format("authorization_%s_%s", aggregateType, aggregateId);
    }
}
