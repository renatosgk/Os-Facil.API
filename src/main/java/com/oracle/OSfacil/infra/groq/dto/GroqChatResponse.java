package com.oracle.OSfacil.infra.groq.dto;

import java.util.List;

public record GroqChatResponse(List<GroqChoice> choices) {}
