package io.mateu.erp.tests;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.mdd.core.util.Helper;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.UUID;

public class VoucherPDFTester {

    public static void main(String[] args) throws Throwable {

        System.setProperty("appconf", "/home/miguel/work/erp.properties");


        //testHtmlPreview(16);

        test(41, "/home/miguel/work");

    }


    private static void test(long bookingId, String donde) throws Throwable {


        Helper.notransact(em -> {


            Booking b = em.find(Booking.class, bookingId);

            long t0 = System.currentTimeMillis();


            try {


                Document xml = new Document(new Element("services"));

                if (AppConfig.get(em).getLogo() != null) xml.getRootElement().setAttribute("urllogo", "file:" + AppConfig.get(em).getLogo().toFileLocator().getTmpPath());


                for (Service s : b.getServices()) {
                    xml.getRootElement().addContent(s.toXml());
                }

                System.out.println(Helper.toString(xml.getRootElement()));

                try {
                    String archivo = UUID.randomUUID().toString();
                    archivo = "testvouchers";

                    File temp = new File(new File(donde), archivo + ".pdf");

                    System.out.println("Temp file : " + temp.getAbsolutePath());

                    FileOutputStream fileOut = new FileOutputStream(temp);
                    //String sxslfo = Resources.toString(Resources.getResource(Contract.class, xslfo), Charsets.UTF_8);
                    String sxml = new XMLOutputter(Format.getPrettyFormat()).outputString(xml);
                    System.out.println("xml=" + sxml);
                    fileOut.write(Helper.fop(new StreamSource(new StringReader(Helper.leerFichero(HotelContract.class.getResourceAsStream("/io/mateu/erp/xsl/voucher.xsl")))), new StreamSource(new StringReader(sxml))));
                    fileOut.close();



                } catch (IOException e) {
                    e.printStackTrace();
                }


            } catch (Exception e1) {
                e1.printStackTrace();
            }

            System.out.println("hecho en " + (System.currentTimeMillis() - t0) + "ms.");


        });


    }
}
