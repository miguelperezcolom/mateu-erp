package io.mateu.erp.client.booking;

import com.google.common.base.Strings;
import io.mateu.erp.model.booking.transfer.TransferDirection;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.erp.model.product.transfer.TransferType;
import io.mateu.erp.server.booking.BookingServiceImpl;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.interfaces.RpcCrudView;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Getter@Setter
public class PickupConfirmationView implements RpcCrudView<PickupConfirmationView, PickupConfirmationView.Row, TransferService> {

    private LocalDate date;
    private TransferPoint hotel;
    private String reference;
    private String flightNumber;
    private String leadName;


    @Override
    public List<Row> rpc(PickupConfirmationView pickupConfirmationView, int offset, int limit) throws Throwable {
        return null;
    }

    @Override
    public int gatherCount(PickupConfirmationView filters) throws Throwable {
        return 0;
    }

    @Override
    public Object deserializeId(String s) {
        return null;
    }

    @Override
    public boolean isAddEnabled() {
        return false;
    }

    @Getter@Setter
    public class Row {

        private long id;

    }

    public static void confirm(Set<Row> selection, String comments) {
        selection.forEach( r -> {
            try {
                new BookingServiceImpl().pickupTimeInformed(MDD.getUserData().getLogin(), r.getId(), comments);
            } catch (Throwable throwable) {
                MDD.alert(throwable);
            }
        });
    }

    public String getSql() {
        String jpql = "select x.id, x.file.agencyReference, x.file.leadName, x.transferType, x.pax, 'Choose', x.pickupTime, " +
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

        jpql = "select x.id, x.file.agencyReference, x.file.leadName, x.transferType, x.pax, 'Choose', x.pickupTime, epu.name, " +
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

        if (date != null) jpql += " and x.start = {d '" + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'} ";
        if (hotel != null) jpql += " and x.effectivePickup.id = " + hotel.getId() + " ";
        if (!Strings.isNullOrEmpty(leadName)) jpql += " and lower(x.file.leadName) like '%" + leadName.toLowerCase().replaceAll("'", "''")+ "%' ";
        if (!Strings.isNullOrEmpty(reference)) jpql += " and lower(x.file.agencyReference) like '%" + reference.toLowerCase().replaceAll("'", "''")+ "%' ";
        if (!Strings.isNullOrEmpty(flightNumber)) jpql += " and lower(x.flightNumber) like '%" + flightNumber.toLowerCase().replaceAll("'", "''")+ "%' ";
        jpql += " order by x.start";
        return jpql;
    }

    /*

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
                , new TextColumn("col" + col++, "PU time", 150, false)
                , new TextColumn("col" + col++, "PU point", 200, false)
                , new TextColumn("col" + col++, "Alternate PU point", 200, false)
        );
    }
    */

}
