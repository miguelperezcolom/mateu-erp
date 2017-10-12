package io.mateu.erp.tests;

import org.javamoney.moneta.CurrencyUnitBuilder;

import javax.money.*;
import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import java.util.Currency;
import java.util.Locale;

public class Tester {

    public static void main(String... args) {
        for (Currency c : Currency.getAvailableCurrencies()) {
            System.out.println(c.getCurrencyCode() + "/" + c.getDisplayName() + "/" + c.getNumericCode());
        }


        CurrencyUnit  pesosCop = Monetary.getCurrency("COP");
        CurrencyUnit  pesosColombia = Monetary.getCurrency(new Locale("es",  "CO"));
        CurrencyUnit  usDolars = Monetary.getCurrency(Locale.US);
//Así podemos  crear una moneda personalizada y registrarla para su posterior uso
        CurrencyUnit  moneda = CurrencyUnitBuilder.of("XBT", "default")
                .setNumericCode(-1)
                .setDefaultFractionDigits(3)
                .build();


        System.out.printf("Cod  = %s\n", pesosCop.getCurrencyCode());
        System.out.printf("Cod  Num = %d\n", pesosCop.getNumericCode());
        System.out.printf("Dígitos  fracc = %d\n", pesosCop.getDefaultFractionDigits());



        CurrencyUnit  dolarUS = Monetary.getCurrency("USD");
        MonetaryAmount valorCop = Monetary.getDefaultAmountFactory()
                .setCurrency("COP")
                .setNumber(500_000).create();
        MonetaryAmount  valorUsd = Monetary.getDefaultAmountFactory()
                .setCurrency(dolarUS)
                .setNumber(500.55)
                .create();



        System.out.printf("Moneda  = %s\n", valorUsd.getCurrency());
        System.out.printf("Cantidad  = %s\n", valorUsd.getNumber());
        System.out.printf("Precisión  = %d\n", valorUsd.getNumber().getPrecision());
        System.out.printf("Escala  = %d\n", valorUsd.getNumber().getScale());
        System.out.printf("Numerador  fracción = %d\n",
                valorUsd.getNumber().getAmountFractionNumerator());
        System.out.printf("Denominador  fracción = %d\n",
                valorUsd.getNumber().getAmountFractionDenominator());


        try {
            valorUsd.add(valorCop);
        } catch (Exception e) {
            e.printStackTrace();
        }


        MonetaryAmount  valorCop100 = Monetary.getDefaultAmountFactory()
                .setCurrency("COP")
                .setNumber(100_000)
                .create();
        MonetaryAmount valorCopFinal =  valorCop.subtract(valorCop100)
                .multiply(2)
                .add(valorCop100);
        System.out.printf("Valor Final =  %s\n", valorCopFinal);



        MonetaryOperator duplicador = v ->  v.multiply(2);
        System.out.printf("Valor Duplicado  = %s\n", valorCop.with(duplicador));



        MonetaryQuery positivoQuery = v -> v.isPositive();
        System.out.printf("Valor  = %s\n", valorCop);
        System.out.printf("Valor positivo?  = %s\n", valorCop.query(positivoQuery));




        MonetaryAmount  valor = Monetary.getDefaultAmountFactory().setCurrency("COP")
                .setNumber(500_000.3472).create();
        MonetaryRounding  roundingCop = Monetary.getRounding(Monetary.getCurrency("COP"));
        System.out.printf("COP  redondeado = %s\n", valor.with(roundingCop));




        MonetaryRounding  rounding = Monetary.getRounding(
                RoundingQueryBuilder.of()
                        .setRoundingName("cashRounding")
                        .setCurrency(Monetary.getCurrency("CHF"))
                        .build());




    }

}
