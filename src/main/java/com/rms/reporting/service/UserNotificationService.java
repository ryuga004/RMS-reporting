package com.rms.reporting.service;

import com.rms.reporting.model.NotificationEvent;
import com.rms.reporting.persistence.NotificationDocument;
import com.rms.reporting.persistence.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserNotificationService {

    private final NotificationRepository notificationRepository;
    private final MongoTemplate mongoTemplate;

    public void store(NotificationEvent message) {
        NotificationDocument document = NotificationDocument.builder()
                .notificationId(message.getNotificationId())
                .userId(message.getUserId())
                .title(message.getTitle())
                .body(message.getBody())
                .type(message.getType())
                .broadcast(message.isBroadcast())
                .metadata(message.getMetadata())
                .createdAt(message.getCreatedAt())
                .receivedAt(Instant.now())
                .isRead(false)
                .build();

        notificationRepository.save(document);
    }

    public Page<NotificationDocument> search(
            String notificationId,
            Long userId,
            String type,
            Boolean broadcast,
            Boolean isRead,
            Instant from,
            Instant to,
            Pageable pageable
    ) {
        Query query = new Query();
        List<Criteria> criteria = new ArrayList<>();

        if (StringUtils.hasText(notificationId)) {
            criteria.add(Criteria.where("notificationId").is(notificationId));
        }
        if (userId != null) {
            criteria.add(Criteria.where("userId").is(userId));
        }
        if (StringUtils.hasText(type)) {
            criteria.add(Criteria.where("type").is(type));
        }
        if (broadcast != null) {
            criteria.add(Criteria.where("broadcast").is(broadcast));
        }
        if (isRead != null) {
            criteria.add(Criteria.where("isRead").is(isRead));
        }
        if (from != null || to != null) {
            Criteria timeCriteria = Criteria.where("createdAt");
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

        long total = mongoTemplate.count(query, NotificationDocument.class);
        query.with(pageable);
        List<NotificationDocument> results = mongoTemplate.find(query, NotificationDocument.class);

        return new PageImpl<>(results, pageable, total);
    }

    public void markRead(String notificationId) {
        mongoTemplate.updateFirst(
                Query.query(Criteria.where("notificationId").is(notificationId)),
                Update.update("isRead", true),
                NotificationDocument.class);
    }

    public void markAllRead(Long userId) {
        Query q = userId != null
                ? Query.query(Criteria.where("userId").is(userId).and("isRead").is(false))
                : Query.query(Criteria.where("broadcast").is(true).and("isRead").is(false));
        mongoTemplate.updateMulti(q, Update.update("isRead", true), NotificationDocument.class);
    }
}
