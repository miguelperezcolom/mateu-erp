package io.mateu.erp.servlets;

import io.mateu.erp.model.tpv.TPVTransaction;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by miguel on 15/4/17.
 */
@WebServlet(urlPatterns = {"/tpv/*"})
public class TPVServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        System.out.println("uri:" + req.getRequestURI());
        System.out.println("url:" + req.getRequestURL());

        resp.addHeader("Access-Control-Allow-Origin", "*");

        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Expires", "0");
        resp.setContentType("text/html; charset=UTF-8");

        String uri = req.getRequestURI();
        try {

            if (uri.contains("lanzadera")) {
                resp.getWriter().print("<html>\n" +
                        "<head>\n" +
                        "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                        "    <title>Payment</title>\n" +
                        "</head>\n" +
                        "<body onload=\"document.f.submit();\">");
                resp.getWriter().print(TPVTransaction.getForm(Long.parseLong(req.getParameter("idtransaccion"))));
                resp.getWriter().print("</body>\n" +
                        "</html>");
            } else if (uri.contains("notificacion")) {
                TPVTransaction.procesarPost(req, resp);
            } else if (uri.contains("ok")) {
                resp.getWriter().print(Helper.leerInputStream(this.getClass().getResourceAsStream("/io/mateu/erp/html/tpvok.html"), "utf-8"));
            } else if (uri.contains("ko")) {
                resp.getWriter().print(Helper.leerInputStream(this.getClass().getResourceAsStream("/io/mateu/erp/html/tpvok.html"), "utf-8"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }


    }

}
