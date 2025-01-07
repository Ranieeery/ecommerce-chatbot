package dev.raniery.chatbot.web.controller;

import dev.raniery.chatbot.domain.service.ChatbotService;
import dev.raniery.chatbot.web.dto.QuestionDto;
import io.github.sashirestela.openai.domain.chat.Chat;
import io.github.sashirestela.openai.domain.chat.Chat.Choice;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.util.stream.Stream;

@Controller
@RequestMapping({"/", "chat"})
public class ChatController {

    private static final String CHAT_PAGE = "chat";
    private final ChatbotService service;

    public ChatController(ChatbotService service) {
        this.service = service;
    }

    @GetMapping
    public String loadChatbotPage() {
        return CHAT_PAGE;
    }

    @PostMapping
    @ResponseBody
    public ResponseBodyEmitter answerQuestion(@RequestBody QuestionDto dto) {
        Stream<Chat> response = service.getResponse(dto.question());
        var emitter = new ResponseBodyEmitter();

            new Thread(() -> {
                try {
                    response.forEach(message -> {
                        try {
                            if (message != null && message.getChoices() != null && !message.getChoices().isEmpty()) {
                                Choice token = message.getChoices().getFirst();
                                if (token != null && token.getMessage() != null) {
                                    String content = token.getMessage().getContent();

                                    if (content != null && !content.isBlank()) {
                                        emitter.send(content);
                                    }
                                }
                            }
                        } catch (IOException e) {
                            emitter.completeWithError(e);
                        }
                    });
                    emitter.complete();
                } catch (Exception e) {
                    emitter.completeWithError(e);
                }
            }).start();

        return emitter;
    }
}
