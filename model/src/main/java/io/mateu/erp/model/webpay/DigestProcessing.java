package io.mateu.erp.model.webpay;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Properties;

public class DigestProcessing {
	private static String keyStoreFile = "";
	private static String keyStorePwd = "";
	private static String privateKeyAlias = "";
	private static String privateKeyPwd = "";
	private static String publicKeyFile = "";
	private static String publicKeyHex = "";
	private static boolean urlEncode = false;
	private static boolean urlEncodeInput = false;
	private static String encoding = "UTF-8";

	private static void pouziti() {
		System.out.println("Konfigurace klice a certifikatu je ulozena v souboru \"digestProc.properties\"");
		System.out.println("Pouziti:");
		System.out.println("- vypocet podpisu: java -jar digestProc.jar -s <retezec pro vypocet>");
		System.out.println("- vypocet podpisu: java -jar digestProc.jar -sf <soubor s retezem pro vypocet>");
		System.out.println("");
		System.out.println("- overeni podpisu: java -jar digestProc.jar -v <retezec pro vypocet> <podpis>");
		System.out.println("- overeni podpisu: java -jar digestProc.jar -vf <soubor s retezem pro vypocet> <soubor s podpisem>");
		System.out.println("");
		System.out.println("nebo");
		System.out.println("");
		System.out.println("- vypocet podpisu: digestProc.exe -s <retezec pro vypocet>");
		System.out.println("- vypocet podpisu: digestProc.exe -sf <soubor s retezem pro vypocet>");
		System.out.println("");
		System.out.println("- overeni podpisu: digestProc.exe -v <retezec pro vypocet> <podpis>");
		System.out.println("- overeni podpisu: digestProc.exe -vf <soubor s retezem pro vypocet> <soubor s podpisem>");
		System.out.println("");
		System.out.println("Pokud <retezec pro vypocet> obsahuje mezery je nutne jej dat do uvozovek.");
	}

