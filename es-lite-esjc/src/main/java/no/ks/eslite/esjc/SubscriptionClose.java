package no.ks.eslite.esjc;


import com.github.msemys.esjc.SubscriptionDropReason;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SubscriptionClose {
    SubscriptionDropReason subscriptionDropReason;
    Exception exception;
}
