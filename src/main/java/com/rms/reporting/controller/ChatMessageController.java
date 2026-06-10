package com.rms.reporting.controller;

import com.rms.reporting.dto.ChatMessageResponse;
import com.rms.reporting.dto.ChatSearchResponse;
import com.rms.reporting.persistence.ChatMessageDocument;
import com.rms.reporting.service.ChatMessageService;
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

@RestController
@RequestMapping("/chat/messages")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ChatSearchResponse search(
            @RequestParam(required = false) String messageId,
            @RequestParam(required = false) String roomId,
            @RequestParam(required = false) Long senderId,
            @RequestParam(required = false) Long recipientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "0") @Min(0) @Max(1000) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(1000) int size,
            @RequestParam(defaultValue = "sentAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Pageable pageable = PageRequest.of(page, size, buildSort(sortBy, sortDir));

        Page<ChatMessageDocument> results = chatMessageService.search(
                messageId,
                roomId,
                senderId,
                recipientId,
                from,
                to,
                pageable
        );

        List<ChatMessageResponse> items = results.getContent().stream()
                .map(ChatMessageResponse::from)
                .toList();

        return ChatSearchResponse.builder()
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
