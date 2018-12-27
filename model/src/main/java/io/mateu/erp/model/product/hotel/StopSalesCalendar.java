package io.mateu.erp.model.product.hotel;

import com.google.common.collect.Lists;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import io.mateu.erp.model.booking.parts.HotelBookingLine;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.mdd.core.CSS;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.util.Helper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StopSalesCalendar extends VerticalLayout {

    private final DateField del;
    private final DateField al;
    private final Panel panelScrollable;
    private final VerticalLayout contenidoPanel;
    private ComboBox<Partner> comboAgencia;
    private final ComboBox<BoardType> comboRegimen;
    private final ComboBox<RoomType> comboHabitacion;
    private final ComboBox<String> modo;
    private final Panel capaDetalle;
    private final VerticalLayout listaDetalle;
    private Button botonRefrescar;
    private final CssLayout capaCalendario;
    private final StopSalesCalendarNavigator nav;
    private ComboBox<Hotel> comboHotel;

    public StopSalesCalendar() {
        this(null);
    }

    public StopSalesCalendar(Hotel hotel) {

        setSizeFull();
        addStyleName(CSS.NOPADDING);

        addStyleName("calendariocupoocierres");

        HorizontalLayout hl;
        addComponent(hl = new HorizontalLayout());

        try {
            hl.addComponent(comboHotel = new ComboBox<Hotel>("Hotel", Helper.selectObjects("select x from " + Hotel.class.getName() + " x order by x.name")));
            comboHotel.setWidth("400px");
        } catch (Throwable throwable) {
            MDD.alert(throwable);
        }

        try {
            hl.addComponent(comboAgencia = new ComboBox<Partner>("Agency", Helper.selectObjects("select x from " + Partner.class.getName() + " x where x.agency = true order by x.name")));
            comboAgencia.setWidth("400px");
        } catch (Throwable throwable) {
            MDD.alert(throwable);
        }

        addComponent(hl = new HorizontalLayout());

        hl.addComponent(comboHabitacion = new ComboBox<RoomType>("Room", (hotel != null)?hotel.getRooms().stream().map(r -> r.getType()).collect(Collectors.toList()):new ArrayList<>()));
        comboHabitacion.setWidth("400px");

        hl.addComponent(comboRegimen = new ComboBox<BoardType>("Board", (hotel != null)?hotel.getBoards().stream().map(r -> r.getType()).collect(Collectors.toList()):new ArrayList<>()));
        comboRegimen.setWidth("400px");

        if (hotel != null) {
            comboHotel.setValue(hotel);
        }

        addComponent(hl = new HorizontalLayout());

        hl.addComponent(nav = new StopSalesCalendarNavigator(this));
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


        Button botonAbrir;
        hl.addComponent(botonAbrir = new Button("open", VaadinIcons.CHECK_CIRCLE_O));
        botonAbrir.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        botonAbrir.addClickListener(e -> abrir());

        Button botonCerrar;
        hl.addComponent(botonCerrar = new Button("Close", VaadinIcons.CLOSE_CIRCLE_O));
        botonCerrar.addStyleName(ValoTheme.BUTTON_DANGER);
        botonCerrar.addClickListener(e -> cerrar());


        addComponent(panelScrollable = new Panel(contenidoPanel = new VerticalLayout()));
        panelScrollable.addStyleName("panelcalendario");
        panelScrollable.addStyleName(ValoTheme.PANEL_BORDERLESS);
        panelScrollable.addStyleName(CSS.NOPADDING);
        contenidoPanel.addStyleName(CSS.NOPADDING);

        contenidoPanel.addComponent(capaCalendario = new CssLayout());
        capaCalendario.addStyleName("calendariocupo");

        comboHotel.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                comboRegimen.setDataProvider(new ListDataProvider<>(e.getValue().getBoards().stream().map(r -> r.getType()).collect(Collectors.toList())));
                comboRegimen.setValue(null);

                comboHabitacion.setDataProvider(new ListDataProvider<>(e.getValue().getRooms().stream().map(r -> r.getType()).collect(Collectors.toList())));
                comboHabitacion.setValue(null);

                refrescar();
            }
        });

        comboAgencia.addValueChangeListener(e -> {
            refrescar();
        });

        comboHabitacion.addValueChangeListener(e -> {
            refrescar();
        });

        comboRegimen.addValueChangeListener(e -> {
            refrescar();
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

    private void cerrar() {
        Hotel h;
        if ((h = comboHotel.getValue()) != null && del.getValue() != null && al.getValue() != null) {
            try {

                Helper.transact(em -> {
                    StopSalesOperation o = new StopSalesOperation();
                    o.setStopSales(h.getStopSales());
                    o.setAction(StopSalesAction.CLOSE);
                    o.setStart(del.getValue());
                    o.setEnd(al.getValue());
                    if (comboHabitacion.getValue() != null) o.getRooms().add(comboHabitacion.getValue());
                    if (comboRegimen.getValue() != null) o.getBoards().add(comboRegimen.getValue());
                    if (comboAgencia.getValue() != null) o.getAgencies().add(comboAgencia.getValue());
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

    private void abrir() {
        Hotel h;
        if ((h = comboHotel.getValue()) != null && del.getValue() != null && al.getValue() != null) {
            try {
                Helper.transact(em -> {
                    StopSalesOperation o = new StopSalesOperation();
                    o.setStopSales(h.getStopSales());
                    o.setAction(StopSalesAction.OPEN);
                    o.setStart(del.getValue());
                    o.setEnd(al.getValue());
                    if (comboHabitacion.getValue() != null) o.getRooms().add(comboHabitacion.getValue());
                    if (comboRegimen.getValue() != null) o.getBoards().add(comboRegimen.getValue());
                    if (comboAgencia.getValue() != null) o.getAgencies().add(comboAgencia.getValue());
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

        if (comboHotel.getValue() != null) {


            try {

                Hotel hotel = comboHotel.getValue();

                LocalDate desde = nav.getFecha();

                List<StopSalesOperation> ops = getOperations(hotel, desde, desde.plusMonths(2));

                capaCalendario.addComponent(construirMes(ops, desde));
                capaCalendario.addComponent(construirMes(ops, desde.plusMonths(1)));

            } catch (Throwable throwable) {
                throwable.printStackTrace();

                MDD.alert(throwable);

            }



        } else {
            capaCalendario.addComponent(new Label("You must select a hotel + inventory + room."));
        }

    }

    public static List<StopSalesOperation> getOperations(Hotel hotel, LocalDate desde, LocalDate hasta) throws Throwable {
        return Helper.selectObjects("select x from " + StopSalesOperation.class.getName() + " x where x.stopSales.id = " + hotel.getStopSales().getId() + " and x.start <= :hasta and x.end >= :desde order by x.id", Helper.hashmap("desde", desde, "hasta", hasta));
    }

    private Component construirMes(List<StopSalesOperation> ops, LocalDate desde) {
        if ("weekly".equalsIgnoreCase(modo.getValue())) return construirMesWeekly(ops, desde);
        else return construirMesMonthly(ops, desde);
    }

    private Component construirMesMonthly(List<StopSalesOperation> ops, LocalDate desde) {
        VerticalLayout mes = new VerticalLayout();
        mes.addStyleName("mes");
        mes.addStyleName("monthly");
        mes.setWidthUndefined();


        Map<LocalDate, Boolean>[] cubo = construirCubo(ops, desde, desde.plusMonths(1).minusDays(1), comboHabitacion.getValue(), comboRegimen.getValue(), comboAgencia.getValue());


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
            fila.addStyleName("disponible");

            fila.addComponent(l = new Label("Available"));
            l.addStyleName("titulo");
            l.setWidth("200px");


            LocalDate f = desde.plusDays(0);
            while (f.getMonth().equals(desde.getMonth()) || f.getDayOfMonth() != 1) {
                fila.addComponent(l = new Label(cubo[0].getOrDefault(f, false)?"C":"O"));
                l.addStyleName("dia");
                if (DayOfWeek.SUNDAY.equals(f.getDayOfWeek())) {
                    l.addStyleName("domingo");
                }
                f = f.plusDays(1);
            }
        }

        return mes;
    }

    public static Map<LocalDate,Boolean>[] construirCubo(List<StopSalesOperation> ops, LocalDate desde, LocalDate hasta, RoomType room, BoardType board, Partner agency) {
        Map<LocalDate,Boolean> m = new HashMap<>();
        Map<LocalDate,Boolean> p = new HashMap<>();

        for (StopSalesOperation o : ops) {
            boolean applied = false;
            if ((room == null && o.getRooms().size() == 0) || o.getRooms().size() == 0 || o.getRooms().contains(room)) {
                if ((board == null && o.getBoards().size() == 0) || o.getBoards().size() == 0 || o.getBoards().contains(board)) {
                    if ((agency == null && o.getAgencies().size() == 0) || o.getAgencies().size() == 0 || o.getAgencies().contains(agency)) {

                        LocalDate d0 = o.getStart();
                        if (d0 == null) d0 = desde;
                        if (d0.isBefore(desde)) d0 = desde;
                        LocalDate d1 = o.getEnd();
                        if (d1 == null) d1 = hasta;
                        if (d1.isAfter(hasta)) d1 = hasta;

                        applied = true;

                        for (LocalDate d = d0.plusDays(0); !d.isAfter(d1); d = d.plusDays(1)) {
                            m.put(d, StopSalesAction.CLOSE.equals(o.getAction()));
                        }

                    }
                }
            }
            if (!applied) {
                LocalDate d0 = o.getStart();
                if (d0 == null) d0 = desde;
                if (d0.isBefore(desde)) d0 = desde;
                LocalDate d1 = o.getEnd();
                if (d1 == null) d1 = hasta;
                if (d1.isAfter(hasta)) d1 = hasta;

                for (LocalDate d = d0.plusDays(0); !d.isAfter(d1); d = d.plusDays(1)) {
                    p.put(d, StopSalesAction.CLOSE.equals(o.getAction()));
                }
            }
        }

        return new Map[] {m, p};
    }

    private Component construirMesWeekly(List<StopSalesOperation> ops, LocalDate desde) {
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

        Map<LocalDate, Boolean>[] cubo = construirCubo(ops, desde, desde.plusMonths(1).minusDays(1), comboHabitacion.getValue(), comboRegimen.getValue(), comboAgencia.getValue());

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

                HorizontalLayout infoCupo;
                dia.addComponent(infoCupo = new HorizontalLayout());
                infoCupo.addStyleName("infoCupo");

                Label disponible;
                infoCupo.addComponent(disponible = new Label());
                disponible.addStyleName("cierre");
                disponible.setValue(cubo[0].getOrDefault(f, false)?"C":"O");

                if (cubo[0].getOrDefault(f, false)) disponible.addStyleName("cerrado");
                else if (cubo[1].getOrDefault(f, false)) {
                    disponible.addStyleName("cierreparcial");
                    //disponible.setValue("P");
                }


                LocalDate finalF = f;
                dia.addLayoutClickListener(e -> {
                    try {
                        verDetalle(ops, finalF, comboHabitacion.getValue(), comboRegimen.getValue(), comboAgencia.getValue());
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

    public void verDetalle(List<StopSalesOperation> ops, LocalDate f, RoomType room, BoardType board, Partner agency) throws Throwable {

        listaDetalle.removeAllComponents();

        Label l;
        listaDetalle.addComponent(l = new Label("Stop sales operations for " + f + ":"));
        l.addStyleName(ValoTheme.LABEL_H3);

        boolean hay = false;

        for (StopSalesOperation o : ops) {
            boolean aplicado = false;
            if ((room == null && o.getRooms().size() == 0) || o.getRooms().size() == 0 || o.getRooms().contains(room)) {
                if ((board == null && o.getBoards().size() == 0) || o.getBoards().size() == 0 || o.getBoards().contains(board)) {
                    if ((agency == null && o.getAgencies().size() == 0) || o.getAgencies().size() == 0 || o.getAgencies().contains(agency)) {
                        if (o.getStart() == null || !o.getStart().isAfter(f)) {
                            if (o.getEnd() == null || !o.getEnd().isBefore(f)) {
                                aplicado = true;
                                listaDetalle.addComponent(l = new Label("" + o));
                                l.addStyleName(ValoTheme.LABEL_BOLD);
                                hay = true;
                            }
                        }
                    }
                }
            }
            if (!aplicado) {
                if (o.getStart() == null || !o.getStart().isAfter(f)) {
                    if (o.getEnd() == null || !o.getEnd().isBefore(f)) {
                        listaDetalle.addComponent(l = new Label("" + o));
                        hay = true;
                    }
                }
            }
        }

        if (!hay) listaDetalle.addComponent(new Label("No operation found"));


    }

    private List<InventoryOperation> getOperations(Inventory inventory) throws Throwable {
        return Helper.selectObjects("select x from " + InventoryOperation.class.getName() + " x where x.inventory.id = " + inventory.getId() + " order by x.id");
    }


    @Override
    public String toString() {
        return "Stop sales calendar";
    }
}
