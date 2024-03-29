/**
 *
 */
package com.plgchain.app.plingaHelper.util;

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class SecurityUtil implements Serializable {

	private static final long serialVersionUID = -1940028435370418123L;

	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_-+=<>?";

	public static String extractTokenFromJson(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONObject dataObject = jsonObject.getJSONObject("data");
        String token = dataObject.getString("token");
        return "Bearer " + token;
    }

	private static IntFunction<CharSequence> createRandomCharacter(Random random) {
	    return i -> CHARACTERS.substring(random.nextInt(CHARACTERS.length()), random.nextInt(CHARACTERS.length()) + 1);
	}

	public static String generateRandomString(int length) {
	    Random random = new SecureRandom();

	    return IntStream.range(0, length)
	            .mapToObj(i -> CHARACTERS.charAt(random.nextInt(CHARACTERS.length())))
	            .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
	            .toString();
	}


	public static String encryptString(String str, String encryptionKeyString) {
	    try {
	        byte[] encryptionKeyBytes = Arrays.copyOf(encryptionKeyString.getBytes(), 16);
	        SecretKey secretKey = new SecretKeySpec(encryptionKeyBytes, "AES");
	        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
	        byte[] encryptedBytes = cipher.doFinal(str.getBytes());
	        String encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);
	        return encryptedText;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}


	public static String decryptString(String encryptedText, String encryptionKeyString) {
	    try {
	        byte[] encryptionKeyBytes = Arrays.copyOf(encryptionKeyString.getBytes(), 16);
	        SecretKey secretKey = new SecretKeySpec(encryptionKeyBytes, "AES");
	        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	        cipher.init(Cipher.DECRYPT_MODE, secretKey);
	        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
	        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
	        String decryptedText = new String(decryptedBytes);
	        return decryptedText;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}

}
