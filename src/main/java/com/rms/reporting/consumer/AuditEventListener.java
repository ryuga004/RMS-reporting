package com.rms.reporting.consumer;

import com.rms.reporting.model.AuditEventMessage;
import com.rms.reporting.service.AuditEventService;
import com.rms.reporting.utils.constants.QueueConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditEventListener {

    private final AuditEventService auditEventService;

    @RabbitListener(queues = QueueConstants.Audit.Queue.MAIN)
    public void handleAuditEvent(AuditEventMessage message) {
        log.info("Received audit event: eventId={}, type={}", message != null ? message.getEventId() : null, message != null ? message.getType() : null);
        if (message == null) {
            throw new IllegalArgumentException("Audit event message was null.");
        }
        auditEventService.store(message);
    }

    @RabbitListener(queues = QueueConstants.Audit.Queue.DLQ)
    public void handleAuditEventDlq(AuditEventMessage message) {
        log.error("Audit event moved to DLQ: eventId={}, type={}", message != null ? message.getEventId() : null, message != null ? message.getType() : null);
    }
}
