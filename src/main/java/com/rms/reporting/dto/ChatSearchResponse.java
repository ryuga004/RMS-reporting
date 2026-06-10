package com.rms.reporting.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ChatSearchResponse {
    List<ChatMessageResponse> items;
    int page;
    int size;
    long totalElements;
    int totalPages;
}
