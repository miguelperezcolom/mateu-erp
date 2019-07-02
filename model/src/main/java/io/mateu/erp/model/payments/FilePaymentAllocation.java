package io.mateu.erp.model.payments;

import io.mateu.erp.model.booking.File;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter@Setter
public class FilePaymentAllocation extends AbstractPaymentAllocation {

    @ManyToOne@NotNull
    private File file;

    public void setFile(File file) {
        this.file = file;
        if (file != null) file.setUpdateRqTime(LocalDateTime.now());
    }

    @PrePersist
    @PreUpdate
    public void pre() {
        setDescription(file != null?"File " + file.getId():"---");
    }

}
