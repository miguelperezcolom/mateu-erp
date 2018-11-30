package io.mateu.erp.model.product.hotel;

import com.google.common.collect.Lists;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ErrorMessage;
import com.vaadin.shared.ui.ErrorLevel;
import com.vaadin.ui.*;
import io.mateu.mdd.core.CSS;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.util.Helper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class InventoryCalendar extends VerticalLayout {

    private final ComboBox<Inventory> comboCupo;
    private final ComboBox<RoomType> comboHabitacion;
    private final ComboBox<String> modo;
    private Button botonRefrescar;
    private final CssLayout capaCalendario;
    private final InventoryCalendarNavigator nav;
    private ComboBox<Hotel> comboHotel;
    private InventoryCalendarCube cubo;

    public InventoryCalendar() {
        this(null);
    }

    public InventoryCalendar(Inventory inventory) {

        setSizeFull();
        addStyleName(CSS.NOPADDING);

        HorizontalLayout hl;
        addComponent(hl = new HorizontalLayout());

        try {
            hl.addComponent(comboHotel = new ComboBox<Hotel>("Hotel", Helper.selectObjects("select x from " + Hotel.class.getName() + " x order by x.name")));
            comboHotel.setWidth("400px");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        hl.addComponent(comboCupo = new ComboBox<Inventory>("Inventory", (inventory != null)?inventory.getHotel().getInventories():new ArrayList<>()));
        comboCupo.setWidth("400px");

        addComponent(hl = new HorizontalLayout());

        hl.addComponent(comboHabitacion = new ComboBox<RoomType>("Room", (inventory != null)?inventory.getHotel().getRooms().stream().map(r -> r.getType()).collect(Collectors.toList()):new ArrayList<>()));
        comboHabitacion.setWidth("400px");


        if (inventory != null) {
            comboHotel.setValue(inventory.getHotel());
            comboCupo.setValue(inventory);
            if (inventory.getHotel().getRooms().size() > 0) comboHabitacion.setValue(inventory.getHotel().getRooms().get(0).getType());
        }

        /*
        hl.addComponent(botonRefrescar = new Button("Refresh", VaadinIcons.REFRESH));

        botonRefrescar.addClickListener(e -> refrescar());
        */
        
        hl.addComponent(nav = new InventoryCalendarNavigator(this));
        nav.setCaption("Period");

        hl.addComponent(modo = new ComboBox<String>("Mode", Lists.newArrayList("Weekly", "Monthly")));
        modo.setValue("Weekly");
        modo.setTextInputAllowed(false);
        modo.setEmptySelectionAllowed(false);
        modo.addValueChangeListener(e -> refrescar());



        addComponent(capaCalendario = new CssLayout());
        capaCalendario.addStyleName("calendariocupo");

        comboHotel.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                comboCupo.setDataProvider(new ListDataProvider<>(e.getValue().getInventories()));
                comboCupo.setValue(e.getValue().getInventories().size() > 0?e.getValue().getInventories().get(0):null);

                comboHabitacion.setDataProvider(new ListDataProvider<>(e.getValue().getRooms().stream().map(r -> r.getType()).collect(Collectors.toList())));
                comboHabitacion.setValue(e.getValue().getRooms().size() > 0?e.getValue().getRooms().get(0).getType():null);
            }
        });

        comboHabitacion.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                refrescar();
            }
        });

        comboCupo.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                refrescar();
            }
        });


        CssLayout espaciador;
        addComponent(espaciador = new CssLayout());
        setExpandRatio(espaciador, 1);

        refrescar();
    }

    protected void refrescar() {

        capaCalendario.removeAllComponents();
        capaCalendario.addStyleName("calendariocupo");

        if (comboHabitacion.getValue() != null) {


            try {
                cubo = new InventoryCalendarCube(comboCupo.getValue());

                LocalDate desde = nav.getFecha();

                capaCalendario.addComponent(construirMes(desde));
                capaCalendario.addComponent(construirMes(desde.plusMonths(1)));

            } catch (Throwable throwable) {
                throwable.printStackTrace();

                MDD.alert(throwable);

            }



        } else {
            capaCalendario.addComponent(new Label("You must select a hotel + inventory + room."));
        }

    }

    private Component construirMes(LocalDate desde) {
        if ("weekly".equalsIgnoreCase(modo.getValue())) return construirMesWeekly(desde);
        else return construirMesMonthly(desde);
    }

    private Component construirMesMonthly(LocalDate desde) {
        VerticalLayout mes = new VerticalLayout();
        mes.addStyleName("mes");
        mes.addStyleName("monthly");
        mes.setWidthUndefined();


        Label l;
        mes.addComponent(l = new Label("" + desde.getMonth().toString() + " " + desde.getYear() + ": occupation = 75%"));
        l.addStyleName("titulo");


        HorizontalLayout fila;
        {
            mes.addComponent(fila = new HorizontalLayout());
            fila.addStyleName("fila");
            fila.addStyleName("calendario");

            fila.addComponent(l = new Label("" + desde.getMonth().toString() + " " + desde.getYear()));
            l.addStyleName("titulo");
            l.setWidth("200px");


            LocalDate f = desde.plusDays(0);
            while (f.getMonth().equals(desde.getMonth()) || f.getDayOfMonth() != 1) {
                fila.addComponent(l = new Label("" + f.getDayOfMonth()));
                l.addStyleName("dia");

                if (DayOfWeek.SATURDAY.equals(f.getDayOfWeek()) || DayOfWeek.SUNDAY.equals(f.getDayOfWeek())) {
                    l.addStyleName("finde");
                }

                if (DayOfWeek.SUNDAY.equals(f.getDayOfWeek())) {
                    l.addStyleName("domingo");
                }

                f = f.plusDays(1);
            }
        }


        {
            mes.addComponent(fila = new HorizontalLayout());
            fila.addStyleName("fila");
            fila.addStyleName("contratado");

            fila.addComponent(l = new Label("Contract"));
            l.addStyleName("titulo");
            l.setWidth("200px");


            LocalDate f = desde.plusDays(0);
            while (f.getMonth().equals(desde.getMonth()) || f.getDayOfMonth() != 1) {
                int[] cupo = cubo.getCubo(f, comboHabitacion.getValue());
                fila.addComponent(l = new Label("" + cupo[0]));
                l.addStyleName("dia");
                if (DayOfWeek.SUNDAY.equals(f.getDayOfWeek())) {
                    l.addStyleName("domingo");
                }
                f = f.plusDays(1);
            }
        }

        {
            mes.addComponent(fila = new HorizontalLayout());
            fila.addStyleName("fila");
            fila.addStyleName("disponible");

            fila.addComponent(l = new Label("Available"));
            l.addStyleName("titulo");
            l.setWidth("200px");


            LocalDate f = desde.plusDays(0);
            while (f.getMonth().equals(desde.getMonth()) || f.getDayOfMonth() != 1) {
                int[] cupo = cubo.getCubo(f, comboHabitacion.getValue());
                fila.addComponent(l = new Label("" + cupo[2]));
                l.addStyleName("dia");
                if (DayOfWeek.SUNDAY.equals(f.getDayOfWeek())) {
                    l.addStyleName("domingo");
                }
                f = f.plusDays(1);
            }
        }

        {
            mes.addComponent(fila = new HorizontalLayout());
            fila.addStyleName("fila");
            fila.addStyleName("reservado");

            fila.addComponent(l = new Label("Booked"));
            l.addStyleName("titulo");
            l.setWidth("200px");


            LocalDate f = desde.plusDays(0);
            while (f.getMonth().equals(desde.getMonth()) || f.getDayOfMonth() != 1) {
                int[] cupo = cubo.getCubo(f, comboHabitacion.getValue());
                fila.addComponent(l = new Label("" + cupo[1]));
                l.addStyleName("dia");
                if (DayOfWeek.SUNDAY.equals(f.getDayOfWeek())) {
                    l.addStyleName("domingo");
                }
                f = f.plusDays(1);
            }
        }

        {
            mes.addComponent(fila = new HorizontalLayout());
            fila.addStyleName("fila");
            fila.addStyleName("reservadoagencia");

            fila.addComponent(l = new Label("Turchese"));
            l.addStyleName("titulo");
            l.setWidth("200px");


            LocalDate f = desde.plusDays(0);
            while (f.getMonth().equals(desde.getMonth()) || f.getDayOfMonth() != 1) {
                fila.addComponent(l = new Label("" + f.getDayOfMonth()));
                l.addStyleName("dia");
                if (DayOfWeek.SUNDAY.equals(f.getDayOfWeek())) {
                    l.addStyleName("domingo");
                }
                f = f.plusDays(1);
            }
        }

        {
            mes.addComponent(fila = new HorizontalLayout());
            fila.addStyleName("fila");
            fila.addStyleName("reservadoagencia");

            fila.addComponent(l = new Label("DTS"));
            l.addStyleName("titulo");
            l.setWidth("200px");


            LocalDate f = desde.plusDays(0);
            while (f.getMonth().equals(desde.getMonth()) || f.getDayOfMonth() != 1) {
                fila.addComponent(l = new Label("" + f.getDayOfMonth()));
                l.addStyleName("dia");
                if (DayOfWeek.SUNDAY.equals(f.getDayOfWeek())) {
                    l.addStyleName("domingo");
                }
                f = f.plusDays(1);
            }
        }

        {
            mes.addComponent(fila = new HorizontalLayout());
            fila.addStyleName("fila");
            fila.addStyleName("reservadoagencia");

            fila.addComponent(l = new Label("Muchoviaje"));
            l.addStyleName("titulo");
            l.setWidth("200px");


            LocalDate f = desde.plusDays(0);
            while (f.getMonth().equals(desde.getMonth()) || f.getDayOfMonth() != 1) {
                fila.addComponent(l = new Label("" + f.getDayOfMonth()));
                l.addStyleName("dia");
                if (DayOfWeek.SUNDAY.equals(f.getDayOfWeek())) {
                    l.addStyleName("domingo");
                }
                f = f.plusDays(1);
            }
        }

        {
            mes.addComponent(fila = new HorizontalLayout());
            fila.addStyleName("fila");
            fila.addStyleName("reservadoagencia");

            fila.addComponent(l = new Label("Directos"));
            l.addStyleName("titulo");
            l.setWidth("200px");


            LocalDate f = desde.plusDays(0);
            while (f.getMonth().equals(desde.getMonth()) || f.getDayOfMonth() != 1) {
                fila.addComponent(l = new Label("" + f.getDayOfMonth()));
                l.addStyleName("dia");
                if (DayOfWeek.SUNDAY.equals(f.getDayOfWeek())) {
                    l.addStyleName("domingo");
                }
                f = f.plusDays(1);
            }
        }

        return mes;
    }

    private Component construirMesWeekly(LocalDate desde) {
        VerticalLayout vl = new VerticalLayout();
        vl.addStyleName("mes");
        vl.addStyleName("weekly");
        vl.setWidthUndefined();

        Label titulo;
        vl.addComponent(titulo = new Label());
        titulo.addStyleName("titulo");
        titulo.setValue("" + desde.getMonth().toString() + " " + desde.getYear());

        LocalDate f = desde.plusDays(0);

        int diasEnBlanco = 0;
        switch (desde.getDayOfWeek()) {
            case TUESDAY: diasEnBlanco = 1; break;
            case WEDNESDAY: diasEnBlanco = 2; break;
            case THURSDAY: diasEnBlanco = 3; break;
            case FRIDAY: diasEnBlanco = 4; break;
            case SATURDAY: diasEnBlanco = 5; break;
            case SUNDAY: diasEnBlanco = 6; break;
        }
        f = f.minusDays(diasEnBlanco);

        HorizontalLayout semana = null;
        while (f.getMonth().equals(desde.getMonth()) || f.getDayOfMonth() != 1) {
            if (semana == null || DayOfWeek.MONDAY.equals(f.getDayOfWeek())) {
                semana = new HorizontalLayout();
                semana.addStyleName("semana");
                vl.addComponent(semana);
            }

            VerticalLayout dia;
            semana.addComponent(dia = new VerticalLayout());
            dia.addStyleName("dia");

            if (!DayOfWeek.MONDAY.equals(f.getDayOfWeek())) {
                dia.addStyleName("notfirst");
            }

            if (1 == f.plusDays(1).getDayOfMonth() && !DayOfWeek.SUNDAY.equals(f.getDayOfWeek()) && f.getMonth().equals(desde.getMonth())) {
                dia.addStyleName("last");
            }

            Label tituloDia;
            dia.addComponent(tituloDia = new Label());
            tituloDia.addStyleName("titulo");
            tituloDia.setValue("" + f.getDayOfMonth());

            if (f.getMonth().equals(desde.getMonth())) {

                int[] cupo = cubo.getCubo(f, comboHabitacion.getValue());

                HorizontalLayout infoCupo;
                dia.addComponent(infoCupo = new HorizontalLayout());
                infoCupo.addStyleName("infoCupo");

                Label disponible;
                infoCupo.addComponent(disponible = new Label());
                disponible.addStyleName("disponible");
                disponible.setValue("" + cupo[2]);

                VerticalLayout dcha;
                infoCupo.addComponent(dcha = new VerticalLayout());
                dcha.addStyleName("dcha");

                Label contratado;
                dcha.addComponent(contratado = new Label());
                contratado.addStyleName("contratado");
                contratado.setValue("" + cupo[0]);

                Label vendido;
                dcha.addComponent(vendido = new Label());
                vendido.addStyleName("vendido");
                vendido.setValue("" + cupo[1]);


                Label desviado;
                infoCupo.addComponent(desviado = new Label());
                desviado.addStyleName("desviado");
                desviado.setValue("15");

                dia.addLayoutClickListener(e -> System.out.println("click!!!"));

            } else {

                dia.addStyleName("enblanco");

            }



            f = f.plusDays(1);
        }

        return vl;
    }


    @Override
    public String toString() {
        return "Inventory calendar";
    }
}
