package dev.raniery.chatbot.web.dto;

import io.github.sashirestela.openai.common.content.ContentPart;

public record ChatResponse(ContentPart content) {
    public static ChatResponse from(ContentPart content) {
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }
        return new ChatResponse(content);
    }

    public String getText() {
        if (content instanceof ContentPart.ContentPartTextAnnotation textContent) {
            return textContent.getText().getValue();
        }
        throw new IllegalStateException("Content is not text");
    }
}