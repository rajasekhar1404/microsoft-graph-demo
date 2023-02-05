package com.graph.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Token {
    private String userId;
    private String organizationId;
}
