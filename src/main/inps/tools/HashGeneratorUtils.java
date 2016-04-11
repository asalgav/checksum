package main.inps.tools;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Hash functions utility class.
 * @author adrian.salas
 *
 */
public class HashGeneratorUtils {

	private HashGeneratorUtils() {
	}
	
	public static String generateMD5(String message) throws HashGenerationException {
		return hashString(message, "MD5");
	}

	public static String generateSHA1(String message) throws HashGenerationException {
		return hashString(message, "SHA-1");
	}
	
	public static String generateSHA256(String message) throws HashGenerationException {
		return hashString(message, "SHA-256");
	}
	
	public static String generateMD5(File mbfile, String hashResultPath) throws HashGenerationException {
		return hashFile(mbfile, hashResultPath, "MD5");
	}

	public static String generateMD5ExclusionPropList(File mbfile, File exclFile, String hashResultPath) throws HashGenerationException {
		return hashFileExcl(mbfile, exclFile, hashResultPath, "MD5");
	}
	
	public static String generateSHA1(File mbfile, String hashResultPath) throws HashGenerationException {
		return hashFile(mbfile, hashResultPath, "SHA-1");
	}
	
	public static String generateSHA256(File mbfile, String hashResultPath) throws HashGenerationException {
		return hashFile(mbfile, hashResultPath, "SHA-256");
	}
	
	private static String hashString(String message, String algorithm)
			throws HashGenerationException {
		
		try {
			MessageDigest digest = MessageDigest.getInstance(algorithm);
			byte[] hashedBytes = digest.digest(message.getBytes("UTF-8"));
			
			return convertByteArrayToHexString(hashedBytes);
		} catch (NoSuchAlgorithmException nsex) {
            throw new HashGenerationException(
                    "Could not generate hash from String", nsex);
        } catch(UnsupportedEncodingException ueex) {
			throw new HashGenerationException(
					"Could not generate hash from String", ueex);
		}
	}
	
	private static String hashFile(File file, String hashResultPath, String algorithm)
			throws HashGenerationException {
		String result;
		try {
            FileInputStream inputStream = new FileInputStream(file);
			MessageDigest digest = MessageDigest.getInstance(algorithm);
			
			byte[] bytesBuffer = new byte[1024];
			int bytesRead = -1;
			
			while ((bytesRead = inputStream.read(bytesBuffer)) != -1) {
				digest.update(bytesBuffer, 0, bytesRead);
			}
			
			byte[] hashedBytes = digest.digest();

			result = convertByteArrayToHexString(hashedBytes);

			saveHashResultIntoFile(result, hashResultPath);
			
			return result;
        } catch (Exception ex) {
            throw new HashGenerationException(
                    "Could not generate hash from String", ex);
        }
	}

	private static String hashFileExcl(File file, File exclusionProp, String hashResultPath, String algorithm)
			throws HashGenerationException {
		Properties prop = new Properties();
		try {
            FileInputStream inputStream = new FileInputStream(file);
			MessageDigest digest = MessageDigest.getInstance(algorithm);

			prop.load(inputStream);
			//Exclude the list of properties
			for (String property : getListProperties2Exclude(exclusionProp)) {
				prop.remove(property);
			}

			File file2 = new File(Constants.TEMP_PROPERTIES_PATH);
			OutputStream out = new FileOutputStream(file2);
			ByteArrayOutputStream arrayOut = new ByteArrayOutputStream();
			prop.store(arrayOut, null);

			//Delete comment lines to avoid changes on MD5 numbers and '\' characters
			String string = new String(arrayOut.toByteArray(), "8859_1");
			String sep = System.getProperty("line.separator");
			String content = string.substring(string.indexOf(sep) + sep.length());
			content = content.replaceAll("\\\\", "");
			out.write(content.getBytes("8859_1"));


			InputStream is
                    = new FileInputStream(Constants.TEMP_PROPERTIES_PATH);

			byte[] bytesBuffer = new byte[1024];
			int bytesRead = -1;

			while ((bytesRead = is.read(bytesBuffer)) != -1) {
				digest.update(bytesBuffer, 0, bytesRead);
			}

			byte[] hashedBytes = digest.digest();


			String result = convertByteArrayToHexString(hashedBytes);
			saveHashResultIntoFile(result, hashResultPath);
			return result;

        } catch (Exception ex) {
            throw new HashGenerationException(
                    "Could not generate hash from String", ex);
        }
	}
	
	private static String convertByteArrayToHexString(byte[] arrayBytes) {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < arrayBytes.length; i++) {
			stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
					.substring(1));
		}		
		return stringBuffer.toString();
	}

	private static Set<String> getListProperties2Exclude(File exclusionProp) throws IOException {

		Properties prop2excl = new Properties();
		FileInputStream inputStream = new FileInputStream(exclusionProp);
		prop2excl.load(inputStream);

		return prop2excl.stringPropertyNames();
	}

	private static void saveHashResultIntoFile(String result, String hashResultPath) throws IOException
	{
		FileOutputStream fop = null;
		File file = null;
		try
		{
			file = new File(hashResultPath);
			fop = new FileOutputStream(file);

			if (!file.exists()) {
				file.createNewFile();
			}
			byte[] contentInBytes = result.getBytes();
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fop != null)
					fop.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
