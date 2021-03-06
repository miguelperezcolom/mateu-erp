package io.mateu.erp.model.tpv;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.payments.BookingPaymentAllocation;
import io.mateu.erp.model.payments.MethodOfPayment;
import io.mateu.erp.model.payments.Payment;
import io.mateu.erp.model.payments.PaymentLine;
import io.mateu.erp.model.webpay.Digest;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import sis.redsys.api.ApiMacSha256;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Setter
public class TPVTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne@NotNull
    private TPV tpv;

    @ManyToOne@NotNull
    private Currency currency;

    private double value;

    private LocalDateTime created = LocalDateTime.now();

    private String log;

    private TPVTRANSACTIONSTATUS status = TPVTRANSACTIONSTATUS.PENDING;

    private String language;

    @ManyToOne@NotNull
    private Booking booking;

    private String subject;




    public static String getForm(final long transactionId) throws Throwable {
        final String[] h = {""};

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {

                TPVTransaction t = em.find(TPVTransaction.class, transactionId);

                h[0] = t.getForm(em, t.getLanguage(), t.getBooking().getId(), t.getId(), t.getValue(), "" + t.getCurrency().getIsoCode(), t.getSubject());

            }
        });

        return h[0];
    }

    public String getForm(EntityManager em, final String language, final long bookingId, final long transactionId, final double amount, final String currency, final String subject) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        String Merchant_ConsumerLanguage;
        if ("es".equals(language)) Merchant_ConsumerLanguage = "001";
        else if ("en".equals(language)) Merchant_ConsumerLanguage = "002";
        else if ("fr".equals(language)) Merchant_ConsumerLanguage = "004";
        else if ("de".equals(language)) Merchant_ConsumerLanguage = "005";
        else if ("it".equals(language)) Merchant_ConsumerLanguage = "007";
        else Merchant_ConsumerLanguage = "001";


        StringBuffer h = new StringBuffer();


        if (getBooking() != null && getBooking().getExpiryDate() != null) {

            if ("es".equals(language)) h.append("<h2>Por favor recuerde que tiene hasta el " + getBooking().getExpiryDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " a las " + getBooking().getExpiryDate().format(DateTimeFormatter.ofPattern("HH:mm")) + " para realizar el pago. Si no es posible confirmar el mismo, esta reserva se cancelará automáticamente.</h2>");
            else h.append("<h2>Please notice you can pay until " + getBooking().getExpiryDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " at " + getBooking().getExpiryDate().format(DateTimeFormatter.ofPattern("HH:mm")) + ". If it is not possible to confirm the payment by that time this booking will be automatically cancelled.</h2>");

        }


        if (TPVTYPE.SERMEPA.equals(getTpv().getType())) {


            ApiMacSha256 apiMacSha256 = new ApiMacSha256();

            Map<String, String> values = new HashMap<>();
            values.put("DS_MERCHANT_AMOUNT", getMerchantAmount(getTpv().isTesting()?0.1:amount));
            values.put("DS_MERCHANT_ORDER", new DecimalFormat("0000").format(getId()));
            values.put("DS_MERCHANT_MERCHANTCODE", getTpv().getMerchantCode());
            values.put("DS_MERCHANT_CURRENCY", getMerchantCurrency(em, currency));
            values.put("DS_MERCHANT_TRANSACTIONTYPE", "0");
            values.put("DS_MERCHANT_TERMINAL", getTpv().getMerchantTerminal());
            values.put("DS_MERCHANT_MERCHANTURL", getTpv().getNotificationUrl() + "/" + getId());
            values.put("DS_MERCHANT_URLOK", getTpv().getOkUrl());
            values.put("DS_MERCHANT_URLKO", getTpv().getKoUrl());


            values.forEach((k,v) -> System.out.println("" + k + ": " + v));

            values.forEach((k,v) -> apiMacSha256.setParameter(k, v));

            String params = apiMacSha256.createMerchantParameters();
            String signature = apiMacSha256.createMerchantSignature(getTpv().getMerchantSecret());

            h.append("<form name=f action='" + getTpv().getActionUrl() + "' method='post'>");
            h.append("<input type='hidden' name=Ds_SignatureVersion value='HMAC_SHA256_V1' />");
            h.append("<input type='hidden' name=Ds_MarchantParametersvalue='" + params + "' />");
            h.append("<input type='hidden' name=Ds_Signature value='" + signature + "' />");
            h.append("<input type='submit' value='Realizar Pago' />");
            h.append("</form>");

/*
            h.append("<form name=f action='" + getTpv().getActionUrl() + "' method='post'>");
            h.append("<input type='hidden' name=Ds_Merchant_MerchantName value='" + getTpv().getMerchantName() + "' />");
            h.append("<input type='hidden' name=Ds_Merchant_MerchantCode value='" + getTpv().getMerchantCode() + "' />");
            h.append("<input type='hidden' name=Ds_Merchant_ConsumerLanguage value='" + Merchant_ConsumerLanguage + "' />");
            h.append("<input type='hidden' name=Ds_Merchant_Terminal value='" + getTpv().getMerchantTerminal() + "' />");
            h.append("<input type='hidden' name=Ds_Merchant_Order value='" + getId() + "' />");
            h.append("<input type='hidden' name=Ds_Merchant_Amount value='" + getMerchantAmount(amount) + "' />");
            h.append("<input type='hidden' name=Ds_Merchant_Currency value='" + getMerchantCurrency(em, currency) + "' />");
            h.append("<input type='hidden' name=Ds_Merchant_TransactionType value='" + getMerchantTransactionType() + "' />");
            h.append("<input type='hidden' name=Ds_Merchant_MerchantURL value='" + getTpv().getNotificationUrl() + "?idtransaccion=" + getId() + "' />");
            h.append("<input type='hidden' name=Ds_Merchant_MerchantSignature value='" + getSignature(em, transactionId, amount, currency, subject) + "' />");
            h.append("<input type='hidden' name=Ds_Merchant_UrlOK value='" + getTpv().getOkUrl() + "' />");
            h.append("<input type='hidden' name=Ds_Merchant_UrlKO value='" + getTpv().getKoUrl() + "' />");
            h.append("<input type='hidden' name=Ds_Merchant_ProductDescription value='" + subject + "' />");
            h.append("</form>");
            */
        } else if (TPVTYPE.WEBPAY.equals(getTpv().getType())) {
            //MERCHANTNUMBER + | + OPERATION + | + ORDERNUMBER + | + AMOUNT + | + CURRENCY + | + DEPOSITFLAG + | + MERORDERNUM + | + URL + | + DESCRIPTION + | + MD

            h.append("<form name=f action='" + getTpv().getActionUrl() + "' method='post'>");
            h.append("<input type='hidden' name=MERCHANTNUMBER value='" + getTpv().getMerchantCode() + "' />");
            h.append("<input type='hidden' name=OPERATION value='CREATE_ORDER' />");
            h.append("<input type='hidden' name=ORDERNUMBER value='" + getId() + "' />");
            h.append("<input type='hidden' name=AMOUNT value='" + getMerchantAmount(getTpv().isTesting()?0.1:amount) + "' />");
            h.append("<input type='hidden' name=CURRENCY value='" + getMerchantCurrency(em, currency) + "' />");
            h.append("<input type='hidden' name=DEPOSITFLAG value='1' />");
            h.append("<input type='hidden' name=MERORDERNUM value='" + getId() + "' />");
            h.append("<input type='hidden' name=URL value='" + getTpv().getNotificationUrl() + "?idtransaccion=" + getId() + "' />");
            h.append("<input type='hidden' name=DESCRIPTION value='" + subject + "' />");
            h.append("<input type='hidden' name=MD value='' />");
            h.append("<input type='hidden' name=DIGEST value='" + getSignature(em, transactionId, amount, currency, subject) + "' />");
            h.append("<input type='submit'>");
            h.append("</form>");
        } else if (TPVTYPE.PAYPAL.equals(getTpv().getType())) {

            h.append("<form action='https://www.paypal.com/cgi-bin/webscr' method='post'>"
                    + "<!-- Identify your business so that you can collect the payments. -->"
                    + "<input type='hidden' name='business' value='" + getTpv().getPaypalEmail() + "'>"
                    + "<!-- Specify a Buy Now button. -->"
                    + "<input type='hidden' name='cmd' value='_xclick'>"
                    + "<!-- Specify details about the item that buyers will purchase. -->"
                    + "<input type='hidden' name='notify_url' value='" + getTpv().getNotificationUrl() + "'>"
                    + "<input type='hidden' name='item_number' value='" + getId() + "'>"
                    + "<input type='hidden' name='item_name' value='BOOKING NR " + bookingId + "'>"
                    + "<input type='hidden' name='amount' value='" + Helper.roundOffEuros(getTpv().isTesting()?0.1:amount) + "'>"
                    + "<input type='hidden' name='currency_code' value='" + currency + "'>"
                    + "<!-- Display the payment button. -->"
                    + "<input type='image' name='submit' border='0' src='https://www.paypalobjects.com/en_US/i/btn/btn_buynow_LG.gif' alt='PayPal - The safer, easier way to pay online'>"
                    + "<img alt='' border='0' width='1' height='1' src='https://www.paypalobjects.com/en_US/i/scr/pixel.gif' >"
                    + "</form>");

        } else {
            h.append("<h1>UNKNOWN TPV TYPE: " + getTpv().getType() + "</h1>");
        }

        System.out.println(h);

        return h.toString();
    }


    private String getMerchantTransactionType() {
        String Merchant_TransactionType = "0";
        return Merchant_TransactionType;
    }


    private String getMerchantCurrency(EntityManager em, String currencyCode) {
        return "" + em.find(Currency.class, currencyCode).getIsoNumericCode();
    }

    public static void main(String[] args) throws Throwable {
        System.out.println("-->" + new DecimalFormat("####.00").format(0.1).replaceAll(",", "").replaceAll("\\.", ""));

    }


    private String getMerchantAmount(double amount) {
        return new DecimalFormat("####.00").format(amount).replaceAll(",", "").replaceAll("\\.", "");
    }

    public String getBoton(EntityManager em) throws Exception {
        return getBoton(em, this.getBooking().getId(), getValue(), getCurrency());
    }

    public String getBoton(EntityManager em, final long bookingId, final double amount, final Currency currency) throws Exception {
        if (TPVTYPE.SERMEPA.equals(getTpv().getType()) || TPVTYPE.WEBPAY.equals(getTpv().getType())) {
            ApiMacSha256 apiMacSha256 = new ApiMacSha256();

            Map<String, String> values = new HashMap<>();
            values.put("DS_MERCHANT_AMOUNT", getMerchantAmount(getTpv().isTesting()?0.1:amount));
            values.put("DS_MERCHANT_ORDER", new DecimalFormat("0000").format(getId()));
            values.put("DS_MERCHANT_MERCHANTCODE", getTpv().getMerchantCode());
            values.put("DS_MERCHANT_CURRENCY", getMerchantCurrency(em, currency.getIsoCode()));
            values.put("DS_MERCHANT_TRANSACTIONTYPE", "0");
            values.put("DS_MERCHANT_TERMINAL", getTpv().getMerchantTerminal());
            values.put("DS_MERCHANT_MERCHANTURL", getTpv().getNotificationUrl() + "/" + getId());
            values.put("DS_MERCHANT_URLOK", getTpv().getOkUrl());
            values.put("DS_MERCHANT_URLKO", getTpv().getKoUrl());

            values.forEach((k,v) -> System.out.println("" + k + ": " + v));

            values.forEach((k,v) -> apiMacSha256.setParameter(k, v));

            String params = apiMacSha256.createMerchantParameters();
            String signature = apiMacSha256.createMerchantSignature(getTpv().getMerchantSecret());

            StringBuffer h = new StringBuffer();

            h.append("<form name=f action='" + getTpv().getActionUrl() + "' method='post'>");
            h.append("<input type='hidden' name=Ds_SignatureVersion value='HMAC_SHA256_V1' />");
            h.append("<input type='hidden' name=Ds_MerchantParameters value='" + params + "' />");
            h.append("<input type='hidden' name=Ds_Signature value='" + signature + "' />");
            h.append("<input type='submit' class='btn btn-primary' value='PAY NOW' />");
            h.append("</form>");

            return h.toString();
        } else if (TPVTYPE.PAYPAL.equals(getTpv().getType())) {
            return "<form action='https://www.paypal.com/cgi-bin/webscr' method='post'>"
                    + "<!-- Identify your business so that you can collect the payments. -->"
                    + "<input type='hidden' name='business' value='" + getTpv().getPaypalEmail() + "'>"
                    + "<!-- Specify a Buy Now button. -->"
                    + "<input type='hidden' name='cmd' value='_xclick'>"
                    + "<!-- Specify details about the item that buyers will purchase. -->"
                    + "<input type='hidden' name='notify_url' value='" + getTpv().getNotificationUrl() + "'>"
                    //+ "<input type='hidden' name='return' value='" + getOkUrl() + "'>"
                    + "<input type='hidden' name='item_number' value='" + getId() + "'>"
                    + "<input type='hidden' name='item_name' value='BOOKING NR " + bookingId + "'>"
                    + "<input type='hidden' name='amount' value='" + Helper.roundOffEuros(amount) + "'>"
                    + "<input type='hidden' name='currency_code' value='" + currency.getIsoCode() + "'>"
                    + "<!-- Display the payment button. -->"
                    + "<input type='image' name='submit' border='0' src='http://www.paypalobjects.com/en_US/i/btn/btn_paynowCC_LG.gif' alt='PayPal - The safer, easier way to pay online'>"
                    + "<img alt='' border='0' width='1' height='1' src='http://www.paypalobjects.com/en_US/i/scr/pixel.gif' >"
                    + "</form>";
        } else {
            return "<h1>UNKNOWN TPV TYPE: " + getTpv().getType() + "</h1>";
        }
    }

    private String getSignature(EntityManager em, final long bookingId, final double amount, final String currency, final String subject) throws NoSuchAlgorithmException {

        if (TPVTYPE.WEBPAY.equals(getTpv().getType())) {

			/*
						h.append("<form name=f action='" + getActionUrl() + "' method='post'>");
			h.append("<input type='hidden' name=MERCHANTNUMBER value='" + getMerchantCode() + "' />");
			h.append("<input type='hidden' name=OPERATION value='CREATE_ORDER' />");
			h.append("<input type='hidden' name=ORDERNUMBER value='" + Helper.getID(this) + "' />");
			h.append("<input type='hidden' name=AMOUNT value='" + getMerchantAmount() + "' />");
			h.append("<input type='hidden' name=CURRENCY value='" + getMerchantCurrency() + "' />");
			h.append("<input type='hidden' name=DEPOSITFLAG value='1' />");
			h.append("<input type='hidden' name=MERORDERNUM value='" + Helper.getID(this) + "' />");
			h.append("<input type='hidden' name=URL value='" + getNotificationUrl() + "?idtransaccion=" + Helper.getID(this) + "' />");
			h.append("<input type='hidden' name=DESCRIPTION value='" + getSubject() + "' />");
			h.append("<input type='hidden' name=DIGEST value='" + getSignature() + "' />");
			h.append("</form>");

			 */

            //MERCHANTNUMBER + | + OPERATION + | + ORDERNUMBER + | + AMOUNT + | + CURRENCY + | + DEPOSITFLAG + | + MERORDERNUM + | + URL + | + DESCRIPTION + | + MD
            String contenido = "" + getTpv().getMerchantCode() + "|" + "CREATE_ORDER" + "|" + getId() + "|" + getMerchantAmount(amount) + "|" + getMerchantCurrency(em, currency) + "|" + "1" + "|" + bookingId + "|" + getTpv().getNotificationUrl() + "?idtransaccion=" + bookingId + "|" + subject + "|" + "";

            System.out.println("contenido=" + contenido);
            System.out.println("clave=" + getTpv().getPrivateKey());

            String firma = null;
            try {
                Security.addProvider(new BouncyCastleProvider());

                PEMReader pemParser = new PEMReader(new StringReader(getTpv().getPrivateKey()));
                Object object = pemParser.readObject();
                System.out.println("leido " + object.getClass().getName());
                KeyPair kp = (KeyPair) object;
                firma = Digest.sign(contenido, kp.getPrivate());

                System.out.println("firma=" + firma);
                System.out.println(Digest.sign(contenido, "/Users/miguel/Downloads/GP_webpay_Implementation_Examples_v2.1_CZ_EN/progs/generate_certificate/generate_certificate_en/estec.ks", "antonia", "gpwebpay"));
                //firma = Digest.sign(contenido, "/Users/miguel/Downloads/GP_webpay_Implementation_Examples_v2.1_CZ_EN/test_keystore_and_certificate/test.ks", "test", "gpwebpay");

                System.out.println("firma=" + firma);

            } catch (KeyException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                //} catch (KeyStoreException e) {
                //	e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }

            System.out.println("Firma: " + firma);

            return firma;
        } else if (TPVTYPE.SERMEPA.equals(getTpv().getType())) {
            byte bAmount[] = getMerchantAmount(amount).getBytes();
            byte bOrder[] = ("" + getId()).getBytes();
            byte bCode[] = getTpv().getMerchantCode().getBytes();
            byte bCurrency[] = getMerchantCurrency(em, currency).getBytes();
            byte bTransactionType[] = getMerchantTransactionType().getBytes();
            byte bMerchantURL[] = (getTpv().getNotificationUrl() + "?idtransaccion=" + getId()).getBytes();
            byte bMerchantURLOK[] = getTpv().getOkUrl().getBytes();
            byte bMerchantURLKO[] = getTpv().getKoUrl().getBytes();
            byte bPassword[] = getTpv().getMerchantSecret().getBytes();

            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            sha.update(bAmount);
            sha.update(bOrder);
            sha.update(bCode);
            sha.update(bCurrency);
            sha.update(bTransactionType);
            sha.update(bMerchantURL);
            byte[] hash = sha.digest(bPassword);

            int h = 0;
            String s = new String();

            final int SHA1_DIGEST_LENGTH = 20;

            String Merchant_Signature = "";

            for(int i = 0; i < SHA1_DIGEST_LENGTH; i++) {
                h = (int) hash[i];          // Convertir de byte a int
                if(h < 0) h += 256;  // Si son valores negativos, pueden haber problemas de conversi¢n.
                s = Integer.toHexString(h); // Devuelve el valor hexadecimal como un String
                if (s.length() < 2) Merchant_Signature = Merchant_Signature.concat("0"); // A¤ade un 0 si es necesario
                Merchant_Signature = Merchant_Signature.concat(s); // A¤ade la conversi¢n a la cadena ya existente
            }

            Merchant_Signature = Merchant_Signature.toUpperCase(); // Convierte la cadena generada a Mayusculas.
            System.out.println("Merchant_Signature: " + Merchant_Signature);

            return Merchant_Signature;
        }
        return null;
    }



    public static void procesarPost(final HttpServletRequest request, final HttpServletResponse response) throws Throwable {
//		final StringBuffer jb = new StringBuffer();
//		  String line = null;
//		  try {
//		    BufferedReader reader = request.getReader();
//		    while ((line = reader.readLine()) != null)
//		      jb.append(line);
//		  } catch (Exception e) { /*report an error*/ }
//
//		  System.out.println(jb.toString());


        //Ds_ErrorCode=SIS0051&Ds_TransactionType=0&Ds_Date=07%2F10%2F2015&Ds_SecurePayment=0&Ds_Order=3274851&Ds_Signature=D95493CF68BB847A98447A5DBD2BB8294DE1F374&Ds_Hour=11%3A58&Ds_Response=0913&Ds_AuthorisationCode=++++++&Ds_Currency=978&Ds_ConsumerLanguage=1&Ds_MerchantCode=266168905&Ds_Amount=10&Ds_Terminal=002
        //Ds_TransactionType=0&Ds_Card_Country=724&Ds_Date=08%2F10%2F2015&Ds_SecurePayment=1&Ds_Signature=342F34F69DB3A62FA9017F818136CFD6655C4BC8&Ds_Order=3276851&Ds_Hour=07%3A48&Ds_Response=0000&Ds_AuthorisationCode=311629&Ds_Currency=978&Ds_ConsumerLanguage=1&Ds_Card_Type=D&Ds_MerchantCode=266168905&Ds_Amount=10&Ds_Terminal=002

        System.out.println("****TPVNOTIFICACION****");
        Map<String, Object> params = new HashMap<>();
        for (String n : request.getParameterMap().keySet()) {
            for (String v : request.getParameterValues(n)) {
                System.out.println("" + n + ":" + v);
                params.put(n, v);
            }
        }

        if (params.containsKey("Ds_MerchantParameters")) {
            String json = new String(Base64.getDecoder().decode("eyJEc19EYXRlIjoiMTJcLzA2XC8yMDE5IiwiRHNfSG91ciI6IjE5OjMxIiwiRHNfU2VjdXJlUGF5bWVudCI6IjEiLCJEc19DYXJkX0NvdW50cnkiOiI3MjQiLCJEc19BbW91bnQiOiIxMCIsIkRzX0N1cnJlbmN5IjoiOTc4IiwiRHNfT3JkZXIiOiIwMDA5IiwiRHNfTWVyY2hhbnRDb2RlIjoiMDc5MTk0NjQzIiwiRHNfVGVybWluYWwiOiIwMDMiLCJEc19SZXNwb25zZSI6IjAwMDAiLCJEc19NZXJjaGFudERhdGEiOiIiLCJEc19UcmFuc2FjdGlvblR5cGUiOiIwIiwiRHNfQ29uc3VtZXJMYW5ndWFnZSI6IjEiLCJEc19BdXRob3Jpc2F0aW9uQ29kZSI6IjE2MTI2NyIsIkRzX0NhcmRfQnJhbmQiOiIxIn0="));
            System.out.println(json);
            Map<String, Object> m = Helper.fromJson(json);

            m.forEach((k,v) -> params.put(k, v));
        }

        System.out.println("****TPVNOTIFICACION****");

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {

                String uri = request.getRequestURI();



                String idtransaccion = uri.substring(uri.lastIndexOf("/") + 1);
                if (idtransaccion == null && request.getParameter("idtransaccion") != null) {
                    idtransaccion = request.getParameter("idtransaccion");
                }
                if (idtransaccion == null && request.getParameter("item_number") != null) {
                    idtransaccion = request.getParameter("item_number");
                }


                TPVTransaction t = em.find(TPVTransaction.class, Long.parseLong(idtransaccion));
                Booking r = t.getBooking();

                String log = t.getLog();
                if (log == null) log = "";
                if (!"".equals(log)) log += "\n";
                log += LocalDateTime.now() + "\n";
                log += uri + "\n";
                log += Helper.toJson(params);
                t.setLog(log);


                boolean ok = false;

                switch (t.getTpv().getType()) {
                    case SERMEPA:
                        String src = (String) params.get("Ds_Response");
                        ok = !Strings.isNullOrEmpty(src) && Helper.toInt(src) < 101;
                        break;
                    case WEBPAY:
                        String src2 = request.getParameter("PRCODE");
                        ok = !Strings.isNullOrEmpty(src2) && Helper.toInt(src2) == 0;
                        break;
                    case PAYPAL:
                        ok = "Completed".equalsIgnoreCase(request.getParameter("payment_status")) || "Processed".equalsIgnoreCase(request.getParameter("payment_status"));
                        break;
                }



                if (ok) {
                    t.setStatus(TPVTRANSACTIONSTATUS.OK);
                    String authCode = "";
                    if (TPVTYPE.WEBPAY.equals(t.getTpv().getType())) {
                        String msg = "";
                        if (request.getParameter("PRCODE") != null) {
                            if (!"".equalsIgnoreCase(msg)) msg += ", ";
                            msg += "PRCODE=" + request.getParameter("PRCODE");
                        }
                        if (request.getParameter("SRCODE") != null) {
                            if (!"".equalsIgnoreCase(msg)) msg += ", ";
                            msg += "SRCODE=" + request.getParameter("SRCODE");
                        }
                        if (request.getParameter("RESULTTEXT") != null) {
                            if (!"".equalsIgnoreCase(msg)) msg += ", ";
                            msg += "RESULTTEXT=" + request.getParameter("RESULTTEXT");
                        }
                        response.sendRedirect("tpvok.jsp?msg=" + msg);
                    } else if (TPVTYPE.SERMEPA.equals(t.getTpv().getType())) {
                        if (params.containsKey("Ds_AuthorisationCode")) authCode = (String) params.get("Ds_AuthorisationCode");
                    }


                    Payment p = new Payment();
                    p.setAgent(t.getBooking().getAgency().getFinancialAgent());
                    BookingPaymentAllocation a;
                    p.setBreakdown(Lists.newArrayList(a = new BookingPaymentAllocation()));
                    a.setBooking(t.getBooking());
                    a.getBooking().getPayments().add(a);
                    a.setValue(t.getValue());
                    a.setPayment(p);

                    PaymentLine l;
                    p.setLines(Lists.newArrayList(l = new PaymentLine()));
                    l.setPayment(p);
                    l.setValue(t.getValue());
                    l.setCurrency(t.getCurrency());
                    l.setMethodOfPayment(t.getTpv().getMethodOfPayment());
                    l.setTransactionCost(Helper.roundEuros(t.getTpv().getMethodOfPayment().getTransactionCostPercent() * t.getValue() / 100d));
                    l.setCurrencyExchangeCost(0);
                    l.setTransactionId(authCode);

                    p.setAccount(t.getTpv().getAccount());
                    p.setDate(LocalDate.now());
                    p.setTriggerUpdate(LocalDateTime.now());

                    em.persist(p);

                } else {
                    t.setStatus(TPVTRANSACTIONSTATUS.ERROR);
                    if (TPVTYPE.WEBPAY.equals(t.getTpv().getType())) {
                        String msg = "";
                        if (request.getParameter("PRCODE") != null) {
                            if (!"".equalsIgnoreCase(msg)) msg += ", ";
                            msg += "PRCODE=" + request.getParameter("PRCODE");
                        }
                        if (request.getParameter("SRCODE") != null) {
                            if (!"".equalsIgnoreCase(msg)) msg += ", ";
                            msg += "SRCODE=" + request.getParameter("SRCODE");
                        }
                        if (request.getParameter("RESULTTEXT") != null) {
                            if (!"".equalsIgnoreCase(msg)) msg += ", ";
                            msg += "RESULTTEXT=" + request.getParameter("RESULTTEXT");
                        }
                        response.sendRedirect("tpvko.jsp?msg=" + msg);
                    }
                }


            }
        });


    }


    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("##,###,###,###,###.00");
        return "<div style='text-align:right; width: 120px;display: inline-block;margin-right:10px;'>" + currency.getIsoCode() + " " + df.format(value) + "</div>" +
                "<div style='text-align:center; width: 100px;display: inline-block;margin-right:10px;'>" + status + "</div>" +
                "<div style='display: inline-block;'>" + subject + "</div>";

    }
}
