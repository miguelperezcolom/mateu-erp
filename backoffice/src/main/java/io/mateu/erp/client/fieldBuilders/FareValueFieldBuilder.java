package io.mateu.erp.client.fieldBuilders;

import com.vaadin.data.*;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
import io.mateu.erp.model.product.hotel.FareValue;
import io.mateu.mdd.core.data.MDDBinder;
import io.mateu.mdd.core.interfaces.AbstractStylist;
import io.mateu.mdd.core.reflection.FieldInterfaced;
import io.mateu.mdd.core.reflection.ReflectionHelper;
import io.mateu.mdd.vaadinport.vaadin.components.fieldBuilders.AbstractFieldBuilder;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

public class FareValueFieldBuilder extends AbstractFieldBuilder {
    @Override
    public boolean isSupported(FieldInterfaced field) {
        return FareValue.class.equals(field.getType());
    }

    @Override
    public void build(FieldInterfaced field, Object object, Layout container, MDDBinder binder, Map<HasValue, List<Validator>> validators, AbstractStylist stylist, Map<FieldInterfaced, Component> allFieldContainers, boolean forSearchFilter) {

        TextField tf;
        container.addComponent(tf = new TextField());
        tf.setValueChangeMode(ValueChangeMode.BLUR);

        tf.setCaption(ReflectionHelper.getCaption(field));

        if (field.isAnnotationPresent(NotNull.class)) tf.setRequiredIndicatorVisible(true);





        Binder.BindingBuilder bindingBuilder = binder.forField(tf);

        bindingBuilder.withConverter(new Converter() {
            @Override
            public Result convertToModel(Object o, ValueContext valueContext) {
                return Result.ok((o != null)?new FareValue((String) o):null);
            }

            @Override
            public Object convertToPresentation(Object o, ValueContext valueContext) {
                return (o != null)?o.toString():"";
            }
        });

        if (!forSearchFilter && field.getDeclaringClass() != null) bindingBuilder.withValidator(new BeanValidator(field.getDeclaringClass(), field.getName()));

        bindingBuilder.bind(field.getName());



    }
}
