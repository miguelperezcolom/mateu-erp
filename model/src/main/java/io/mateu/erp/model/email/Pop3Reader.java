package io.mateu.erp.model.email;

import com.Ostermiller.util.CSVParser;
import com.sun.mail.pop3.POP3Folder;
import io.mateu.erp.model.booking.transfer.Importer;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.importing.TraveltinoImporter;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.mail.*;
import javax.persistence.EntityManager;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import static org.apache.fop.fonts.type1.AdobeStandardEncoding.w;

/**
 * Created by miguel on 19/5/17.
 */
public class Pop3Reader {

    public static void main(String... args) throws Throwable {

        //test();

        read();
    }

    public static void read() throws InterruptedException {

        while (true) {

            int[] read = {0};

            try {
                Helper.transact(new JPATransaction() {
                    @Override
                    public void run(EntityManager em) throws Throwable {

                        io.mateu.erp.model.config.AppConfig appconfig = io.mateu.erp.model.config.AppConfig.get(em);


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

                            for (int i = 0; i < messages.length; i++) {
                                if (i > 10) break;

                                Message m = messages[i];

                                m.setFlag(Flags.Flag.DELETED, true);

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



                        inbox.close(true);
                        store.close();

                    }
                });
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

            if (read[0] == 0) Thread.currentThread().sleep(60000);

            if (false) { // para que sonarlint no se queje ;)
                break;
            }

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

                    String[][] csv = CSVParser.parse(new InputStreamReader(part.getInputStream()), '¬');

                    if (w != null) {

                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);

                        TraveltinoImporter.process(em, csv, pw);

                        Helper.resend(appconfig.getAdminEmailSmtpHost(), appconfig.getAdminEmailSmtpPort(), appconfig.getAdminEmailUser(), appconfig.getAdminEmailPassword(), m, "TRAVELTINO MANIFEST READ", appconfig.getPop3ReboundToEmail(), null, sw.toString());


                    }



                }
            }
        }


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
