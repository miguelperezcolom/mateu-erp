package io.mateu.erp.model.invoicing;

import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.Ignored;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class SelfBillForm {
    @Ignored
    private final Set<IssuedInvoice> selection;

    public SelfBillForm(Set<IssuedInvoice> selection) {
        this.selection = selection;
        for (IssuedInvoice i : selection) {
            for (AbstractInvoiceLine l : i.getLines()) if (l instanceof BookingInvoiceLine) {
                BookingInvoiceLine bil = (BookingInvoiceLine) l;
                AmountPerBooking a;
                amounts.add(a = new AmountPerBooking());
                a.setBookingId(((BookingInvoiceLine) l).getCharge().getBooking().getId());
                a.setAmount(l.getTotal());
            }
        }
    }

    private String postscript;

    private List<AmountPerBooking> amounts = new ArrayList<>();

    @Getter@Setter
    public class AmountPerBooking {
        private long bookingId;
        private double amount;
    }

    public void createSelfBill() {
        MDD.alert("Self bill created");
    }
}