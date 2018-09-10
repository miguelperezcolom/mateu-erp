package io.mateu.erp.client.fieldBuilders;

import com.vaadin.data.*;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.*;
import io.mateu.erp.model.financials.Amount;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.product.hotel.FareValue;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.data.MDDBinder;
import io.mateu.mdd.core.dataProviders.JPQLListDataProvider;
import io.mateu.mdd.core.interfaces.AbstractStylist;
import io.mateu.mdd.core.reflection.FieldInterfaced;
import io.mateu.mdd.core.reflection.ReflectionHelper;
import io.mateu.mdd.vaadinport.vaadin.components.fieldBuilders.AbstractFieldBuilder;
import lombok.Getter;
import lombok.Setter;
import org.javamoney.moneta.FastMoney;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AmountFieldBuilder extends AbstractFieldBuilder {
    @Override
    public boolean isSupported(FieldInterfaced field) {
        return FareValue.class.equals(field.getType());
    }


    @Getter@Setter
    public class DTO {

        private double value;

        private Currency currency;

        public DTO(Amount o) {
            if (o != null) {
                value = o.getValue();
                currency = o.getCurrency();
            }
        }

        public FastMoney toFastMoney() {
            return FastMoney.of(value, (currency != null)?currency.getIsoCode():"EUR");
        }
    }


    @Override
    public void build(FieldInterfaced field, Object object, Layout container, MDDBinder binder, Map<HasValue, List<Validator>> validators, AbstractStylist stylist, Map<FieldInterfaced, Component> allFieldContainers, boolean forSearchFilter) {


        Binder<DTO> subBinder = new Binder(DTO.class);

        TextField tf = new TextField();
        tf.setValueChangeMode(ValueChangeMode.BLUR);
        subBinder.forField(tf).withConverter(new StringToDoubleConverter(0.0, "Must be a valid number")).bind("value");


        ComboBox<Currency> cb = new ComboBox<>();
        cb.setDataProvider((ListDataProvider<Currency>) new JPQLListDataProvider(Currency.class));
        subBinder.forField(cb).bind("currency");


        HorizontalLayout hl;
        container.addComponent(hl = new HorizontalLayout(tf, cb));


        hl.setCaption(ReflectionHelper.getCaption(field));
        if (field.isAnnotationPresent(NotNull.class)) cb.setRequiredIndicatorVisible(true);







        Binder.BindingBuilder bindingBuilder = binder.forField(new HasValue() {

            List<ValueChangeListener> valueChangeListeners = new ArrayList<>();


            {
                subBinder.addValueChangeListener(e -> {
                    ValueChangeEvent vce = new ValueChangeEvent(tf, this, null, e.isUserOriginated());
                    valueChangeListeners.forEach(l -> l.valueChange(vce));
                });
            }

            @Override
            public void setValue(Object o) {
                subBinder.setBean(new DTO((Amount) o));
            }

            @Override
            public Object getValue() {
                try {
                    return new Amount(subBinder.getBean().toFastMoney());
                } catch (Throwable throwable) {
                    MDD.alert(throwable);
                }
                return null;
            }

            @Override
            public Registration addValueChangeListener(ValueChangeListener valueChangeListener) {
                valueChangeListeners.add(valueChangeListener);
                return new Registration() {
                    @Override
                    public void remove() {
                        valueChangeListeners.remove(valueChangeListener);
                    }
                };
            }

            @Override
            public void setRequiredIndicatorVisible(boolean b) {

            }

            @Override
            public boolean isRequiredIndicatorVisible() {
                return false;
            }

            @Override
            public void setReadOnly(boolean b) {

            }

            @Override
            public boolean isReadOnly() {
                return false;
            }
        });

        if (!forSearchFilter && field.getDeclaringClass() != null) bindingBuilder.withValidator(new BeanValidator(field.getDeclaringClass(), field.getName()));

        bindingBuilder.bind(field.getName());


    }
}
