package com.financia.kash.shared.infrastructure.utils.event;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class DomainEventPublisher {
    private final Sinks.Many<Object> sink = Sinks.many().multicast().onBackpressureBuffer();

    public void publish(Object envent) {
        sink.tryEmitNext(envent);
    }

    public Flux<Object> getStream() {
        return sink.asFlux();
    }
}
