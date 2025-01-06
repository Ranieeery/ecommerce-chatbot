package dev.raniery.chatbot.infra.openai;

public record ChatCompletionDataRequest(String systemMessage, String userMessage) {
}
