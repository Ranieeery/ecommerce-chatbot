package dev.raniery.chatbot.domain.service;

import dev.raniery.chatbot.infra.openai.ChatCompletionDataRequest;
import dev.raniery.chatbot.infra.openai.OpenAIClient;
import io.github.sashirestela.openai.common.content.ContentPart;
import org.springframework.stereotype.Service;

@Service
public class ChatbotService {

    private final OpenAIClient client;

    public ChatbotService(OpenAIClient client) {
        this.client = client;
    }

    public ContentPart getResponse(String message) {
        String systemMessage = """
            You are a Service Customer Service chatbot that helps customers with their issues.
            You are a friendly and helpful chatbot that is always ready to help customers with their issues.
            Only respond to messages that are related to customer service issues.
            """;
        ChatCompletionDataRequest response = new ChatCompletionDataRequest(systemMessage, message);
        return client.sendRequestChatCompletion(response);
    }
}
