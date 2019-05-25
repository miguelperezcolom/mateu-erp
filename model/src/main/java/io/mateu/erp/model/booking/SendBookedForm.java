package io.mateu.erp.model.booking;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.vaadin.icons.VaadinIcons;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.workflow.SendEmailTask;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Getter@Setter
public class SendBookedForm {

    @Ignored
    private java.io.File temp;

    @Ignored
    private final Booking booking;

    @NotEmpty
    private String email;

    @TextArea
    private String postscript;

    @IFrame
    @FullWidth
    private URL preview;




    public SendBookedForm(Booking booking) throws Throwable {
        this.booking = booking;
        if (Strings.isNullOrEmpty(email)) {
            if (booking.getAgency().getFinancialAgent() != null && booking.getAgency().getFinancialAgent().isDirectSale()) {
                email = booking.getEmail();
            } else {
                email = booking.getAgency().getEmail();
            }
        }

        String archivo = UUID.randomUUID().toString();
        java.io.File temp = (System.getProperty("tmpdir") == null)? java.io.File.createTempFile(archivo, ".html"):new java.io.File(new java.io.File(System.getProperty("tmpdir")), archivo + ".html");

        Helper.notransact(em -> {
            String freemark = AppConfig.get(em).getBookedEmailTemplate();

            String msg = "*********";
            if (!Strings.isNullOrEmpty(freemark)) {
                Map<String, Object> data = booking.getData(em);
                data.put("postscript", postscript);
                msg = Helper.freemark(freemark, data);
            }

            Files.write(msg.getBytes(), temp);

        });

        String baseUrl = System.getProperty("tmpurl");
        if (baseUrl == null) {
            baseUrl = temp.toURI().toURL().toString();
        }
        preview = new URL(baseUrl + "/" + temp.getName());
    }

    @Action(icon = VaadinIcons.ENVELOPE, order = 1)
    public void send() throws Throwable {
        Helper.transact(em ->{

            em.find(Booking.class, booking.getId()).sendBooked(em, email, postscript);

        });
    }
}
