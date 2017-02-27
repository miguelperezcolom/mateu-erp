package io.mateu.erp.model.booking;

import io.mateu.erp.model.authentication.Audit;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by miguel on 25/2/17.
 */
@Entity
@Getter
@Setter
public class ProviderRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Embedded
    private Audit audit;

    private boolean sent;
    private boolean confirmed;
    private boolean rejected;



}
