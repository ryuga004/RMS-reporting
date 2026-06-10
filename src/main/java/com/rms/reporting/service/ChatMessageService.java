package com.rms.reporting.service;

import com.rms.reporting.model.ChatMessageEvent;
import com.rms.reporting.persistence.ChatMessageDocument;
import com.rms.reporting.persistence.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final MongoTemplate mongoTemplate;

    public void store(ChatMessageEvent message) {
        ChatMessageDocument document = ChatMessageDocument.builder()
                .messageId(message.getMessageId())
                .roomId(message.getRoomId())
                .senderId(message.getSenderId())
                .recipientId(message.getRecipientId())
                .content(message.getContent())
                .metadata(message.getMetadata())
                .sentAt(message.getSentAt())
                .receivedAt(Instant.now())
                .build();

        chatMessageRepository.save(document);
    }

    public Page<ChatMessageDocument> search(
            String messageId,
            String roomId,
            Long senderId,
            Long recipientId,
            Instant from,
            Instant to,
            Pageable pageable
    ) {
        Query query = new Query();
        List<Criteria> criteria = new ArrayList<>();

        if (StringUtils.hasText(messageId)) {
            criteria.add(Criteria.where("messageId").is(messageId));
        }
        if (StringUtils.hasText(roomId)) {
            criteria.add(Criteria.where("roomId").is(roomId));
        }
        if (senderId != null) {
            criteria.add(Criteria.where("senderId").is(senderId));
        }
        if (recipientId != null) {
            criteria.add(Criteria.where("recipientId").is(recipientId));
        }
        if (from != null || to != null) {
            Criteria timeCriteria = Criteria.where("sentAt");
            if (from != null && to != null) {
                timeCriteria.gte(from).lte(to);
            } else if (from != null) {
                timeCriteria.gte(from);
            } else {
                timeCriteria.lte(to);
            }
            criteria.add(timeCriteria);
        }

        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }

        long total = mongoTemplate.count(query, ChatMessageDocument.class);
        query.with(pageable);
        List<ChatMessageDocument> results = mongoTemplate.find(query, ChatMessageDocument.class);

        return new PageImpl<>(results, pageable, total);
    }
}
