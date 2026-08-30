package com.financia.kash.shared.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PaginationResponse<T> {
    private List<T> content;
    private int pageNum;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    private boolean last;
}
