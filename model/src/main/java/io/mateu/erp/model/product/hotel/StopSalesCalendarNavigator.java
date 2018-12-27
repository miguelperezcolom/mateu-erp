package io.mateu.erp.model.product.hotel;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import java.time.LocalDate;

public class StopSalesCalendarNavigator extends HorizontalLayout {

    private final Label titulo;
    private final StopSalesCalendar calendario;
    LocalDate fecha = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), 1);

    private final Button botonAnterior;
    private final Button botonSiguiente;

    public StopSalesCalendarNavigator(StopSalesCalendar calendario) {

        this.calendario = calendario;

        addStyleName("navegadorcalendario");

        addComponent(botonAnterior = new Button(VaadinIcons.ARROW_LEFT));
        addComponent(titulo = new Label());
        titulo.addStyleName("titulo");
        addComponent(botonSiguiente = new Button(VaadinIcons.ARROW_RIGHT));

        botonAnterior.addClickListener(e -> retroceder());
        botonSiguiente.addClickListener(e -> avanzar());

        refrescar(true);

    }

    public void retroceder() {
        fecha = fecha.minusMonths(1);
        refrescar();
    }

    public void avanzar() {
        fecha = fecha.plusMonths(1);
        refrescar();
    }

    private void refrescar() {
        refrescar(false);
    }

    private void refrescar(boolean soloTitulo) {

        titulo.setValue("" + fecha.getYear() + " " + fecha.getMonth().toString() + " - " + fecha.plusMonths(1).getMonth().toString());

        if (!soloTitulo) calendario.refrescar();
    }

    public LocalDate getFecha() {
        return fecha;
    }
}
