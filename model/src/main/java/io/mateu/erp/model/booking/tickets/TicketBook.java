package io.mateu.erp.model.booking.tickets;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import io.mateu.erp.model.product.AbstractProduct;
import io.mateu.erp.model.product.Variant;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.NotWhenEditing;
import io.mateu.mdd.core.annotations.Output;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Set;

@Entity
@Getter@Setter
public class TicketBook {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String reference;

    public boolean isProductEnabled() {
        return !ticketsGenerated;
    }

    private String serie;

    public boolean isSerieEnabled() {
        return !ticketsGenerated;
    }

    private int fromNumber;

    public boolean isFromNumberEnabled() {
        return !ticketsGenerated;
    }

    private int toNumber;

    public boolean isToNumberEnabled() {
        return !ticketsGenerated;
    }

    @Output
    private boolean ticketsGenerated;


    @Override
    public String toString() {
        return "" + reference + " " + serie + " " + fromNumber + " to " + toNumber;
    }

    @Action
    public static void generateTickets(EntityManager em, Set<TicketBook> selected) {

        for (TicketBook book : selected) if (!book.isTicketsGenerated()) {

            for (int i = book.getFromNumber(); i < book.getToNumber(); i++) {
                Ticket t = new Ticket();
                t.setBook(book);
                String serie = book.getSerie();
                if (serie == null) serie = "";
                t.setId(serie + i);
                em.persist(t);
            }

            book.setTicketsGenerated(true);

        }

    }
}
