package dev.raniery.chatbot.domain.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import dev.raniery.chatbot.domain.UF;
import io.github.sashirestela.openai.common.function.Functional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ShippingCalculator implements Functional {

    @JsonPropertyDescription("State abbreviation")
    @JsonProperty(required = true)
    private UF uf;

    @JsonPropertyDescription("Product quantity in the cart")
    @JsonProperty(required = true)
    private Integer productQuantity;

    public BigDecimal calc() {
        if (productQuantity == null || uf == null) {
            throw new IllegalStateException("Product quantity and UF must be set");
        }
        return new BigDecimal("3.45").multiply(new BigDecimal(productQuantity));
    }

    @Override
    public Object execute() {
        return calc();
    }

    public void setUf(UF uf) {
        this.uf = uf;
    }

    public void setProductQuantity(Integer productQuantity) {
        this.productQuantity = productQuantity;
    }
}
