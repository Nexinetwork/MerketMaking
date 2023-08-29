/**
 *
 */
package com.plgchain.app.plingaHelper.util;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        return i -> CHARACTERS.subSequence(random.nextInt(CHARACTERS.length()), random.nextInt(CHARACTERS.length()) + 1);
    }

	public static String generateRandomString(int length) {
        Random random = new SecureRandom();

        return IntStream.range(0, length)
                .mapToObj(createRandomCharacter(random))
                .collect(Collectors.joining());
    }

}
