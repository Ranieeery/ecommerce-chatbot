package dev.raniery.chatbot.infra.openai;

import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.common.Page;
import io.github.sashirestela.openai.common.content.ContentPart;
import io.github.sashirestela.openai.domain.assistant.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.Thread;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OpenAIClient {

    private final String apiKey;
    private final String assistantId;
    private final SimpleOpenAI service;
    private String threadId;

    public OpenAIClient(@Value("${app.openai.api.key}") String apiKey, @Value("${app.openai.assistant.id}") String assistantId) {
        this.apiKey = apiKey;
        this.assistantId = assistantId;
        this.service = SimpleOpenAI.builder().apiKey(apiKey).build();
    }

    public ContentPart sendRequestChatCompletion(ChatCompletionDataRequest dataRequest) {

        if (this.threadId == null) {
            var thread = service.threads().create(ThreadRequest.builder().build()).join();
            this.threadId = thread.getId();
        }

        service.threadMessages().create(this.threadId, ThreadMessageRequest.builder()
            .role(ThreadMessageRole.USER)
            .content(dataRequest.userMessage())
            .build()).join();

        ThreadRun run = service.threadRuns()
            .create(this.threadId, ThreadRunRequest.builder()
                .assistantId(assistantId)
                .build())
            .join();

        while (!run.getStatus().equals(ThreadRun.RunStatus.COMPLETED)) {
            try {
                Thread.sleep(500);
                run = service.threadRuns().getOne(this.threadId, run.getId()).join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        Page<ThreadMessage> messages = service.threadMessages().getList(threadId).join();

        return messages.getData().stream().max(Comparator.comparing(ThreadMessage::getCreatedAt)).get().getContent().getFirst();
    }


    public List<ContentPart> loadChat() {
        List<ContentPart> messages = new ArrayList<>();

        if (this.threadId != null) {
            messages.addAll(service.threadMessages().getList(threadId).join().getData()
                .stream()
                .sorted(Comparator.comparing(ThreadMessage::getCreatedAt))
                .map(ThreadMessage::getContent)
                .flatMap(List::stream)
                .toList());
        }

        return messages;
    }
}
