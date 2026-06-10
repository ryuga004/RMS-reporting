package com.rms.reporting.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class NotificationDocument {
    @Id
    private String id;
    private String notificationId;
    private Long userId;
    private String title;
    private String body;
    private String type;
    private boolean broadcast;
    private Map<String, Object> metadata;
    private Instant createdAt;
    private Instant receivedAt;
    private boolean isRead = false;
}
