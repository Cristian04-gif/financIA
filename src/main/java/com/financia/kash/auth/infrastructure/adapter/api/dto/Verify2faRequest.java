package com.financia.kash.auth.infrastructure.adapter.api.dto;

import lombok.Data;

@Data
public class Verify2faRequest {

    private String preToken;
    private String code;
}
