package dev.raniery.chatbot.domain.service;

import dev.raniery.chatbot.domain.ShippingDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ShippingCalculator {

    public BigDecimal calc(ShippingDetails details) {
        //lógica para cálculo de frete aqui...

        return new BigDecimal("3.45").multiply(new BigDecimal(details.productCount()));
    }

}
