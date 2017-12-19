package io.mateu.erp.server.booking;

import io.mateu.erp.model.booking.Service;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.AbstractServerSideWizard;
import io.mateu.ui.mdd.server.ERPServiceImpl;
import io.mateu.ui.mdd.server.WizardPageVO;
import io.mateu.ui.mdd.server.annotations.Output;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by miguel on 23/4/17.
 */
public class TransferOperations extends AbstractServerSideWizard {

    public static final String ACTION_CHECKRETAINED = "CHECKRETAINED";
    public static final String ACTION_CHECKFLIGHTTIMES = "CHECKFLIGHTTIMES";
    public static final String ACTION_MAP = "MAP";
    public static final String ACTION_SETPICKUPS = "SETPICKUPS";
    public static final String ACTION_SENDTOPROVIDERS = "SENDTOPROVIDERS";



    @Override
    public WizardPageVO execute(UserData user, EntityManager em, String action, Data data) throws Throwable {
        if (action == null) {
            return getInitialPage(user, em, data);
        } else {
            switch (action) {
                case ACTION_CHECKRETAINED:
                    return getRetainedBookings(user, em, data);
                case ACTION_CHECKFLIGHTTIMES:
                    break;
                case ACTION_MAP:
                    break;
                case ACTION_SETPICKUPS:
                    break;
                case ACTION_SENDTOPROVIDERS:
                    break;

            }
            return null;
        }
    }

    private WizardPageVO getRetainedBookings(UserData user, EntityManager em, Data data) throws Throwable {
        WizardPageVO vo = new WizardPageVO();
        vo.setData(data);
        vo.setWizardClassName(this.getClass().getName());
        vo.setFirstPage(true);

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {
                List<Service> l = em.createQuery("select x from TransferService x where x.start = ? and x.retained order by x.flighttime").setParameter(0, data.getDate("workDate")).getResultList();
                if (l.size() == 0) {
                    vo.setMetaData(new ERPServiceImpl().getMetadaData(user, em, new Object() {
                        @Output
                        String date;
                    }.getClass()));
                } else {
                    vo.setMetaData(new ERPServiceImpl().getMetadaData(user, em, new Object() {
                        @Output
                        String date;
                    }.getClass()));
                }
            }
        });

        return vo;
    }

    private WizardPageVO getInitialPage(UserData user, EntityManager em, Data data) throws Throwable {
        WizardPageVO vo = new WizardPageVO();
        vo.setData(data);
        vo.setWizardClassName(this.getClass().getName());
        vo.setFirstPage(true);
        vo.setMetaData(new ERPServiceImpl().getMetadaData(user, em, new Object() {
            @NotNull
            LocalDate workDate;
        }.getClass()));
        return vo;
    }
}
