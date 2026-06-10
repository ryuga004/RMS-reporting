package com.rms.reporting.persistence;

import com.rms.reporting.model.AuditAction;
import com.rms.reporting.model.AuditType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "audit_events")
public class AuditEventDocument {
    @Id
    private String id;
    private String eventId;
    private AuditType type;
    private Long userId;
    private String method;
    private String path;
    private String resourceId;
    private AuditAction action;
    private int status;
    private Instant timestamp;
    private Instant receivedAt;
}
