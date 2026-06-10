package com.rms.reporting.dto;

import com.rms.reporting.persistence.ChatMessageDocument;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Map;

@Value
@Builder
public class ChatMessageResponse {
    String id;
    String messageId;
    String roomId;
    Long senderId;
    Long recipientId;
    String content;
    Map<String, Object> metadata;
    Instant sentAt;
    Instant receivedAt;

    public static ChatMessageResponse from(ChatMessageDocument document) {
        if (document == null) {
            return null;
        }
        return ChatMessageResponse.builder()
                .id(document.getId())
                .messageId(document.getMessageId())
                .roomId(document.getRoomId())
                .senderId(document.getSenderId())
                .recipientId(document.getRecipientId())
                .content(document.getContent())
                .metadata(document.getMetadata())
                .sentAt(document.getSentAt())
                .receivedAt(document.getReceivedAt())
                .build();
    }
}
