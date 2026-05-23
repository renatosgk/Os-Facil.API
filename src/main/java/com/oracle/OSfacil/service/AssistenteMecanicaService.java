package com.oracle.OSfacil.service;

import com.oracle.OSfacil.infra.groq.IGroqClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssistenteMecanicaService implements IAiService {

    private static final String SYSTEM_PROMPT = """
            Você é um mecânico especialista com mais de 20 anos de experiência em diagnósticos automotivos.
            Responda sempre em português brasileiro, de forma didática, clara e direta.
            Foque em diagnósticos preliminares, causas mais comuns dos problemas e orientações iniciais.
            Organize a resposta em tópicos quando houver múltiplas informações.
            Recomende levar o veículo a um mecânico profissional para inspeção presencial sempre que necessário.
            Responda APENAS perguntas relacionadas a veículos, mecânica ou manutenção automotiva.
            Para qualquer outro assunto, informe educadamente que só pode ajudar com questões automotivas.
            """;

    private final IGroqClient groqClient;

    @Override
    public String responder(String pergunta) {
        return groqClient.enviarPergunta(SYSTEM_PROMPT, pergunta);
    }
}
