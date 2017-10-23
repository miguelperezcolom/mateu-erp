package io.mateu.erp.model.webpay;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class CertData {
    public static X509Certificate getCertFromBytes(byte[] cert) throws CertificateException{
    X509Certificate certif = null;
    CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
    InputStream input = new ByteArrayInputStream(cert);
    certif = (X509Certificate) certFactory.generateCertificate(input);
    return certif;
  }
}
