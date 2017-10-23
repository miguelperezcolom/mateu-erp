package io.mateu.erp.model.webpay;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.net.ssl.*;
import javax.xml.bind.DatatypeConverter;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;

/**
 * @author tomola
 */
public class AES {
	/**
	 * Path to the SSL keystore (it's assumed that it contains both server certificates and the client certificate)
	 */
	public static final String SSL_KEYSTORE_PATH = "./SSLKeystore.jks";
	/**
	 * Password for accessing SSL keystore and password protected data inside the keystore
	 */
	public static final char[] SSL_KEYSTORE_PASSWORD = "111111".toCharArray();
	/**
	 * Type of SSL keystore
	 */
	public static final String SSL_KEYSTORE_TYPE = "JCEKS";

	/**
	 * Path to the CRYPTO keystore (it's assumed that it contains an AES key for data encryption, an RSA private key for the
	 * signature and a certificate with GPE public key for signature verification)
	 */
	public static final String CRYPTO_KEYSTORE_PATH = "./CryptoKeystore.jks";
	/**
	 * Password for accessing CRYPTO keystore and password protected data inside the keystore
	 */
	public static final char[] CRYPTO_KEYSTORE_PASSWORD = "111111".toCharArray();
	/**
	 * Type of CRYPTO keystore
	 */
	public static final String CRYPTO_KEYSTORE_TYPE = "JCEKS";
	/**
	 * Initialization vector for AES CBC operations
	 */
	public static final IvParameterSpec CRYPTO_IV = new IvParameterSpec(new byte[16]);
	/**
	 * The label of the private key in CRYPTO keystore
	 */
	public static final String CRYPTO_KEY_ALIAS_SIGNATURE = "myPrivateKey";
	/**
	 * The label of the certificate with the public key in CRYPTO keystore
	 */
	public static final String CRYPTO_KEY_ALIAS_VERIFY = "gpePublicKey";
	/**
	 * The label of the AES key in CRYPTO keystore
	 */
	public static final String CRYPTO_KEY_ALIAS_AES = "aes_test_key";

	/**
	 * Endpoint for sending WS requests
	 */
	public static final String SERVICE_URL = "https://test.3dsecure.gpwebpay.com/pay-ws/PaymentService";

	/**
	 * Cached cipher for data encryption
	 */
	protected static Cipher cipher;
	/**
	 * Cached signature for request data signing
	 */
	protected static Signature signer;
	/**
	 * Cached signature for response data verification
	 */
	protected static Signature verifier;

	/**
	 * Loads a keystore according to the given parameters
	 */
	protected static KeyStore loadKeyStore(final File keyStorePath, final char[] keyStorePassword, final String keyStoreType) throws Exception {
		final KeyStore keyStore = KeyStore.getInstance(keyStoreType);
		final BufferedInputStream bis = new BufferedInputStream(new FileInputStream(keyStorePath));
		try {
			keyStore.load(bis, keyStorePassword);
		}
		finally {
			bis.close();
		}

		return keyStore;
	}

	/**
	 * Sets up SSL context for WS communication
	 */
	protected static void setUpSsl() throws Exception {
		final KeyStore sslKeystore = loadKeyStore(new File(SSL_KEYSTORE_PATH), SSL_KEYSTORE_PASSWORD, SSL_KEYSTORE_TYPE);

		final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(sslKeystore);

		final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(sslKeystore, SSL_KEYSTORE_PASSWORD);

		final SSLContext context = SSLContext.getInstance("SSL");
		context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
		final SSLSocketFactory fac = context.getSocketFactory();

		HttpsURLConnection.setDefaultSSLSocketFactory(fac);
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			/**
			 * @see HostnameVerifier#verify(String, SSLSession)
			 */
			public boolean verify(final String hostname, final SSLSession session) {
				return true;
			}
		});
	}

	/**
	 * Loads the given key from CRYPTO keystore
	 */
	protected static Key loadKey(final String keyAlias) throws Exception {
		final KeyStore cryptoKeystore = loadKeyStore(new File(CRYPTO_KEYSTORE_PATH), CRYPTO_KEYSTORE_PASSWORD, CRYPTO_KEYSTORE_TYPE);

		return cryptoKeystore.getKey(keyAlias, CRYPTO_KEYSTORE_PASSWORD);
	}

	/**
	 * Loads the given certificate from CRYPTO keystore
	 */
	protected static Certificate loadCertificate(final String keyAlias) throws Exception {
		final KeyStore cryptoKeystore = loadKeyStore(new File(CRYPTO_KEYSTORE_PATH), CRYPTO_KEYSTORE_PASSWORD, CRYPTO_KEYSTORE_TYPE);

		return cryptoKeystore.getCertificate(keyAlias);
	}

	/**
	 * Initializes or returns previously initialized cipher for data encryption
	 */
	protected static Cipher getCipher() throws Exception {
		if (cipher == null) {
			final Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			aesCipher.init(Cipher.ENCRYPT_MODE, loadKey(CRYPTO_KEY_ALIAS_AES), CRYPTO_IV);

			cipher = aesCipher;
		}

		return cipher;
	}

	/**
	 * Initializes or returns previously initialized signature for WS request data signing
	 */
	protected static Signature getSigner() throws Exception {
		if (signer == null) {
			final Signature rsaSigner = Signature.getInstance("SHA1withRSA");
			rsaSigner.initSign((PrivateKey) loadKey(CRYPTO_KEY_ALIAS_SIGNATURE));

			signer = rsaSigner;
		}

		return signer;
	}

	/**
	 * Initializes or returns previously initialized signature for WS response data verification
	 */
	protected static Signature getVerifier() throws Exception {
		if (verifier == null) {
			final Signature rsaVerifier = Signature.getInstance("SHA1withRSA");
			rsaVerifier.initVerify(loadCertificate(CRYPTO_KEY_ALIAS_VERIFY).getPublicKey());

			verifier = rsaVerifier;
		}

		return verifier;
	}

	public static void main(String[] args) throws Exception {
		final Cipher cipher = getCipher();
		//System.out.println(DatatypeConverter.printHexBinary(cipher.doFinal("4056070000000008".getBytes())));
		//C0D69DA40DF22619745752CC17154F8B292A3D0620097DA09302EEA235E8ED54
		
		
		System.out.println(DatatypeConverter.printHexBinary(cipher.doFinal("4056070000000016|2012|435".getBytes("UTF-8"))));
		//2F60364A9B514F19A75D70DBA85B3E76
		//18f12b2bad858d011055ca3c6a9f4767
	}
	
}