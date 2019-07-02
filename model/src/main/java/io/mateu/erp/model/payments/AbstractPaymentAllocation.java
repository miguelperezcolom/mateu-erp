package io.mateu.erp.model.payments;

import com.vaadin.ui.Grid;
import com.vaadin.ui.StyleGenerator;
import io.mateu.erp.model.booking.Passenger;
import io.mateu.erp.model.booking.Service;
import io.mateu.mdd.core.interfaces.GridDecorator;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "paymentallocation")
@Getter
@Setter
public abstract class AbstractPaymentAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne@NotNull
    private Payment payment;

    public void setPayment(Payment payment) {
        this.payment = payment;
        pushUp();
    }

    private String description;

    private double value;

    public void setValue(double value) {
        this.value = value;
        pushUp();
    }

    public void pushUp() {
        if (payment != null) {
            payment.updateBalance();
        }
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj != null && obj instanceof Payment && id == ((Payment) obj).getId());
    }

    @Override
    public String toString() {
        return "" + payment.getDate() + " " + value;
    }


    public static GridDecorator getGridDecorator() {
        return new GridDecorator() {
            @Override
            public void decorateGrid(Grid grid) {
                grid.getColumns().forEach(col -> {

                    StyleGenerator old = ((Grid.Column) col).getStyleGenerator();

                    ((Grid.Column)col).setStyleGenerator(new StyleGenerator() {
                        @Override
                        public String apply(Object o) {
                            String s = null;
                            if (old != null) s = old.apply(o);

                            if (o instanceof BookingPaymentAllocation) {
                                if (((BookingPaymentAllocation)o).getInvoice() != null) s = (s != null)?s + " cancelled":"cancelled";
                            }
                            return s;
                        }
                    });
                });
            }
        };
    }

}
