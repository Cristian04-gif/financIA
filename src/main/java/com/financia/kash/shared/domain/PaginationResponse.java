package com.financia.kash.shared.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaginationResponse<T> {
    private List<T> content;
    private int pageNum;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    private boolean last;

    @JsonCreator
    public PaginationResponse(
            @JsonProperty("content") List<T> content,
            @JsonProperty("pageNum") int pageNum,
            @JsonProperty("pageSize") int pageSize,
            @JsonProperty("totalPages") int totalPages,
            @JsonProperty("totalElements") long totalElements,
            @JsonProperty("last") boolean last) {
        this.content = content;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.last = last;
    }

}
