package io.mateu.erp.model.payments;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.File;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@Getter@Setter
public class FilePaymentAllocation extends AbstractPaymentAllocation {

    @ManyToOne@NotNull
    private File file;

    public void setFile(File file) {
        this.file = file;
        if (file != null) file.setForcePre(true);
    }
}
