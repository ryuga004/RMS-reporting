package com.rms.reporting.controller;

import com.rms.reporting.dto.NotificationResponse;
import com.rms.reporting.dto.NotificationSearchResponse;
import com.rms.reporting.persistence.NotificationDocument;
import com.rms.reporting.service.UserNotificationService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final UserNotificationService userNotificationService;

    @GetMapping("")
    @PreAuthorize("hasAnyRole('ADMIN', 'TENANT', 'SUPER_ADMIN')")
    public NotificationSearchResponse search(
            @RequestParam(required = false) String notificationId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Boolean broadcast,
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "0") @Min(0) @Max(1000) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(1000) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Pageable pageable = PageRequest.of(page, size, buildSort(sortBy, sortDir));

        Page<NotificationDocument> results = userNotificationService.search(
                notificationId,
                userId,
                type,
                broadcast,
                isRead,
                from,
                to,
                pageable
        );

        List<NotificationResponse> items = results.getContent().stream()
                .map(NotificationResponse::from)
                .toList();

        return NotificationSearchResponse.builder()
                .items(items)
                .page(results.getNumber())
                .size(results.getSize())
                .totalElements(results.getTotalElements())
                .totalPages(results.getTotalPages())
                .build();
    }

    @PutMapping("/{notificationId}/read")
    @PreAuthorize("hasAnyRole('ADMIN', 'TENANT', 'SUPER_ADMIN')")
    public ResponseEntity<Void> markRead(@PathVariable String notificationId) {
        userNotificationService.markRead(notificationId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/read-all")
    @PreAuthorize("hasAnyRole('ADMIN', 'TENANT', 'SUPER_ADMIN')")
    public ResponseEntity<Void> markAllRead(@RequestParam(required = false) Long userId) {
        userNotificationService.markAllRead(userId);
        return ResponseEntity.noContent().build();
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
