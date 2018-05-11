package io.mateu.erp.model.product.hotel;

import io.mateu.erp.dispo.interfaces.product.IRoom;
import io.mateu.common.model.multilanguage.Literal;
import io.mateu.erp.model.product.hotel.RoomType;
import io.mateu.ui.mdd.server.annotations.ListColumn;
import io.mateu.ui.mdd.server.annotations.SearchFilter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Getter
@Setter
public class Room implements IRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @SearchFilter
    @ManyToOne
    @ListColumn
    private Hotel hotel;

    @SearchFilter
    @ManyToOne
    @ListColumn
    private RoomType type;

    @ManyToOne(cascade = CascadeType.ALL)
    private Literal description;

    @Convert(converter = MaxCapacitiesConverter.class)
    private MaxCapacities maxCapacities = new MaxCapacities();

    private int minPax;

    private int minAdultsForChildDiscount;

    private boolean infantsAllowed;

    private boolean childrenAllowed;

    private boolean infantsInBed;

    @ManyToOne
    @ListColumn(field = "code", value = "Inv. owner")
    private RoomType inventoryPropietary;

    @Override
    public boolean fits(int adults, int children, int babies) {
        boolean ok = (adults + children + babies) >= getMinPax();

        ok = ok && isInfantsAllowed() || babies == 0;
        ok = ok && isChildrenAllowed() || (children == 0 && babies == 0);

        if (!isInfantsInBed()) babies = 0;

        if (ok && getMaxCapacities() != null && getMaxCapacities().getCapacities().size() > 0) {
            ok = false;
            for (MaxCapacity c : getMaxCapacities().getCapacities()) {
                int ra = c.getAdults() - adults;
                int rc = c.getChildren() - children;
                int ri = c.getInfants() - babies;
                ok = ra >= 0 && rc >= 0 && ri >= 0;
                if (!ok) {
                    if (ri < 0 && rc > 0) rc -= ri;
                    if (rc > 0 && ra > 0) ra -= rc;
                    ok = ra >= 0;
                }
                if (ok) {
                    break;
                }
            }
        }

        return ok;
    }

    @Override
    public String getCode() {
        return getType().getCode();
    }

    @Override
    public String getName() {
        return getType().getName().getEs();
    }

    @Override
    public String getInventoryPropietaryRoomCode() {
        if (getInventoryPropietary() != null) return getInventoryPropietary().getCode();
        else return null;
    }
}
