package com.financia.kash.shared.domain;

import lombok.Data;

@Data
public class PaginationRequest {

    private int pageNum;
    private int pageSize;
    private String sortBy;
    private String direction;
}
