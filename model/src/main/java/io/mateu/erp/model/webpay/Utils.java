package io.mateu.erp.model.webpay;

public class Utils {

	private static final String CODEPAGE = "Cp1250";

	public static String toFullHexString(byte[] cislo) {
		StringBuffer vystup = new StringBuffer();
		for (int i = 0; i < cislo.length; i++) {
			vystup.append(toFullHexString(cislo[i]));
		}
		return vystup.toString();
	}

	public static String toFullHexString(String text) throws Exception {
		// int pomoc = 0;
		String vystup = "";
		try {
			byte[] b = text.getBytes(CODEPAGE);
			vystup = toFullHexString(b);
		}
		catch (Exception e) {
			System.err.println("Nepodporovane kodovani cestiny!");
			throw new Exception(e);
		}
		return vystup;
	}

	public static String toFullHexString(byte cislo) {
		StringBuffer vystup = new StringBuffer();
		int pomoc = getIntValue(cislo);
		if (pomoc < 16) {
			vystup.append('0');
		}
		return vystup.toString() + Integer.toHexString(pomoc).toUpperCase();
	}

	public static int getIntValue(byte x) {
		return (x < 0) ? x + 256 : x;
	}

	public static String fromFullHexString(String text) throws Exception {
		return new String(fromFullHexStringToBytes(text));
	}

	public static byte[] fromFullHexStringToBytes(String text) throws Exception {
		byte[] pom = null;

		if (text == null) {
			throw new Exception("Illegal argument format: null");
		}

		if ((text.trim().length() % 2) != 0) {
			throw new Exception("Illegal argument length: length MOD 2 != 0");
		}

		if (text.trim().length() > 0) {
			pom = new byte[text.trim().length() / 2];
			int pozice = 0;
			for (int i = 0; i < text.length(); i += 2) {
				pom[pozice] = (byte) Integer.parseInt(text.substring(i, i + 2), 16);
				pozice++;
			}
		}
		return pom;
	}
}
