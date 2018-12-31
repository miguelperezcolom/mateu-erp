package io.mateu.erp.model.product.hotel;

import com.google.common.collect.Lists;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import io.mateu.erp.model.booking.parts.HotelBookingLine;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.mdd.core.CSS;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.util.Helper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InventoryCalendar extends VerticalLayout {

    private final ComboBox<Inventory> comboCupo;
    private ComboBox<RoomType> comboHabitacion;
    private final ComboBox<String> modo;
    private final Panel capaDetalle;
    private final VerticalLayout listaDetalle;
    private final Panel panelScrollable;
    private final VerticalLayout contenidoPanel;
    private final DateField del;
    private final DateField al;
    private final ComboBox<Integer> qt;
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
        addStyleName("calendariocupoocierres");

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

        Button botonRefrescarCupo;
        HorizontalLayout aux;
        hl.addComponent(aux = new HorizontalLayout(botonRefrescarCupo = new Button(VaadinIcons.REFRESH)));
        aux.setCaption("Refresh");
        botonRefrescarCupo.addClickListener(e -> {

            try {

                Hotel h = comboHotel.getValue();
                Inventory i = comboCupo.getValue();
                RoomType rt = comboHabitacion.getValue();

                comboHotel.setValue(null);
                comboCupo.setValue(null);
                comboHabitacion.setValue(null);

                comboHotel.setDataProvider(new ListDataProvider<Hotel>(Helper.selectObjects("select x from " + Hotel.class.getName() + " x order by x.name")));
                comboCupo.setDataProvider(new ListDataProvider<>((h != null)?h.getInventories():new ArrayList<>()));
                comboHabitacion.setDataProvider(new ListDataProvider<>((h != null)?h.getRooms().stream().map(r -> r.getType()).collect(Collectors.toList()):new ArrayList<>()));

                for (Hotel x : ((ListDataProvider<Hotel>)comboHotel.getDataProvider()).getItems()) if (x.getId() == h.getId()) comboHotel.setValue(x);
                for (Inventory x : ((ListDataProvider<Inventory>)comboCupo.getDataProvider()).getItems()) if (x.getId() == h.getId()) comboCupo.setValue(x);
                for (RoomType x : ((ListDataProvider<RoomType>)comboHabitacion.getDataProvider()).getItems()) if (x.getCode() == rt.getCode()) comboHabitacion.setValue(x);


            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

            refrescar();
        });

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


        addComponent(hl = new HorizontalLayout());
        hl.addStyleName("formulario");
        hl.addComponent(new Label("From: "));
        hl.addComponent(del = new DateField());
        hl.addComponent(new Label("To: "));
        hl.addComponent(al = new DateField());
        hl.addComponent(new Label("Quantity: "));
        Collection<Integer> qts = new ArrayList<>();
        for (int i = 1; i < 100; i++) qts.add(i);
        hl.addComponent(qt = new ComboBox<Integer>(null, qts));
        qt.setEmptySelectionAllowed(false);
        qt.setTextInputAllowed(false);
        qt.setValue(1);


        Button botonAbrir;
        hl.addComponent(botonAbrir = new Button("set", VaadinIcons.CHECK_CIRCLE_O));
        botonAbrir.addStyleName(ValoTheme.BUTTON_PRIMARY);
        botonAbrir.addClickListener(e -> set());



        addComponent(panelScrollable = new Panel(contenidoPanel = new VerticalLayout()));
        panelScrollable.addStyleName("panelcalendario");
        panelScrollable.addStyleName(ValoTheme.PANEL_BORDERLESS);
        panelScrollable.addStyleName(CSS.NOPADDING);
        contenidoPanel.addStyleName(CSS.NOPADDING);

        contenidoPanel.addComponent(capaCalendario = new CssLayout());
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


        contenidoPanel.addComponent(capaDetalle = new Panel(listaDetalle = new VerticalLayout()));
        setExpandRatio(panelScrollable, 1);

        /*
        CssLayout espaciador;
        addComponent(espaciador = new CssLayout());
        setExpandRatio(espaciador, 1);
        */

        refrescar();
    }

    private void set() {
        Inventory h;
        RoomType r;
        if ((h = comboCupo.getValue()) != null && (r = comboHabitacion.getValue()) != null && del.getValue() != null && al.getValue() != null) {
            try {

                Helper.transact(em -> {
                    InventoryOperation o = new InventoryOperation();
                    o.setInventory(h);
                    o.setAction(InventoryAction.SET);
                    o.setStart(del.getValue());
                    o.setEnd(al.getValue());
                    if (comboHabitacion.getValue() != null) o.setRoom(comboHabitacion.getValue());
                    o.setQuantity(qt.getValue());
                    o.setCreated(LocalDateTime.now());
                    o.setCreatedBy(MDD.getCurrentUser());
                    em.persist(o);
                });
            } catch (Throwable throwable) {
                MDD.alert(throwable);
            }
            refrescar();
        }
    }

    protected void refrescar() {

        listaDetalle.removeAllComponents();

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

    private Component construirMes(LocalDate desde) throws Throwable {
        if ("weekly".equalsIgnoreCase(modo.getValue())) return construirMesWeekly(desde);
        else return construirMesMonthly(desde);
    }

    private Component construirMesMonthly(LocalDate desde) throws Throwable {
        VerticalLayout mes = new VerticalLayout();
        mes.addStyleName("mes");
        mes.addStyleName("monthly");
        mes.setWidthUndefined();


        List<StopSalesOperation> ops = (comboHotel.getValue() != null)?StopSalesCalendar.getOperations(comboHotel.getValue(), desde, desde.plusMonths(2)):new ArrayList<>();
        Map<LocalDate, Boolean>[] cuboParos = StopSalesCalendar.construirCubo(ops, desde, desde.plusMonths(1).minusDays(1), comboHabitacion.getValue(), null, null);

        Label l;
        Label ltitulomes;
        mes.addComponent(ltitulomes = l = new Label("" + desde.getMonth().toString() + " " + desde.getYear()));
        l.addStyleName("titulo");

        int totalContratado = 0;
        int totalReservado = 0;

        HorizontalLayout fila;
        {
            mes.addComponent(fila = new HorizontalLayout());
            fila.addStyleName("fila");
            fila.addStyleName("calendario");

            fila.addComponent(l = new Label(""));
            //fila.addComponent(l = new Label("" + desde.getMonth().toString() + " " + desde.getYear()));
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
                totalContratado += cupo[0];
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
            fila.addStyleName("derivado");

            fila.addComponent(l = new Label("Moved"));
            l.addStyleName("titulo");
            l.setWidth("200px");


            LocalDate f = desde.plusDays(0);
            while (f.getMonth().equals(desde.getMonth()) || f.getDayOfMonth() != 1) {
                int[] cupo = cubo.getCubo(f, comboHabitacion.getValue());
                fila.addComponent(l = new Label("" + cupo[3]));
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
                fila.addComponent(l = new Label("" + cupo[1]));
                l.addStyleName("dia");

                if (cuboParos[0].getOrDefault(f, false)) l.addStyleName("cerrado");

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
                fila.addComponent(l = new Label("" + cupo[2]));
                totalReservado += cupo[2];
                l.addStyleName("dia");
                if (DayOfWeek.SUNDAY.equals(f.getDayOfWeek())) {
                    l.addStyleName("domingo");
                }
                f = f.plusDays(1);
            }
        }

        for (int posAgencia = 0; posAgencia < cubo.getAgencias().size(); posAgencia++) {

            fila = new HorizontalLayout();

            fila.addStyleName("fila");
            fila.addStyleName("reservadoagencia");

            fila.addComponent(l = new Label(cubo.getAgencias().get(posAgencia).getName()));
            l.addStyleName("titulo");
            l.setWidth("200px");




            LocalDate f = desde.plusDays(0);
            boolean hayReservas = false;
            while (f.getMonth().equals(desde.getMonth()) || f.getDayOfMonth() != 1) {
                int[] cupo = cubo.getCubo(f, comboHabitacion.getValue());
                fila.addComponent(l = new Label("" + cupo[4 + posAgencia]));
                hayReservas |= cupo[4 + posAgencia] != 0;
                l.addStyleName("dia");
                if (DayOfWeek.SUNDAY.equals(f.getDayOfWeek())) {
                    l.addStyleName("domingo");
                }
                f = f.plusDays(1);
            }

            if (hayReservas) mes.addComponent(fila);
        }


        if (totalContratado != 0) ltitulomes.setValue(ltitulomes.getValue() + ": occupation = " + Math.round(100d * ((0d + totalReservado) / (0d + totalContratado))) + "%");

        return mes;
    }

    private Component construirMesWeekly(LocalDate desde) throws Throwable {
        VerticalLayout vl = new VerticalLayout();
        vl.addStyleName("mes");
        vl.addStyleName("weekly");
        vl.setWidthUndefined();

        Label titulo;
        vl.addComponent(titulo = new Label());
        titulo.addStyleName("titulo");
        titulo.setValue("" + desde.getMonth().toString() + " " + desde.getYear());

        LocalDate f = desde.plusDays(0);

        List<StopSalesOperation> ops = (comboHotel.getValue() != null)?StopSalesCalendar.getOperations(comboHotel.getValue(), desde, desde.plusMonths(2)):new ArrayList<>();
        Map<LocalDate, Boolean>[] cuboParos = StopSalesCalendar.construirCubo(ops, desde, desde.plusMonths(1).minusDays(1), comboHabitacion.getValue(), null, null);


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
                disponible.setValue("" + cupo[1]);

                if (cuboParos[0].getOrDefault(f, false)) disponible.addStyleName("cerrado");

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
                vendido.setValue("" + cupo[2]);


                Label desviado;
                infoCupo.addComponent(desviado = new Label());
                desviado.addStyleName("desviado");
                desviado.setValue("" + cupo[3]);

                LocalDate finalF = f;
                dia.addLayoutClickListener(e -> {
                    try {
                        verDetalle(finalF);
                    } catch (Throwable throwable) {
                        MDD.alert(throwable);
                    }
                });

            } else {

                dia.addStyleName("enblanco");

            }



            f = f.plusDays(1);
        }

        return vl;
    }

    public void verDetalle(LocalDate f) throws Throwable {

        listaDetalle.removeAllComponents();

        Label lt;
        listaDetalle.addComponent(lt = new Label("Inventory operations for " + f + ":"));
        lt.addStyleName(ValoTheme.LABEL_H3);

        if (comboCupo.getValue() != null && comboHabitacion.getValue() != null) {

            Inventory inventory = comboCupo.getValue();
            RoomType room = comboHabitacion.getValue();

            for (HotelContract c : inventory.getContracts()) {
                if (c.getTerms() != null) for (Allotment a : c.getTerms().getAllotment()) if (a.getRoom() != null && a.getRoom().equals(room) && (a.getStart() == null || !a.getStart().isAfter(f)) && (a.getEnd() == null || !a.getEnd().isBefore(f))) listaDetalle.addComponent(new Label("" + c + " " + (a.getQuantity() >= 0?"+":"") + a.getQuantity()));
            }


            for (Inventory dependant : inventory.getDependantInventories()) {
                for (HotelContract c : dependant.getContracts()) if (!inventory.getContracts().contains(c)) {
                    if (c.getTerms() != null) for (Allotment a : c.getTerms().getAllotment()) if (a.getRoom() != null && a.getRoom().equals(room) && (a.getStart() == null || !a.getStart().isAfter(f)) && (a.getEnd() == null || !a.getEnd().isBefore(f))) listaDetalle.addComponent(new Label("" + c + " " + (a.getQuantity() > 0?"":"+") + (-1 * a.getQuantity())));
                }
            }

            for (InventoryOperation a : inventory.getOperations()) {
                int q = a.getQuantity();
                if (InventoryAction.SUBSTRACT.equals(a.getAction())) q *= -1;
                if (a.getRoom() != null && a.getRoom().equals(room) && (a.getStart() == null || !a.getStart().isAfter(f)) && (a.getEnd() == null || !a.getEnd().isBefore(f))) listaDetalle.addComponent(new Label("" + a + " " + (InventoryAction.SET.equals(a.getAction())?"=":(q >= 0?"+":"")) + q));
            }

            for (HotelBookingLine l : inventory.getBookings()) {
                if (l.getBooking().isActive() && l.isActive()) {
                    HotelBookingLine a = l;
                    int q = -1 * a.getRooms();
                    if (a.getRoom() != null && a.getRoom().getType().equals(room) && (a.getStart() == null || !a.getStart().isAfter(f)) && (a.getEnd() == null || !a.getEnd().isBefore(f))) listaDetalle.addComponent(new Label("" + a.getBooking() + " " + (q >= 0?"+":"") + q));
                }
            }

        }

        if (listaDetalle.getComponentCount() <= 1) listaDetalle.addComponent(new Label("No inventory operation for " + f));

    }



    @Override
    public String toString() {
        return "Inventory calendar";
    }
}
