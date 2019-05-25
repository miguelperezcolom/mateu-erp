package io.mateu.erp.model.email;

import com.Ostermiller.util.CSVParser;
import com.Ostermiller.util.CSVPrinter;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.sun.mail.pop3.POP3Folder;
import io.mateu.erp.model.authentication.ERPUser;
import io.mateu.erp.model.booking.transfer.Importer;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.importing.TraveltinoImportTask;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.partners.Agency;
import io.mateu.erp.model.product.Tariff;
import io.mateu.mdd.core.model.util.Constants;
import io.mateu.mdd.core.model.util.EmailHelper;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;
import io.mateu.mdd.core.workflow.WorkflowEngine;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.mail.*;
import javax.persistence.EntityManager;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import static org.apache.fop.fonts.type1.AdobeStandardEncoding.w;

/**
 * Created by miguel on 19/5/17.
 */
public class Pop3Reader {

    private static boolean testing;


    public static void main(String... args) throws Throwable {

        System.setProperty("appconf", "/home/miguel/work/quotravel.properties");

        EmailHelper.setTesting(true);


        testTraveltino();

        /*
        testing = true;

        //test();

        read();
        */

        WorkflowEngine.exit(0);
    }

    private static void testTraveltino() throws IOException {

        System.out.println("test traveltino on /home/miguel/work/viajesibiza");

        Files.list(Paths.get("/home/miguel/work/viajesibiza")).filter(p -> p.getFileName().toString().startsWith("traveltino_")).sorted().forEach(p -> {

            System.out.println(p.getFileName());


            if (true) {
                try {
                    String[][] csv = CSVParser.parse(new FileReader(p.toFile()), '¬');

                    if (csv != null) {

                        StringWriter sw = new StringWriter();
                        new CSVPrinter(sw, 'x', '"','¬').println(csv);

                        try {
                            Helper.transact(em -> {
                                ERPUser u = em.find(ERPUser.class, Constants.IMPORTING_USER_LOGIN);
                                TraveltinoImportTask t = new TraveltinoImportTask("Traveltino", u, (Agency) Helper.selectObjects("select x from Agency x where x.name = 'TRAVELTINO'").get(0),sw.toString(), (Office) Helper.selectObjects("select x from Office x where x.name = 'Ibiza'").get(0), em.find(Tariff.class, 1l), (PointOfSale) Helper.selectObjects("select x from PointOfSale x where x.name = 'Importación'").get(0), em.find(BillingConcept.class, "TRA"));
                                em.persist(t);

                            });
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

        });

        //TransferBookingRequest.run();
    }


    public static void read() throws InterruptedException {

        boolean dentro = true;

        while (dentro) {

            int[] read = {0};

            try {
                Helper.transact(new JPATransaction() {
                    @Override
                    public void run(EntityManager em) throws Throwable {

                        io.mateu.erp.model.config.AppConfig appconfig = io.mateu.erp.model.config.AppConfig.get(em);

                        if (!Strings.isNullOrEmpty(appconfig.getPop3Host())
                                && !Strings.isNullOrEmpty(appconfig.getPop3User())
                                && !Strings.isNullOrEmpty(appconfig.getPop3Password())
                                && !Strings.isNullOrEmpty(appconfig.getAdminEmailSmtpHost())
                                && !Strings.isNullOrEmpty(appconfig.getAdminEmailUser())
                                && !Strings.isNullOrEmpty(appconfig.getAdminEmailPassword())
                                ) {
                            // connect to my pop3 inbox
                            Properties properties = System.getProperties();
                            properties.setProperty("mail.transport.protocol", "smtp");
                            properties.setProperty("mail.smtp.host", appconfig.getAdminEmailSmtpHost());
                            properties.setProperty("mail.host", appconfig.getAdminEmailSmtpHost());
//                        properties.setProperty("mail.user", appconfig.geta);
//                        properties.setProperty("mail.password", password);
                            Session session = Session.getDefaultInstance(properties);
                            Store store = session.getStore("pop3");
                            store.connect(appconfig.getPop3Host(), appconfig.getPop3User(), appconfig.getPop3Password());

                            Folder inbox = store.getFolder("Inbox");

                            inbox.open(Folder.READ_WRITE);

                            try {

                                // get the list of inbox messages
                                Message[] messages = inbox.getMessages();




                                if (messages.length == 0) System.out.println("" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ": No messages found.");
                                else System.out.println("" + messages.length + " messages read from pop3");

                                for (int i = 0; i < messages.length; i++) {
                                    if (i > 10) break;

                                    Message m = messages[i];

                                    if (!testing) m.setFlag(Flags.Flag.DELETED, true);

                                    try {


                                        System.out.println("Message " + (i + 1));
                                        System.out.println("From : " + m.getFrom()[0]);
                                        System.out.println("Subject : " + m.getSubject());
                                        System.out.println("Sent Date : " + m.getSentDate());
                                        System.out.println();

                                        if ("error".equalsIgnoreCase(m.getSubject())) throw new Exception("este no es correcto");
                                        else if ("pickups".equalsIgnoreCase(m.getSubject())) procesarPickups(em, appconfig, m);
                                        else if (m.getSubject() != null && m.getSubject().toLowerCase().contains("banktransfers")) procesarTraveltino(em, appconfig, m);


                                    } catch (Throwable e) {
                                        e.printStackTrace();

                                        Helper.resend(appconfig.getAdminEmailSmtpHost(), appconfig.getAdminEmailSmtpPort(), appconfig.getAdminEmailUser(), appconfig.getAdminEmailPassword(), m, "ERROR:" + e.getClass().getName() + "(" + e.getMessage() + ")", appconfig.getPop3ReboundToEmail(), null);

                                    }


                                }

                            } catch (Throwable e) {
                                e.printStackTrace();
                            }



                            inbox.close(!testing);
                            store.close();
                        }

                    }
                });
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

            if (!testing && read[0] == 0) Thread.currentThread().sleep(60000);

            dentro = !testing;
        }

    }

    private static void procesarTraveltino(EntityManager em, AppConfig appconfig, Message m) throws Throwable {

        Object o = m.getContent();
        //Si es Formato HTML lo procesamos...
        if (m.isMimeType("multipart/mixed")) {
            Multipart mp = (Multipart) o;
            int cnt = mp.getCount();
            for (int j = 0; j < cnt; j++) {
                Part part = mp.getBodyPart(j);

                String disposition = part.getDisposition();
                if ((disposition != null) && ((disposition.equals(Part.ATTACHMENT) || disposition.equals(Part.INLINE)))) {

                    String[][] csv = CSVParser.parse(new InputStreamReader(part.getInputStream(), Charsets.UTF_8), '¬');

                    if (csv != null) {

                        if (testing) {
                            new CSVPrinter(new FileWriter("/home/miguel/work/viajesibiza/traveltino_" + System.currentTimeMillis() + ".csv")).println(csv);
                        }


                        StringWriter sw = new StringWriter();
                        new CSVPrinter(sw).println(csv);

                        ERPUser u = em.find(ERPUser.class, Constants.IMPORTING_USER_LOGIN);
                        TraveltinoImportTask t = new TraveltinoImportTask(m.getSubject(), u, (Agency) Helper.selectObjects("select x from Agency x where x.name = 'TRAVELTINO'").get(0),sw.toString(), (Office) Helper.selectObjects("select x from Office x where x.name = 'Ibiza'").get(0), em.find(Tariff.class, 1l), (PointOfSale) Helper.selectObjects("select x from PointOfSale x where x.name = 'Importación'").get(0), em.find(BillingConcept.class, "TRA"));
                        em.persist(t);

                    }

                }
            }
        }

        Helper.resend(appconfig.getAdminEmailSmtpHost(), appconfig.getAdminEmailSmtpPort(), appconfig.getAdminEmailUser(), appconfig.getAdminEmailPassword(), m, "TRAVELTINO MANIFEST READ", appconfig.getPop3ReboundToEmail(), null);

    }

    private static void procesarPickups(EntityManager em, io.mateu.erp.model.config.AppConfig appconfig, Message m) throws Throwable {

        Object o = m.getContent();
        //Si es Formato HTML lo procesamos...
        if (m.isMimeType("multipart/mixed")) {
            Multipart mp = (Multipart) o;
            int cnt = mp.getCount();
            for (int j = 0; j < cnt; j++) {
                Part part = mp.getBodyPart(j);

                String disposition = part.getDisposition();
                if ((disposition != null) && ((disposition.equals(Part.ATTACHMENT) || disposition.equals(Part.INLINE)))) {

                    Workbook wb = null;

                    Object adjunto = null;

                    if (part.getFileName().toLowerCase().endsWith(".xls")) {

                        try {

                            wb = new HSSFWorkbook(part.getInputStream());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } else if (part.getFileName().toLowerCase().endsWith(".xlsx")) {

                        try {

                            wb = new XSSFWorkbook(part.getInputStream());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    if (w != null) {

                        Object[][] l = Helper.readExcel(wb)[0];

                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);

                        Importer.importPickupTimes(em, l, pw);

                        Helper.resend(appconfig.getAdminEmailSmtpHost(), appconfig.getAdminEmailSmtpPort(), appconfig.getAdminEmailUser(), appconfig.getAdminEmailPassword(), m, "PICKUPS READ", appconfig.getPop3ReboundToEmail(), null, sw.toString());


                    }



                }
            }
        }


    }

    private static void test() throws Throwable {

        // mail server connection parameters
        String host = "mail.invisahoteles.com"; // 995
        String user = "inbox@viajesibiza.es";
        String pass = "Y4t3n3m0sXML";

        // connect to my pop3 inbox
        Properties properties = System.getProperties();
        Session session = Session.getDefaultInstance(properties);
        Store store = session.getStore("pop3");
        store.connect(host, user, pass);


        /*


        Folder inbox = store.getFolder("Inbox");

        inbox.open(Folder.READ_ONLY);

        // get the list of inbox messages
        int count = inbox.getMessageCount();
        System.out.println("" + count + " messages in inbox for " + user);
        if (count > 0) {
            int max = count;
            if (max > 10) max = 10;

            Message[] messages = inbox.getMessages();

            if (messages.length == 0) System.out.println("No messages found.");

            for (int i = 0; i < messages.length; i++) {
                System.out.println("Message " + (i + 1));
                System.out.println("From : " + messages[i].getFrom()[0]);
                System.out.println("Subject : " + messages[i].getSubject());
                System.out.println("Sent Date : " + messages[i].getSentDate());
                System.out.println();

            }
        }
        */



        POP3Folder inbox = (POP3Folder) store.getFolder("Inbox");
        inbox.open(Folder.READ_WRITE);
        FetchProfile profile = new FetchProfile();
        profile.add(UIDFolder.FetchProfileItem.UID);
        Message[] messages = inbox.getMessages();
        inbox.fetch(messages,profile);
        for(int i = 0;i < messages.length;i++)
        {
            String uid = inbox.getUID(messages[i]);
            System.out.print(i);
            System.out.print(". ");
            System.out.println(inbox.getMessage(i + 1).getSubject());
            messages[i].setFlag(Flags.Flag.DELETED, true);
        }
        System.out.println("Done.");

        inbox.close(true);
        store.close();


    }

}
