package dev.raniery.chatbot.web.dto;

import io.github.sashirestela.openai.common.content.ContentPart;

import java.util.List;
import java.util.stream.Collectors;

public record ChatResponse(ContentPart content) {
    public static ChatResponse from(ContentPart content) {
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }
        return new ChatResponse(content);
    }

    public static List<String> extractMessages(List<ContentPart> contents) {
        return contents.stream()
            .filter(content -> content instanceof ContentPart.ContentPartTextAnnotation)
            .map(content -> ((ContentPart.ContentPartTextAnnotation) content).getText().getValue())
            .collect(Collectors.toList());
    }

    public String getText() {
        if (content instanceof ContentPart.ContentPartTextAnnotation textContent) {
            return textContent.getText().getValue();
        }
        throw new IllegalStateException("Content is not text");
    }
}