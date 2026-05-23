package com.oracle.OSfacil.infra.groq;

import com.oracle.OSfacil.infra.exeception.RegraDeNegocioException;
import com.oracle.OSfacil.infra.groq.dto.GroqChatRequest;
import com.oracle.OSfacil.infra.groq.dto.GroqChatResponse;
import com.oracle.OSfacil.infra.groq.dto.GroqMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class GroqClientImpl implements IGroqClient {

    private static final double TEMPERATURE = 0.7;
    private static final int MAX_TOKENS = 1024;

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public GroqClientImpl(
            @Value("${groq.api.url}") String baseUrl,
            @Value("${groq.api.key}") String apiKey,
            @Value("${groq.api.model}") String model) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.apiKey = apiKey;
        this.model = model;
    }

    @Override
    public String enviarPergunta(String systemPrompt, String pergunta) {
        GroqChatRequest request = new GroqChatRequest(
                model,
                List.of(
                        new GroqMessage("system", systemPrompt),
                        new GroqMessage("user", pergunta)
                ),
                MAX_TOKENS,
                TEMPERATURE
        );

        GroqChatResponse response = restClient.post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new RegraDeNegocioException(
                            "Erro na API Groq - Status: " + res.getStatusCode());
                })
                .body(GroqChatResponse.class);

        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            throw new RegraDeNegocioException("A IA nao retornou nenhuma resposta");
        }

        String content = response.choices().get(0).message().content();
        if (content == null || content.isBlank()) {
            throw new RegraDeNegocioException("Resposta da IA esta vazia");
        }

        return content;
    }
}
