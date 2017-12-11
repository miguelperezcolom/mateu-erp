package io.mateu.erp.model.financials;

import org.jdom2.JDOMException;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;

@Converter
public class CurrencyExchangeConverter implements AttributeConverter<CurrencyExchange, String> {


    @Override
    public String convertToDatabaseColumn(CurrencyExchange exchange) {
        if (exchange == null) return null;
        else return exchange.toString();
    }

    @Override
    public CurrencyExchange convertToEntityAttribute(String s) {
        if (s == null || "".equals(s)) return null;
        try {
            return new CurrencyExchange(s);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
