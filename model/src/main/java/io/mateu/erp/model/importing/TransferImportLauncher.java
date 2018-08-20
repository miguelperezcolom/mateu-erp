package io.mateu.erp.model.importing;


import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by miguel on 1/5/17.
 */
@WebServlet(urlPatterns = {"/launchtransferimports"})
public class TransferImportLauncher extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Helper.transact(new JPATransaction() {
                @Override
                public void run(EntityManager em) throws Throwable {
                    List<TransferAutoImport> l = em.createQuery("select x from " + TransferAutoImport.class.getName() + " x").getResultList();
                    for (TransferAutoImport t : l) {
                        t.getBookings(LocalDate.now(), Integer.parseInt(req.getParameter("days")));
                    }
                }
            });
            resp.getWriter().print("ok");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throwable.printStackTrace(resp.getWriter());
        }
    }
}
