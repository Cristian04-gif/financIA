package com.financia.kash.shared.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaginationRequest {

    private int pageNum;
    private int pageSize;
    private String sortBy;
    private String direction;
}
