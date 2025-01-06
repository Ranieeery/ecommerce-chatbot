package dev.raniery.chatbot.web.controller;

import dev.raniery.chatbot.web.dto.QuestionDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping({"/", "chat"})
public class ChatController {

    private static final String CHAT_PAGE = "chat";

    @GetMapping
    public String loadChatbotPage() {
        return CHAT_PAGE;
    }

    @PostMapping
    @ResponseBody
    public String answerQuestion(@RequestBody QuestionDto dto) {
        return dto.question();
    }

    @GetMapping("clean")
    public String cleanChat() {
        return CHAT_PAGE;
    }

}
