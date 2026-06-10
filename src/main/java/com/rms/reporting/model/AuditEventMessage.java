package com.rms.reporting.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEventMessage {
    private String eventId;
    private AuditType type;
    private Long userId;
    private String method;
    private String path;
    private String resourceId;
    private AuditAction action;
    private int status;
    private Instant timestamp;
}
