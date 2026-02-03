package com.workbuddy.cluster.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
public class CommandMessage {
    private final NodeCommand command;
    private final long issuedAt;

    @JsonCreator
    public CommandMessage(
            @JsonProperty("command") NodeCommand command,
            @JsonProperty("issuedAt") long issuedAt
    ) {
        this.command = command;
        this.issuedAt = issuedAt;
    }
}
