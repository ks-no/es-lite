package no.ks.eslite.framework.test;

import lombok.Builder;
import lombok.Data;
import no.ks.eslite.framework.Event;
import no.ks.eslite.framework.EventWrapper;
import no.ks.eslite.framework.Projection;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ValidationProjection extends Projection {
    private final Queue<Expected<Event>> expectedEvents = new LinkedList<>();
    private final Queue<Event> actualEvents = new LinkedList<>();

    public ValidationProjection() {
    }

    public ValidationProjection(InMemoryEventstore eventstore) {
        eventstore.register(this);
    }

    @Override
    public <T extends Event> void accept(EventWrapper wrapper) {
        actualEvents.add(wrapper.getEvent());
    }

    public <T extends Event> ValidationProjection expectedEvent(Class<T> eventClass) {
        return expectedEvent(eventClass, event -> { });
    }

    public <T extends Event> ValidationProjection expectedEvent(Class<T> eventClass, Consumer<T> consumer) {
        expectedEvents.add(Expected.builder().eventClass(eventClass).consumer(event -> consumer.accept(eventClass.cast(event))).build());
        return this;
    }

    public void run(Runnable runnable) {
        run(new RunnableCallable(runnable));
    }

    public void run(Class<? extends Exception> expectedExceptionClass, Runnable runnable) {
        run(expectedExceptionClass, new RunnableCallable(runnable));
    }

    public <T> T run(Callable<T> callable) {
        return run(null, callable);
    }

    public <T> T run(Class<? extends Exception> expectedExceptionClass, Callable<T> callable) {
        try {
            return doRun(callable, () -> {
                Set<String> remainingEvents = actualEvents.stream().map(Event::getClass).map(Class::getSimpleName).collect(Collectors.toSet());
                actualEvents.forEach(event -> requireNonNull(expectedEvents.poll(), "No events expected. Remaining events:" + remainingEvents).check(event));
                assertNoRemainingExpectedEvents();
            });
        } catch (Exception e) {
            Optional.ofNullable(expectedExceptionClass)
                    .filter(ee -> e.getClass().equals(ee))
                    .orElseThrow(() -> rethrowAndWrapAsRuntimeException(e));
        }
        return null;
    }

    public void assertNoRemainingExpectedEvents() {
        if (!expectedEvents.isEmpty()) {
            throw new RuntimeException(String.format("Has remaining events %s.", expectedEvents));
        }
    }

    private Expected<Event> requireNonNull(Expected<Event> expected, String text) {
        if (expected == null) {
            throw new IllegalStateException(text);
        }
        return expected;
    }

    private <T> T doRun(Callable<T> callable, Runnable runnable) throws Exception {
        T call = callable.call();
        runnable.run();
        return call;
    }

    private RuntimeException rethrowAndWrapAsRuntimeException(Exception e) {
        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        }
        throw new WrappedException(e);
    }

    @Builder
    @Data
    private static class Expected<T extends Event> {
        Class<? extends T> eventClass;
        Consumer<Event> consumer;

        public void check(Event event) {
            if (!event.getClass().equals(eventClass)) {
                throw new RuntimeException(String.format("Expected %s. Actual %s", eventClass, event.getClass()));
            }
            consumer.accept(event);
        }
    }

    private static class RunnableCallable implements Callable<Void> {
        private final Runnable runnable;

        RunnableCallable(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public Void call() {
            runnable.run();
            return null;
        }
    }

    public static class WrappedException extends RuntimeException {

        WrappedException(Exception exception) {
            super(exception);
        }
    }

}