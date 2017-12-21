package io.mateu.erp.tests;

import com.google.common.base.Joiner;
import io.mateu.erp.dispo.KeyValue;
import io.mateu.erp.model.booking.hotel.HotelService;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.workflow.AbstractTask;
import io.mateu.ui.core.shared.UserData;
import org.easytravelapi.common.Amount;
import org.easytravelapi.hotel.Allocation;
import org.easytravelapi.hotel.BoardPrice;
import org.javamoney.moneta.CurrencyUnitBuilder;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import javax.money.*;
import java.util.*;

public class Tester {

    private static Set<Class> getSubtypes(Reflections reflections, Class c) {
        List<Class> l = new ArrayList<>();
        Set<Class> s = reflections.getSubTypesOf(c);
        l.addAll(s);
        for (Class sc : s) {
            l.addAll(getSubtypes(reflections, sc));
        }
        return new HashSet<>(l);
    }


    public static void main(String... args) throws Throwable {

        //crearReservaHotel();

        poblar();


        //otros();


    }

    private static void poblar() throws Throwable {

        TestPopulator.populateEverything();

    }

    private static void otros() {

        {
            Class c = AbstractTask.class;

            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .filterInputsBy(new FilterBuilder().add((s) -> {
                        System.out.println(s);
                        return s.endsWith(".class");
                    }))//include("\\.class"))
                    .setScanners(new SubTypesScanner()).setUrls(c.getProtectionDomain().getCodeSource().getLocation())); //c.getPackage().getName());

            Set<Class> subTypes = getSubtypes(reflections, c);

            List<String> subclases = new ArrayList<>();
            for (Class s : subTypes) {
                if (s.getCanonicalName() != null) subclases.add(s.getSimpleName());
            }

            System.out.println("subclasses = " + Joiner.on(",").join(subclases));
        }


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

    private static void crearReservaHotel() {

            UserData ud = new UserData();
            ud.setLogin("admin");

            KeyValue k = new KeyValue();
            k.setPointOfSaleId(1);
            k.setHotelId(1);
            k.setAgencyId(1);
            k.setCheckIn(20180115);
            k.setCheckOut(20180123);
            Allocation a;
            k.getAllocation().add(a = new Allocation(1, 2, null, "DBL", ""));
            BoardPrice bp;
            k.setBoardPrice(bp = new BoardPrice());
            bp.setBoardBasisId("HB");
            bp.setNetPrice(new Amount("EUR", 123.32));

            try {

                long id = HotelService.createFromKey(ud, k, "21321321", "Mr test", "Local test");

                System.out.println("service id = " + id);

            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

    }

}
