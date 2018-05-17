package io.mateu.erp.client.booking;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.mateu.erp.client.booking.BookingServiceAsync;
import io.mateu.erp.model.booking.transfer.TransferDirection;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.product.transfer.TransferType;
import io.mateu.erp.shared.booking.BookingService;
import io.mateu.ui.core.client.app.Callback;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.DateField;
import io.mateu.ui.core.client.components.fields.TextAreaField;
import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.LinkColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.TextColumn;
import io.mateu.ui.core.client.views.AbstractDialog;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.Pair;
import io.mateu.ui.mdd.client.AbstractJPAListView;
import io.mateu.ui.mdd.client.JPAAutocompleteField;
import io.mateu.ui.mdd.client.MDDOpenEditorAction;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PickupConfirmationView extends AbstractJPAListView {
    @Override
    public String getSql() {
        String jpql = "select x.id, x.booking.agencyReference, x.booking.leadName, x.transferType, x.pax, 'Choose', x.flightTime, x.flightNumber, x.flightOriginOrDestination, x.pickupTime, epu.name, x.providers, x.sentToProvider, x.pickupConfirmedByWeb, x.pickupConfirmedByEmailToHotel, x.pickupConfirmedBySMS, x.pickupConfirmedByTelephone from TransferService x left join x.effectivePickup epu where 1 = 1 ";

        jpql = "select x.id, x.booking.agencyReference, x.booking.leadName, x.transferType, x.pax, 'Choose', x.pickupTime, " +
                "case when " +
                        " ap.id != null and x.transferType != " + TransferType.class.getTypeName() + ".EXECUTIVE and (" +
                        " x.transferType = " + TransferType.class.getTypeName() + ".SHUTTLE " +
                        " or epu.alternatePointForNonExecutive = true " +
                        ") then ap.name else epu.name end" +
                ", x.providers, x.sentToProvider " +
                " from TransferService x left join x.effectivePickup epu " +
                "   left join epu.alternatePointForShuttle ap " +
                " where 1 = 1 and x.direction = " + TransferDirection.class.getTypeName() + ".OUTBOUND " +
                " and x.start >= {d '" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'} ";

        jpql = "select x.id, x.booking.agencyReference, x.booking.leadName, x.transferType, x.pax, 'Choose', x.pickupTime, ap.name, " +
                "case when " +
                " ap.id != null and x.transferType != " + TransferType.class.getTypeName() + ".EXECUTIVE and (" +
                " x.transferType = " + TransferType.class.getTypeName() + ".SHUTTLE " +
                " or epu.alternatePointForNonExecutive = true " +
                ") then ap.name else '---' end" +
                " " +
                " from TransferService x left join x.effectivePickup epu " +
                "   left join epu.alternatePointForShuttle ap " +
                " where 1 = 1 and x.direction = " + TransferDirection.class.getTypeName() + ".OUTBOUND " +
                " and x.start >= {d '" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'} ";

        if (getData().get("fecha") != null) jpql += " and x.start = {d '" + getData().getLocalDate("fecha").format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'} ";
        if (getData().get("hotel") != null) jpql += " and x.effectivePickup.id = " + ((Pair)getData().get("hotel")).getValue() + " ";
        if (!Strings.isNullOrEmpty(getData().getString("nombre"))) jpql += " and lower(x.booking.leadName) like '%" + getData().getString("nombre").toLowerCase().replaceAll("'", "''")+ "%' ";
        if (!Strings.isNullOrEmpty(getData().getString("ref"))) jpql += " and lower(x.booking.agencyReference) like '%" + getData().getString("ref").toLowerCase().replaceAll("'", "''")+ "%' ";
        if (!Strings.isNullOrEmpty(getData().getString("vuelo"))) jpql += " and lower(x.flightNumber) like '%" + getData().getString("vuelo").toLowerCase().replaceAll("'", "''")+ "%' ";
        jpql += " order by x.start";
        return jpql;
    }

    @Override
    public List<AbstractColumn> createColumns() {
        int col = 1;
        return Lists.newArrayList(
                new LinkColumn("col" + col++, "Ref.", 100) {
                    @Override
                    public void run(Data in) {
                        new MDDOpenEditorAction("", TransferService.class, in.get("_id")).run();
                    }
                }
                , new TextColumn("col" + col++, "Lead name", 170, false)
                , new TextColumn("col" + col++, "Service", 80, false)
                , new TextColumn("col" + col++, "Pax", 40, false)

                , new LinkColumn("col" + col++, "Informed", 100) {
                    @Override
                    public void run(Data in) {
                        MateuUI.openView(new AbstractDialog() {
                            @Override
                            public void onOk(Data data) {
                                ((BookingServiceAsync) MateuUI.create(BookingService.class)).pickupTimeInformed(MateuUI.getApp().getUserData().getLogin(), in.get("_id"), getData().getString("obs"), new Callback<Void>() {
                                    @Override
                                    public void onSuccess(Void result) {
                                        search();
                                    }
                                });
                            }

                            @Override
                            public String getTitle() {
                                return "PU confirmed";
                            }

                            @Override
                            public void build() {
                                add(new TextAreaField("obs", "Comments"));
                            }
                        });
                    }
                }
                /*
                , new TextColumn("col" + col++, "F. date", 150, false)
                , new TextColumn("col" + col++, "F. number", 150, false)
                , new TextColumn("col" + col++, "Orig/Dest", 150, false)
                */
                , new TextColumn("col" + col++, "PU time", 150, false)
                , new TextColumn("col" + col++, "PU point", 200, false)
                , new TextColumn("col" + col++, "Alternate PU point", 200, false)
                //, new TextColumn("col" + col++, "Provider", 150, false)
                //, new TextColumn("col" + col++, "Sent to prov", 150, false)
                /*
                , new TextColumn("col" + col++, "Web", 150, false)
                , new TextColumn("col" + col++, "Email", 150, false)
                , new TextColumn("col" + col++, "SMS", 150, false)
                , new TextColumn("col" + col++, "Telephone", 150, false)
                */
        );
    }

    @Override
    public String getTitle() {
        return "Pickup confirmation";
    }

    @Override
    public void build() {
        add(new DateField("fecha", "Date"));
        add(new JPAAutocompleteField("hotel", "Pickup point", "select x.id, x.name from TransferPoint x order by x.name"));
        add(new TextField("ref", "Reference"));
        add(new TextField("vuelo", "Filght number"));
        add(new TextField("nombre", "Lead name"));
    }
}
