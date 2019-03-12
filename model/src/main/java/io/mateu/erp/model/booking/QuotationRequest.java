package io.mateu.erp.model.booking;

import com.google.common.base.Strings;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Grid;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.themes.ValoTheme;
import io.mateu.erp.dispo.Helper;
import io.mateu.erp.model.booking.parts.HotelBookingLine;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.partners.Agency;
import io.mateu.erp.model.workflow.AbstractTask;
import io.mateu.erp.model.workflow.SendEmailTask;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.interfaces.GridDecorator;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.config.Template;
import lombok.Getter;
import lombok.Setter;
import org.javamoney.moneta.FastMoney;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
public class QuotationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Section("Info")
    @Embedded
    @Output
    private Audit audit;

    @Output
    @ManyToOne
    private File file;


    @NotNull
    @ManyToOne
    @ListColumn
    private Agency agency;

    @ListColumn@KPI
    private boolean active = true;

    @ListColumn@KPI
    private boolean confirmed;

    @ListColumn
    private String title;

    @NotNull
    @ListColumn
    private Currency currency;

    @KPI
    @ListColumn
    private double total;

    @KPI
    @ListColumn
    private double totalCost;

    @KPI
    @ListColumn
    private double totalMarkup;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rq")
    @NotInlineEditable
    private List<QuotationRequestLine> lines = new ArrayList<>();

    public String getLinesHtml() {
        String h = "<div class='lines'>";
        for (QuotationRequestLine l : lines) {
            h += "<div class='line" + (l.isActive() ? "" : " cancelled") + "'>";
            h += l.toString();
            h += "</div>";
        }
        h += "</div>";

        return h;
    }


    @Section("Operation")
    @ListColumn
    private LocalDate expiryDate;

    @TextArea
    private String text;

    @TextArea
    private String privateComments;

    @Section("Contact")

    private String name;

    private String email;

    private String telephone;


    @NotNull@Ignored
    private QuotationRequestAnswer answer = QuotationRequestAnswer.PENDING;

    @UseLinkToListView
    @OneToMany(cascade = CascadeType.ALL)
    private List<AbstractTask> tasks = new ArrayList<>();

    @Ignored
    private LocalDateTime readTime;

    @Ignored
    private String reader;

    @Ignored
    private LocalDateTime answerTime;

    @Ignored
    private String answerText;

    @Ignored
    private FastMoney answerPrice;

    public void updateTotal() {
        double t = 0;
        double c = 0;
        for (QuotationRequestLine line : lines) if (line.isActive()) {
            t += line.getTotal();
            c += line.getTotalCost();
        }
        setTotal(Helper.roundEuros(t));
        setTotalCost(Helper.roundEuros(c));
        setTotalMarkup(Helper.roundEuros(t - c));
    }

    @Action(saveAfter = true, order = 1, confirmationMessage = "Are you sure you want to cancel this quotation?", style = ValoTheme.BUTTON_DANGER, icon = VaadinIcons.CLOSE)
    public void cancel() {
        setActive(false);
    }

    public boolean isCancelVisible() {
        return isActive() && !isConfirmed();
    }

    @Action(saveAfter = true, order = 2, confirmationMessage = "Are you sure you want to confirm this quotation?", style = ValoTheme.BUTTON_FRIENDLY, icon = VaadinIcons.CHECK)
    public void confirm() {
        setConfirmed(true);
    }

    public boolean isConfirmVisible() {
        return isActive() && !isConfirmed();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj != null && obj instanceof QuotationRequest && id == ((QuotationRequest) obj).getId());
    }

    @Override
    public String toString() {
        return !Strings.isNullOrEmpty(title)?title:"Quotation request " + id;
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

                            if (o instanceof QuotationRequest) {
                                if (!((QuotationRequest)o).isActive()) s = (s != null)?s + " cancelled":"cancelled";
                            } else {
                                if (!((Boolean)((Object[])o)[3])) {
                                    s = (s != null)?s + " cancelled":"cancelled";
                                }
                            }
                            return s;
                        }
                    });
                });
            }
        };
    }











    @Action(order = 3, icon = VaadinIcons.ENVELOPE, saveBefore = true, saveAfter = true)
    @NotWhenCreating
    public void sendEmail(@Help("If blank the postscript will be sent as the email body") Template template, String changeEmail, @Help("If blank, the subject from the templaet will be used") String subject, @TextArea String postscript, boolean includeProforma) throws Throwable {


        io.mateu.mdd.core.util.Helper.transact(em ->{

            long t0 = new Date().getTime();

            AppConfig appconfig = AppConfig.get(em);

            String to = changeEmail;
            if (Strings.isNullOrEmpty(to)) {
                to = getEmail();
            }
            if (Strings.isNullOrEmpty(to)) {
                to = getAgency().getEmail();
            }
            if (Strings.isNullOrEmpty(to)) throw new Exception("No valid email address. Please check the agency " + getAgency().getName() + " and fill the email field.");


            SendEmailTask t;
            tasks.add(t = new SendEmailTask());

            t.setTo(to);
            t.setAudit(new Audit(MDD.getCurrentUser()));

            String msg = postscript;

            String freemark = appconfig.getVouchersEmailTemplate();

            if (template != null) {
                Map<String, Object> data = io.mateu.mdd.core.util.Helper.getGeneralData();
                data.put("postscript", postscript);
                t.setDescription("Send email from template " + template.getName());
                t.setSubject(!Strings.isNullOrEmpty(subject) ? subject : template.getSubject());
                msg = io.mateu.mdd.core.util.Helper.freemark(template.getFreemarker(), data);
            } else {
                t.setDescription("Send email from void template");
                t.setSubject(subject);
                msg = postscript;
            }

            t.setMessage(msg);

            em.merge(this);


        });

    }
}
