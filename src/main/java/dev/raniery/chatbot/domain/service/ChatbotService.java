package dev.raniery.chatbot.domain.service;

import dev.raniery.chatbot.infra.openai.ChatCompletionDataRequest;
import dev.raniery.chatbot.infra.openai.OpenAIClient;
import io.github.sashirestela.openai.common.content.ContentPart;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatbotService {

    private final OpenAIClient client;

    public ChatbotService(OpenAIClient client) {
        this.client = client;
    }

    public ContentPart getResponse(String message) {
        String systemMessage = """
            You're a customer service chatbot who helps customers with their problems.
            You are a friendly and helpful chatbot who is always ready to help customers with their problems.
            Only respond to messages related to customer service issues or product recommendations.
            Consider that we stock any and all products related in the store e-commerce description.
            If the customer asks for recommendations, reply with 2-3 recommendations based on what they asked for.
            Reply in the language of the user's last message.
            """;
        ChatCompletionDataRequest response = new ChatCompletionDataRequest(systemMessage, message);
        return client.sendRequestChatCompletion(response);
    }

    public List<ContentPart> loadHistory() {
        return client.loadChat();
    }

    public void clearHistory() {
        client.clearChat();
    }
}
