package com.oracle.OSfacil.infra.groq;

public interface IGroqClient {
    String enviarPergunta(String systemPrompt, String pergunta);
}
