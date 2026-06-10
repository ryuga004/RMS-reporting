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
@Document(collection = "chat_messages")
public class ChatMessageDocument {
    @Id
    private String id;
    private String messageId;
    private String roomId;
    private Long senderId;
    private Long recipientId;
    private String content;
    private Map<String, Object> metadata;
    private Instant sentAt;
    private Instant receivedAt;
}
