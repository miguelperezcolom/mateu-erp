package io.mateu.erp.client.booking;

import com.google.common.base.Strings;
import io.mateu.erp.model.booking.transfer.TransferDirection;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.erp.model.product.transfer.TransferType;
import io.mateu.erp.server.booking.BookingServiceImpl;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.MainSearchFilter;
import io.mateu.mdd.core.interfaces.RpcCrudView;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter@Setter
public class PickupConfirmationView implements RpcCrudView<PickupConfirmationView, PickupConfirmationRow, TransferService> {

    @MainSearchFilter
    private LocalDate date;
    @MainSearchFilter
    private TransferPoint hotel;
    @MainSearchFilter
    private String reference;
    @MainSearchFilter
    private String flightNumber;
    @MainSearchFilter
    private String leadName;


    @Ignored
    private transient int count;


    @Override
    public List<PickupConfirmationRow> rpc(PickupConfirmationView pickupConfirmationView, int offset, int limit) throws Throwable {
        List<PickupConfirmationRow> l = new ArrayList<>();
        Helper.transact(em -> {
            l.addAll(em.createQuery(getSql()).getResultList());
        });
        count = l.size();
        return l;
    }

    @Override
    public int gatherCount(PickupConfirmationView filters) throws Throwable {
        return count;
    }

    @Override
    public Object deserializeId(String s) {
        return null;
    }

    @Override
    public boolean isAddEnabled() {
        return false;
    }

    @Action
    public static void confirm(Set<PickupConfirmationRow> selection, String comments) {
        selection.forEach( r -> {
            try {
                new BookingServiceImpl().pickupTimeInformed(MDD.getUserData().getLogin(), r.getId(), comments);
            } catch (Throwable throwable) {
                MDD.alert(throwable);
            }
        });
    }

    public String getSql() {
        String jpql = "select new " + PickupConfirmationRow.class.getName() + "( x.id, x.file.agencyReference, x.file.leadName, x.transferType, x.pax, x.pickupTime, epu.name, " +
                "case when " +
                " ap.id != null and x.transferType != " + TransferType.class.getTypeName() + ".EXECUTIVE and (" +
                " x.transferType = " + TransferType.class.getTypeName() + ".SHUTTLE " +
                " or epu.alternatePointForNonExecutive = true " +
                ") then ap.name else '---' end" +
                ") " +
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

}
