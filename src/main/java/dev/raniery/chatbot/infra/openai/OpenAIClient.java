package dev.raniery.chatbot.infra.openai;

import dev.raniery.chatbot.domain.ShippingDetails;
import dev.raniery.chatbot.domain.service.ShippingCalculator;
import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.common.Page;
import io.github.sashirestela.openai.common.content.ContentPart;
import io.github.sashirestela.openai.common.function.FunctionCall;
import io.github.sashirestela.openai.common.function.FunctionDef;
import io.github.sashirestela.openai.common.function.FunctionExecutor;
import io.github.sashirestela.openai.domain.assistant.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.Thread;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class OpenAIClient {

    private final String apiKey;
    private final String assistantId;
    private final SimpleOpenAI service;
    private final ShippingCalculator shippingCalculator;

    private String threadId;

    public OpenAIClient(@Value("${app.openai.api.key}") String apiKey, @Value("${app.openai.assistant.id}") String assistantId, ShippingCalculator shippingCalculator) {
        this.apiKey = apiKey;
        this.assistantId = assistantId;
        this.service = SimpleOpenAI.builder().apiKey(apiKey).build();
        this.shippingCalculator = shippingCalculator;
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
                Thread.sleep(500);
                run = service.threadRuns().getOne(this.threadId, run.getId()).join();
                completed = run.getStatus().equals(ThreadRun.RunStatus.COMPLETED);
                requiredAction = run.getRequiredAction() != null;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (requiredAction) {
            return ContentPart.builder().build();
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

    public void clearChat() {
        if (this.threadId != null) {
            service.threads().delete(this.threadId).join();
            this.threadId = null;
        }
    }

    public void callShippingCalculator(ThreadRun run) {
        try {
            var function = run.getRequiredAction().getSubmitToolOutputs().getToolCalls().get(0).getFunction();
            var calculateShippingFunction = FunctionDef.builder()
                .name("shippingCalculator")
                .functionalClass(ShippingDetails.class, d -> shippingCalculator.calc(d))
                .build();

        } catch (Exception e) {
            throw new IllegalStateException("Could not call shipping calculator");
        }
    }
}
