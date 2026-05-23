package com.oracle.OSfacil.infra.groq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GroqChatRequest(
        String model,
        List<GroqMessage> messages,
        @JsonProperty("max_tokens") int maxTokens,
        double temperature
) {}
