package dev.raniery.chatbot.web.controller;

import dev.raniery.chatbot.domain.service.ChatbotService;
import dev.raniery.chatbot.web.dto.ChatResponse;
import dev.raniery.chatbot.web.dto.QuestionDto;
import io.github.sashirestela.openai.common.content.ContentPart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping({"/", "chat"})
public class ChatController {

    private static final String CHAT_PAGE = "chat";
    private final ChatbotService service;

    public ChatController(ChatbotService service) {
        this.service = service;
    }

    @GetMapping("clear")
    public String limparConversa() {
        service.clearHistory();

        return "redirect:/chat";
    }

    @GetMapping
    public String loadChatbotPage(Model model) {
        List<ContentPart> history = service.loadHistory();
        List<String> messages = ChatResponse.extractMessages(history);
        model.addAttribute("history", messages);
        return CHAT_PAGE;
    }

    @PostMapping
    @ResponseBody
    public String answerQuestion(@RequestBody QuestionDto dto) {
        ContentPart response = service.getResponse(dto.question());

        var answer = new ChatResponse(response);

        return answer.getText();
    }
}
