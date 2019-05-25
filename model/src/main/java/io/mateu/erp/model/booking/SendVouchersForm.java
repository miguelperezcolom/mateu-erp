package io.mateu.erp.model.booking;

import com.google.common.base.Strings;
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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Getter@Setter
public class SendVouchersForm {

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
    private URL vouchers;




    public SendVouchersForm(Booking booking) throws Throwable {
        this.booking = booking;
        if (Strings.isNullOrEmpty(email)) {
            if (booking.getAgency().getFinancialAgent() != null && booking.getAgency().getFinancialAgent().isDirectSale()) {
                email = booking.getEmail();
            } else {
                email = booking.getAgency().getEmail();
            }
        }

        String archivo = UUID.randomUUID().toString();
        java.io.File temp = (System.getProperty("tmpdir") == null)? java.io.File.createTempFile(archivo, ".pdf"):new java.io.File(new java.io.File(System.getProperty("tmpdir")), archivo + ".pdf");

        Helper.notransact(em -> {
            booking.writeVouchers(em, temp);
        });

        String baseUrl = System.getProperty("tmpurl");
        if (baseUrl == null) {
            vouchers = temp.toURI().toURL();
        }
        vouchers = new URL(baseUrl + "/" + temp.getName());
    }

    @Action(icon = VaadinIcons.ENVELOPE, order = 1)
    public void send() throws Throwable {
        Helper.transact(em ->{

            long t0 = new Date().getTime();

            AppConfig appconfig = AppConfig.get(em);

            Booking b = em.find(Booking.class, booking.getId());

            if (Strings.isNullOrEmpty(email)) throw new Exception("No valid email address. Please fill.");


            SendEmailTask t;
            b.getTasks().add(t = new SendEmailTask());

            if (b.getPos() != null && b.getPos().getOffice() != null) {
                t.setOffice(b.getPos().getOffice());
            }
            t.setSubject("Vouchers for booking " + b.getId() + "");
            t.setTo(email);
            t.setAudit(new Audit(MDD.getCurrentUser()));
            t.setDescription("Send vouchers email");
            t.getBookings().add(b);

            String msg = postscript;

            String freemark = appconfig.getVouchersEmailTemplate();

            if (!Strings.isNullOrEmpty(freemark)) {
                Map<String, Object> data = b.getData(em);
                msg = Helper.freemark(freemark, data);
            }

            t.setMessage(msg);


            b.adjuntarVouchers(em, t);

            // fin crear vouchers

        });
    }
}
