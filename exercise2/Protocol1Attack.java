import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Protocol1Attack {

	private static final String ENCRYPTED_SESSION_KEY = "3fcd9e51c9705df150716f6920b08338740083d3a525cf4c4a044b8d20fb1f0";
	private static final String hostName = "127.0.0.1";
	private static final int portNumber = 11337;

	// By Wireshark, we know that here we have the encrypted session key:
		// 3fcd9e51c9705df150716f6920b08338740083d3a525cf4c4a044b8d20fb1f0

		// By Wireshark we have the nonce:
		// 7245396af552155f5acb49b6fd576f79

		// We have the encrypted nonce:
		// d39a6115a157ca6d29afbb31a9260c9c

		// We have the encrypted secret value using the session key
		// 8740083d3a525cf4c4a044b8d20fb1f0e679b7f569281ea8bc688b888dc5c3b573162c9e2dbb5bb94fc88ea4b0f4fc6f89a3a0690ff3dca409ca0e7f48070d185fbc4b1c614dcce402cc5a26e485120c

	public static void main(String[] args) {
		try {
			Socket connection = new Socket(hostName, portNumber);
			InputStream input = connection.getInputStream();
			OutputStream output = connection.getOutputStream();

			output.write(hexStringToByteArray(ENCRYPTED_SESSION_KEY));
			output.flush();

			byte[] returned = new byte[112];
			input.read(returned);
			connection.close();
			byteArrayToHexString(returned);
		} catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Stolen from Protocol1Server.java
	 */
	private static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i+1), 16));
		}
		return data;
	}

	/**
	 * Stolen from Protocol1Server.java
	 */
	private static String byteArrayToHexString(byte[] data) { 
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) { 
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do { 
				if ((0 <= halfbyte) && (halfbyte <= 9)) 
					buf.append((char) ('0' + halfbyte));
				else 
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while(two_halfs++ < 1);
		} 
		return buf.toString();
	} 
}