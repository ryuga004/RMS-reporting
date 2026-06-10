package com.rms.reporting.service;

import com.rms.reporting.model.AuditAction;
import com.rms.reporting.model.AuditEventMessage;
import com.rms.reporting.model.AuditType;
import com.rms.reporting.persistence.AuditEventDocument;
import com.rms.reporting.persistence.AuditEventRepository;
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
public class AuditEventService {

    private final AuditEventRepository auditEventRepository;
    private final MongoTemplate mongoTemplate;

    public void store(AuditEventMessage message) {
        AuditEventDocument document = AuditEventDocument.builder()
                .eventId(message.getEventId())
                .type(message.getType())
                .userId(message.getUserId())
                .method(message.getMethod())
                .path(message.getPath())
                .resourceId(message.getResourceId())
                .action(message.getAction())
                .status(message.getStatus())
                .timestamp(message.getTimestamp())
                .receivedAt(Instant.now())
                .build();

        auditEventRepository.save(document);
    }

    public Page<AuditEventDocument> search(
            String eventId,
            AuditType type,
            Long userId,
            String method,
            String path,
            String resourceId,
            AuditAction action,
            Integer status,
            Instant from,
            Instant to,
            Pageable pageable
    ) {
        Query query = new Query();
        List<Criteria> criteria = new ArrayList<>();

        if (StringUtils.hasText(eventId)) {
            criteria.add(Criteria.where("eventId").is(eventId));
        }
        if (type != null) {
            criteria.add(Criteria.where("type").is(type));
        }
        if (userId != null) {
            criteria.add(Criteria.where("userId").is(userId));
        }
        if (StringUtils.hasText(method)) {
            criteria.add(Criteria.where("method").is(method));
        }
        if (StringUtils.hasText(path)) {
            criteria.add(Criteria.where("path").is(path));
        }
        if (StringUtils.hasText(resourceId)) {
            criteria.add(Criteria.where("resourceId").is(resourceId));
        }
        if (action != null) {
            criteria.add(Criteria.where("action").is(action));
        }
        if (status != null) {
            criteria.add(Criteria.where("status").is(status));
        }
        if (from != null || to != null) {
            Criteria timeCriteria = Criteria.where("timestamp");
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

        long total = mongoTemplate.count(query, AuditEventDocument.class);
        query.with(pageable);
        List<AuditEventDocument> results = mongoTemplate.find(query, AuditEventDocument.class);

        return new PageImpl<>(results, pageable, total);
    }
}
