package com.rms.reporting.controller;

import com.rms.reporting.dto.AuditEventResponse;
import com.rms.reporting.dto.AuditSearchResponse;
import com.rms.reporting.model.AuditAction;
import com.rms.reporting.model.AuditType;
import com.rms.reporting.persistence.AuditEventDocument;
import com.rms.reporting.service.AuditEventService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
public class AuditEventController {

    private final AuditEventService auditEventService;

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public AuditSearchResponse search(
            @RequestParam(required = false) String eventId,
            @RequestParam(required = false) AuditType type,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String method,
            @RequestParam(required = false) String path,
            @RequestParam(required = false) String resourceId,
            @RequestParam(required = false) AuditAction action,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "0") @Min(0) @Max(1000) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(1000) int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Pageable pageable = PageRequest.of(page,size,buildSort(sortBy,sortDir));

        Page<AuditEventDocument> results = auditEventService.search(
                eventId,
                type,
                userId,
                method,
                path,
                resourceId,
                action,
                status,
                from,
                to,
                pageable
        );

        List<AuditEventResponse> items = results.getContent().stream()
                .map(AuditEventResponse::from)
                .toList();

        return AuditSearchResponse.builder()
                .items(items)
                .page(results.getNumber())
                .size(results.getSize())
                .totalElements(results.getTotalElements())
                .totalPages(results.getTotalPages())
                .build();
    }

    private Sort buildSort(String sortBy, String sortDir) {
        String field = sortBy.toLowerCase();
        Sort sort = Sort.by(field);
        if ("asc".equalsIgnoreCase(sortDir)) {
            return sort.ascending();
        }
        return sort.descending();
    }
}