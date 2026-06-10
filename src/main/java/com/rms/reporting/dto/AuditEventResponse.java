package com.rms.reporting.dto;

import com.rms.reporting.model.AuditAction;
import com.rms.reporting.model.AuditType;
import com.rms.reporting.persistence.AuditEventDocument;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class AuditEventResponse {
    String id;
    String eventId;
    AuditType type;
    Long userId;
    String method;
    String path;
    String resourceId;
    AuditAction action;
    int status;
    Instant timestamp;
    Instant receivedAt;

    public static AuditEventResponse from(AuditEventDocument document) {
        if (document == null) {
            return null;
        }
        return AuditEventResponse.builder()
                .id(document.getId())
                .eventId(document.getEventId())
                .type(document.getType())
                .userId(document.getUserId())
                .method(document.getMethod())
                .path(document.getPath())
                .resourceId(document.getResourceId())
                .action(document.getAction())
                .status(document.getStatus())
                .timestamp(document.getTimestamp())
                .receivedAt(document.getReceivedAt())
                .build();
    }
}