	public static void main(String[] args) throws Exception {
		System.out.println("");
		System.out.println("");
		System.out.println("Digest Processing, (c) 10/2014 Dimitrij R. Holovka");
		System.out.println("");
		if ((args.length < 2) || (args.length > 3)) {
			pouziti();
			System.exit(1);
		}
		System.out.print("---- Nacitani parametru --- ");
		FileInputStream f = new FileInputStream("digestProc.properties");
		Properties p = new Properties();
		p.load(f);
		f.close();
		System.out.println("OK");

		keyStoreFile = p.getProperty("keyStoreFile");
		keyStorePwd = p.getProperty("keyStorePwd");
		privateKeyAlias = p.getProperty("privateKeyAlias");
		privateKeyPwd = p.getProperty("privateKeyPwd");
		publicKeyFile = p.getProperty("publicKeyFile");
		publicKeyHex = p.getProperty("publicKeyHex");
		urlEncode = p.getProperty("output_urlencoded").equalsIgnoreCase("true");
		urlEncodeInput = p.getProperty("input_urlencoded").equalsIgnoreCase("true");
		encoding = p.getProperty("encoding");

		System.out.println("Soubor keyStore: " + keyStoreFile);
		System.out.println("Heslo keyStore: " + keyStorePwd);
		System.out.println("Alias privatniho klice: " + privateKeyAlias);
		System.out.println("Heslo privatniho klice: " + privateKeyPwd);

		KeyStore ks = KeyStore.getInstance("JKS");
		InputStream fis = null;
		try {
			fis = new FileInputStream(keyStoreFile);
		}
		catch (FileNotFoundException e) {
			throw new KeyStoreException("KeyStore not found.");
		}
		byte[] keyStore = new byte[fis.available()];
		fis.read(keyStore);
		fis.close();
		try {
			ks.load(new ByteArrayInputStream(keyStore), keyStorePwd.toCharArray());
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(1);
		}
		catch (CertificateException e) {
			e.printStackTrace();
			System.exit(1);
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		PrivateKey priKey = null;
		try {
			priKey = (PrivateKey) ks.getKey(privateKeyAlias, privateKeyPwd.toCharArray());
		}
		catch (UnrecoverableKeyException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		System.out.println("Privatni klic: OK");

		if (publicKeyFile != null && publicKeyFile.trim().length() > 0) {
			System.out.println("Soubor publicKeyStore: " + publicKeyFile);
		}
		if (publicKeyHex != null && publicKeyHex.trim().length() > 0) {
			System.out.println("Soubor publicKeyStoreHEX: zadano");
		}
		if (publicKeyFile != null && publicKeyFile.trim().length() > 0 && publicKeyHex != null && publicKeyHex.trim().length() > 0) {
			System.out.println("JKS soubor verejneho klice MA PREDNOST pred HEX tvarem.");
		}

		byte[] data;
		if (publicKeyFile != null && publicKeyFile.trim().length() > 0) {
			fis = new FileInputStream(publicKeyFile);
			data = new byte[fis.available()];
			fis.read(data);
			fis.close();
		}
		else {
			data = Utils.fromFullHexStringToBytes(publicKeyHex);
		}

		Certificate publicKey = CertData.getCertFromBytes(data);
		System.out.println("Verejny klic: OK");
		System.out.println("");
		System.out.println("URLEncoded podpis na vstupu: " + urlEncodeInput);
		System.out.println("Provadet URLEncode na vystupu: " + urlEncode);
		System.out.println("Kodova stranka: " + encoding);
		System.out.println("-------------------------------------");
		if ((args[0].equalsIgnoreCase("-s")) || (args[0].equalsIgnoreCase("-sf"))) {
			System.out.println("Vytvoreni podpisu pomoci privatniho klice:");
			String dataForSign;
			if (args[0].equalsIgnoreCase("-s")) {
				dataForSign = new String(args[1].getBytes(encoding), encoding);
			}
			else {
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(args[1]));
				byte[] buffer = new byte[in.available()];
				in.read(buffer);
				in.close();
				dataForSign = new String(buffer, encoding);
			}
			String digest = Digest.sign(dataForSign, priKey);
			System.out.println("Delka podpisu: " + digest.length());
			if (urlEncode) {
				digest = URLEncoder.encode(digest, encoding);
				System.out.println("Delka podpisu po URLEncoding: " + digest.length());
			}
			System.out.println("Soubor s podpisem: digestProc.sign");
			FileOutputStream fos = new FileOutputStream("digestProc.sign");
			fos.write(digest.getBytes());
			fos.close();
			System.out.println("Data k podpisu: \"" + dataForSign + "\"");
			System.out.println("Podpis:");
			System.out.println(digest);
		}
		else if ((args[0].equalsIgnoreCase("-v")) || (args[0].equalsIgnoreCase("-vf"))) {
			System.out.println("Overeni podpisu pomoci verejneho klice:");
			if (args.length == 3) {
				String digestForVerification;
				String dataForVerification;
				if (args[0].equalsIgnoreCase("-v")) {
					dataForVerification = args[1];
					digestForVerification = args[2];
				}
				else {
					BufferedInputStream in = new BufferedInputStream(new FileInputStream(args[1]));
					byte[] buffer = new byte[in.available()];
					in.read(buffer);
					in.close();
					dataForVerification = new String(buffer, encoding);
					in = new BufferedInputStream(new FileInputStream(args[2]));
					buffer = new byte[in.available()];
					in.read(buffer);
					in.close();
					digestForVerification = new String(buffer, encoding);
				}
				if (urlEncodeInput) {
					digestForVerification = URLDecoder.decode(digestForVerification, encoding);
				}
				boolean overeni = Digest.verifyByCertificate(dataForVerification, digestForVerification, publicKey);
				System.out.println("Vysledek overeni: " + overeni);
			}
			else {
				System.out.println("Spatny pocet argumentu!");
				pouziti();
			}
		}
		else {
			System.out.println("Neznamy parametr! : " + args[0]);
			pouziti();
		}
	}
}