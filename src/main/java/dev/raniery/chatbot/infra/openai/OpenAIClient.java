package dev.raniery.chatbot.infra.openai;

import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.domain.chat.Chat;
import io.github.sashirestela.openai.domain.chat.ChatMessage.SystemMessage;
import io.github.sashirestela.openai.domain.chat.ChatMessage.UserMessage;
import io.github.sashirestela.openai.domain.chat.ChatRequest;
import io.github.sashirestela.openai.exception.OpenAIException.AuthenticationException;
import io.github.sashirestela.openai.exception.OpenAIException.InternalServerException;
import io.github.sashirestela.openai.exception.OpenAIException.RateLimitException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class OpenAIClient {

    private final String apiKey;
    private final SimpleOpenAI service;

    public OpenAIClient(@Value("${app.openai.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.service = SimpleOpenAI.builder().apiKey(apiKey).build();
    }

    public CompletableFuture<Chat> sendRequestChatCompletion(ChatCompletionDataRequest dataRequest) {
        ChatRequest chatRequest = ChatRequest.builder()
            .model("gpt-4-1106-preview")
            .message(SystemMessage.of(dataRequest.systemMessage()))
            .message(UserMessage.of(dataRequest.userMessage()))
            .build();

        var tries = 0;
        var seconds = 5;
        while (tries < 3) {
            try {
                return service.chatCompletions().create(chatRequest);
            } catch (AuthenticationException e) {
                throw new RuntimeException("Invalid API Key");
            } catch (RateLimitException e) {
                tries++;
                try {
                    Thread.sleep(1000 * seconds);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                seconds *= 2;
                throw new RuntimeException("Rate Limit Exceeded, try number: " + tries + " of 3. Waiting " + seconds + " seconds");
            } catch (InternalServerException e) {
                tries++;
                try {
                    Thread.sleep(1000 * seconds);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                seconds *= 2;
                throw new RuntimeException("Internal Server Error, try number: " + tries + " of 3. Waiting " + seconds + " seconds");
            }
        }
        throw new RuntimeException("API Fora do ar! Tentativas finalizadas sem sucesso!");
    }

}
