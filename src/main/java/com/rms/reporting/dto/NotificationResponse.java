package com.rms.reporting.dto;

import com.rms.reporting.persistence.NotificationDocument;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Map;

@Value
@Builder
public class NotificationResponse {
    String id;
    String notificationId;
    Long userId;
    String title;
    String body;
    String type;
    boolean broadcast;
    Map<String, Object> metadata;
    Instant createdAt;
    Instant receivedAt;
    boolean isRead;

    public static NotificationResponse from(NotificationDocument document) {
        if (document == null) {
            return null;
        }
        return NotificationResponse.builder()
                .id(document.getId())
                .notificationId(document.getNotificationId())
                .userId(document.getUserId())
                .title(document.getTitle())
                .body(document.getBody())
                .type(document.getType())
                .broadcast(document.isBroadcast())
                .metadata(document.getMetadata())
                .createdAt(document.getCreatedAt())
                .receivedAt(document.getReceivedAt())
                .isRead(document.isRead())
                .build();
    }
}
