package com.graph.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "orgSettings")
public class OnBoardParams {

    @Id
    private String id;
    private String clientId;
    private String tenetId;
    private String secret;
}
