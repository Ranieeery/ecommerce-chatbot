package dev.raniery.chatbot.infra.openai;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.raniery.chatbot.domain.service.ShippingCalculator;
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

@Component
public class OpenAIClient {

    private final String assistantId;
    private final SimpleOpenAI service;

    private String threadId;

    public OpenAIClient(@Value("${app.openai.api.key}") String apiKey, @Value("${app.openai.assistant.id}") String assistantId) {
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

        var completed = false;
        var requiredAction = false;
        while (!completed && !requiredAction) {
            try {
                Thread.sleep(200);
                run = service.threadRuns().getOne(this.threadId, run.getId()).join();
                completed = run.getStatus().equals(ThreadRun.RunStatus.COMPLETED);
                requiredAction = run.getRequiredAction() != null;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (requiredAction) {
            String shippingPrice = callShippingCalculator(run);
            var submitRequest = ThreadRunSubmitOutputRequest.builder()
                .toolOutputs(List.of(ThreadRunSubmitOutputRequest.ToolOutput.builder()
                    .toolCallId(run.getRequiredAction().getSubmitToolOutputs().getToolCalls().get(0).getId())
                    .output(shippingPrice)
                    .build()
                )).build();

            run = service.threadRuns()
                .submitToolOutput(this.threadId, run.getId(), submitRequest)
                .join();

            completed = false;
            while (!completed) {
                try {
                    Thread.sleep(200);
                    run = service.threadRuns().getOne(this.threadId, run.getId()).join();
                    completed = run.getStatus().equals(ThreadRun.RunStatus.COMPLETED);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread interrupted while waiting for completion", e);
                }
            }
        }

        Page<ThreadMessage> messages = service.threadMessages().getList(threadId).join();

        return messages.getData().stream().max(Comparator.comparing(ThreadMessage::getCreatedAt)).get().getContent().get(0);
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

    public void clearChat() {
        if (this.threadId != null) {
            service.threads().delete(this.threadId).join();
            this.threadId = null;
        }
    }

    public String callShippingCalculator(ThreadRun run) {
        try {
            var toolCalls = run.getRequiredAction().getSubmitToolOutputs().getToolCalls().get(0).getFunction();
            var calculator = new ShippingCalculator();

            ObjectMapper mapper = new ObjectMapper();
            mapper.readerForUpdating(calculator).readValue(toolCalls.getArguments());

            return calculator.execute().toString();
        } catch (Exception e) {
            throw new RuntimeException("Error calling shipping calculator", e);
        }
    }
}
