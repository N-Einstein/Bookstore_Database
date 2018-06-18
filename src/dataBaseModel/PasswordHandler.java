package dataBaseModel;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHandler {
	private static PasswordHandler instance = new PasswordHandler();

	private PasswordHandler() {

	}

	public static PasswordHandler getInstance() {
		return instance;
	}

	public String encrypt(String password) {
		if (password.equals(""))
			return password;
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(password.getBytes());

			byte byteData[] = md.digest();

			// convert the byte to hex format method 1
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));

			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;

	}

	public boolean compare(String origPassword, String ecriptedPassword) {
		String encripted = encrypt(origPassword);
		return encripted.equals(ecriptedPassword);
	}

}