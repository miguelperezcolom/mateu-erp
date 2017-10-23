package io.mateu.erp.model.webpay;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class Digest {
	// public static final String METHOD = "SHA-1/RSA/PKCS#1";
	public static final String METHOD = "SHA1withRSA";
	public static final String ALGORITHM = "RSA";
	public static final int KEYLENGTH = 2048;
	public static final String ENCODING = "UTF-8";

	private Digest() {
	}

	public static void main(String[] args) throws IOException, KeyException {

		Security.addProvider(new BouncyCastleProvider());


		File privateKeyFile = new File("/Users/miguel/Downloads/GP_webpay_Implementation_Examples_v2.1_CZ_EN/progs/generate_certificate/generate_certificate_en/estec.rsaprivada"); // private key file in PEM format
		PEMReader pemParser = new PEMReader(new FileReader(privateKeyFile));
		Object object = pemParser.readObject();
		System.out.println("leido " + object.getClass().getName());
		KeyPair kp = (KeyPair) object;
		System.out.println(kp.getPrivate());
		/*
		PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder().build(password);
		JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
		KeyPair kp;
		if (object instanceof PEMEncryptedKeyPair) {
			System.out.println("Encrypted key - we will use provided password");
			kp = converter.getKeyPair(((PEMEncryptedKeyPair) object).decryptKeyPair(decProv));
		} else {
			System.out.println("Unencrypted key - no password needed");
			kp = converter.getKeyPair((PEMKeyPair) object);
		}

		String keyPath = "mykey.pem";
		BufferedReader br = new BufferedReader(new FileReader(keyPath));
		Security.addProvider(new BouncyCastleProvider());
		PEMReader pp = new PEMReader(br);
		PEMKeyPair pemKeyPair = (PEMKeyPair) pp.readObject();
		KeyPair kp = new JcaPEMKeyConverter().getKeyPair(pemKeyPair);
		pp.close();
		samlResponse.sign(Signature.getInstance("SHA1withRSA").toString(), kp.getPrivate(), certs);
		sign("hola!!", Helper.leerByteArray(new FileInputStream("/Users/miguel/Downloads/GP_webpay_Implementation_Examples_v2.1_CZ_EN/progs/generate_certificate/generate_certificate_en/estec.rsaprivada")));
		*/
	}


	public static void mainxx(String[] args) throws Exception {
		System.out.println("");
		String message = "Pokusn� zpr�va";
		String podpis = Digest.sign(message, "keystore", "changeit", "novy");
		System.out.println("Podpis je: " + podpis);
		if ((podpis != null) && Digest.verify(message, podpis, "keystore", "changeit", "novy")) {
			System.out.println("Podpis ov��en");
		}
		else {
			System.out.println("Podpis se nepoda�ilo ov��it");
		}
	}

	public static boolean verifyByCertificate(String message, String signature, String certBCD) throws CertificateException {
		try {
			return verifyByCertificate(message, signature, Utils.fromFullHexStringToBytes(certBCD));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new CertificateException(ex);
		}
	}

	public static boolean verifyByCertificate(String message, String signature, byte[] cert) throws CertificateException, KeyException {
		Certificate certif = null;
		try {
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			InputStream input = new ByteArrayInputStream(cert);
			certif = certFactory.generateCertificate(input);
			certif.verify(certif.getPublicKey());
		}
		catch (SignatureException ex) {
			ex.printStackTrace();
			throw new CertificateException(ex);
		}
		catch (NoSuchProviderException ex) {
			ex.printStackTrace();
			throw new CertificateException(ex);
		}
		catch (InvalidKeyException ex) {
			ex.printStackTrace();
			throw new CertificateException(ex);
		}
		catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
			throw new CertificateException(ex);
		}
		catch (CertificateException ex) {
			ex.printStackTrace();
			throw new CertificateException(ex);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new CertificateException(ex);
		}
		return verifyByCertificate(message, signature, certif);
	}

	public static boolean verifyByCertificate(String message, String signature, Certificate certif) throws KeyException {
		return verify(message, signature, certif.getPublicKey());
	}

	public static boolean verify(String message, String signature, String publicKeyBCD) throws KeyException {
		byte[] key = null;
		try {
			key = Utils.fromFullHexStringToBytes(publicKeyBCD);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new KeyException("Bad BCD");
		}
		return verify(message, signature, key);
	}

	public static boolean verify(String message, String signature, Certificate cert) throws CertificateException, KeyException {
		try {
			cert.verify(cert.getPublicKey());
		}
		catch (SignatureException ex) {
			ex.printStackTrace();
			throw new CertificateException(ex);
		}
		catch (NoSuchProviderException ex) {
			ex.printStackTrace();
			throw new CertificateException(ex);
		}
		catch (InvalidKeyException ex) {
			ex.printStackTrace();
			throw new CertificateException(ex);
		}
		catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
			throw new CertificateException(ex);
		}
		catch (CertificateException ex) {
			ex.printStackTrace();
			throw new CertificateException(ex);
		}
		return verify(message, signature, cert.getPublicKey());
	}

	public static boolean verify(String message, String signature, byte[] publicKey) throws KeyException {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(publicKey);
			PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
			return verify(message, signature, pubKey);
		}
		catch (InvalidKeySpecException ex) {
			ex.printStackTrace();
			throw new KeyException(ex);
		}
		catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
			throw new KeyException(ex);
		}
	}

	public static boolean verify(String message, String signature, String keystore, String keystorepass, String alias) throws KeyStoreException, KeyException {
		KeyStore ks = KeyStore.getInstance("JKS");
		InputStream fis = null;
		try {
			fis = new FileInputStream(keystore);
			ks.load(fis, keystorepass.toCharArray());
			Certificate cert = ks.getCertificate(alias);
			PublicKey pubKey = cert.getPublicKey();
			return verify(message, signature, pubKey);
		}
		catch (FileNotFoundException e) {
			throw new KeyStoreException("KeyStore not found.");
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new KeyException(e);
		}
		catch (CertificateException e) {
			e.printStackTrace();
			throw new KeyException(e);
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new KeyException(e);
		}
		finally {
			if (fis != null) {
				try {
					fis.close();
				}
				catch (IOException e) {
					// nezajima nas, ze to neslo zavrit
				}
			}
		}

	}

	public static boolean verify(String message, String signature, PublicKey pub) throws KeyException {
		init();
		// BASE64Decoder dec = new BASE64Decoder();
		try {
			Signature sig = Signature.getInstance(METHOD);
			// byte[] signature2 = dec.decodeBuffer(signature);
			byte[] signature2 = Base64.decode(signature.getBytes(ENCODING));
			sig.initVerify(pub);
			sig.update(message.getBytes(ENCODING));
			// System.out.println(sig.getProvider());
			if (sig.verify(signature2)) {
				return true;
			}
			else {
				return false;
			}
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new KeyException(e);
		}
		catch (InvalidKeyException e) {
			e.printStackTrace();
			throw new KeyException(e);
		}
		catch (SignatureException e) {
			e.printStackTrace();
			throw new KeyException(e);
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new KeyException(e);
		}
	}

	public static String sign(String message, String keystore, String keystorepass, String alias) throws KeyStoreException, KeyException {
		KeyStore ks = KeyStore.getInstance("JKS");
		InputStream fis = null;
		try {
			fis = new FileInputStream(keystore);
			ks.load(fis, keystorepass.toCharArray());
			PrivateKey priKey = null;
			priKey = (PrivateKey) ks.getKey(alias, keystorepass.toCharArray());
			return sign(message, priKey);
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new KeyException(e);
		}
		catch (CertificateException e) {
			e.printStackTrace();
			throw new KeyException(e);
		}
		catch (FileNotFoundException e) {
			throw new KeyStoreException("KeyStore not found.");

		}
		catch (UnrecoverableKeyException e) {
			e.printStackTrace();
			throw new KeyException(e);
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new KeyException(e);
		}
		finally {
			if (fis != null) {
				try {
					fis.close();
				}
				catch (IOException e) {
					// nezajima nas, ze to neslo zavrit
				}
			}
		}
	}

	public static String sign(String message, String privateKey) throws KeyException {
		byte[] key = null;
		try {
			key = Utils.fromFullHexString(privateKey).getBytes(ENCODING);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new KeyException(ex);
		}
		return sign(message, key);
	}

	public static String sign(String message, byte[] privateKey) throws KeyException {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
			PKCS8EncodedKeySpec priKeySpec = new PKCS8EncodedKeySpec(privateKey);
			PrivateKey priKey = keyFactory.generatePrivate(priKeySpec);
			return sign(message, priKey);
		}
		catch (InvalidKeySpecException ex) {
			ex.printStackTrace();
			throw new KeyException(ex);
		}
		catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
			throw new KeyException(ex);
		}
	}

	public static String sign(String message, PrivateKey privateKey) throws KeyException {
		init();
		// BASE64Encoder enc = new BASE64Encoder();
		try {
			Signature sig = Signature.getInstance(METHOD);
			sig.initSign(privateKey);
			sig.update(message.getBytes(ENCODING));
			byte[] signature = sig.sign();
			// String signEnc = enc.encode(signature);
			String signEnc = new String(Base64.encode(signature));
			signEnc = signEnc.replaceAll("\r", "").replaceAll("\n", "");
			return signEnc;
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new KeyException(e);
		}
		catch (InvalidKeyException e) {
			e.printStackTrace();
			throw new KeyException(e);
		}
		catch (SignatureException e) {
			e.printStackTrace();
			throw new KeyException(e);
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new KeyException(e);
		}
	}

	public static String signByKeystoreBCD(String message, String keyStoreBCD, String keyStorePsw, String alias) throws KeyStoreException, KeyException {
		byte[] keyStore = null;
		try {
			keyStore = Utils.fromFullHexStringToBytes(keyStoreBCD);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new KeyStoreException("Bad BCD");
		}
		InputStream fis = null;
		KeyStore ks = null;
		PrivateKey priKey = null;
		try {
			ks = KeyStore.getInstance("JKS");
			fis = new ByteArrayInputStream(keyStore);
			ks.load(fis, keyStorePsw.toCharArray());
			priKey = (PrivateKey) ks.getKey(alias, keyStorePsw.toCharArray());
			return sign(message, priKey);
		}
		catch (UnrecoverableKeyException ex) {
			ex.printStackTrace();
			throw new KeyException(ex);
		}
		catch (KeyStoreException ex) {
			ex.printStackTrace();
			throw new KeyException(ex);
		}
		catch (CertificateException ex) {
			ex.printStackTrace();
			throw new KeyException(ex);
		}
		catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
			throw new KeyException(ex);
		}
		catch (IOException ex) {
			ex.printStackTrace();
			throw new KeyException(ex);
		}
		finally {
			if (fis != null) {
				try {
					fis.close();
				}
				catch (IOException e) {
					// nezajima nas, ze to neslo zavrit
				}
			}
		}
	}

	private static void init() {
		/*
		 * Provider cryptix_provider = new CryptixCrypto(); int result = Security.insertProviderAt(cryptix_provider, 6); if
		 * (result == -1) { System.out.println("Provider entry already in file.\n" + cryptix_provider.toString()); } else {
		 * System.out.println("Provider added at position: " + result + "\n" + cryptix_provider.toString()); }
		 */
	}
}
