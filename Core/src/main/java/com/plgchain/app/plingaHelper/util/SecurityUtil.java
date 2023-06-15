/**
 *
 */
package com.plgchain.app.plingaHelper.util;

import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class SecurityUtil implements Serializable {

	private static final long serialVersionUID = -1940028435370418123L;

	public static String extractTokenFromJson(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONObject dataObject = jsonObject.getJSONObject("data");
        String token = dataObject.getString("token");
        return "Bearer " + token;
    }

}
