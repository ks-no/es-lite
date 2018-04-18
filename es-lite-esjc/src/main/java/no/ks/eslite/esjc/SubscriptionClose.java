package no.ks.eslite.akka;


import com.github.msemys.esjc.SubscriptionDropReason;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SubscriptionClose {
    SubscriptionDropReason subscriptionDropReason;
    Exception exception;
}
