package com.oracle.OSfacil.service;

import com.oracle.OSfacil.infra.exeception.RegraDeNegocioException;
import com.oracle.OSfacil.infra.groq.IGroqClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssistenteMecanicaServiceTest {

    @Mock
    IGroqClient groqClient;

    @InjectMocks
    AssistenteMecanicaService service;

    @Test
    void responder_quandoGroqRetornaConteudo_repassaResposta() {
        String respostaEsperada = "O barulho ao frear pode indicar pastilhas desgastadas.";
        when(groqClient.enviarPergunta(anyString(), anyString())).thenReturn(respostaEsperada);

        String resultado = service.responder("Meu carro faz barulho ao frear");

        assertThat(resultado).isEqualTo(respostaEsperada);
    }

    @Test
    void responder_semprePassaSystemPromptNaoVazio() {
        when(groqClient.enviarPergunta(anyString(), anyString())).thenReturn("ok");

        service.responder("qualquer pergunta");

        verify(groqClient).enviarPergunta(
                argThat(prompt -> prompt != null && !prompt.isBlank()),
                eq("qualquer pergunta")
        );
    }

    @Test
    void responder_quandoGroqLancaExcecao_propagaExcecao() {
        when(groqClient.enviarPergunta(anyString(), anyString()))
                .thenThrow(new RegraDeNegocioException("Erro na API Groq - Status: 429 TOO_MANY_REQUESTS"));

        assertThatThrownBy(() -> service.responder("pergunta qualquer"))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Groq");
    }

    @Test
    void responder_passaPerguntaOriginalSemModificacao() {
        String pergunta = "Motor aquecendo demais, o que pode ser?";
        when(groqClient.enviarPergunta(anyString(), eq(pergunta))).thenReturn("resposta");

        service.responder(pergunta);

        verify(groqClient).enviarPergunta(anyString(), eq(pergunta));
    }
}
