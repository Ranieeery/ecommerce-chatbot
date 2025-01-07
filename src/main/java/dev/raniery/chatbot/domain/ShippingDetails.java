package dev.raniery.chatbot.domain;

import dev.raniery.chatbot.domain.service.ShippingCalculator;
import io.github.sashirestela.openai.common.function.Functional;

public record ShippingDetails(Integer productCount, UF uf) implements Functional {

    @Override
    public Object execute() {
        return shippingCalculator.calc(this);
    }
}
