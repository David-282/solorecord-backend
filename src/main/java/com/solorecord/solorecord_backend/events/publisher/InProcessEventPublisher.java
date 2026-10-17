package com.solorecord.solorecord_backend.events.publisher;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class InProcessEventPublisher implements EventPublisher{

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(Object event) {
        eventPublisher.publishEvent(event);

    }
}
