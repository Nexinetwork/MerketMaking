/**
 *
 */
package com.plgchain.app.config;

import java.io.Serializable;

import org.json.JSONException;
import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSON;
import com.plgchain.app.plingaHelper.security.dao.request.SigninRequest;
import com.plgchain.app.plingaHelper.util.SecurityUtil;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

/**
 *
 */
public class HealthConfigTest implements Serializable {

	private static final long serialVersionUID = -2410095301994741197L;

	public String getAuthToken() {
		String jToken = "";
		var auth = SigninRequest.builder().email("info@Plinga.technology").password("MYLoveArash2023plgchainLOGIN")
				.build();
		HttpResponse<String> response = Unirest.post("http://185.173.129.83:7001/api/v1/auth/signin")
				.header("content-type", "application/json")
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(JSON.toJSONString(auth)).asString();
		// System.out.println("Result is : " + response.getBody());
		try {
			jToken = SecurityUtil.extractTokenFromJson(response.getBody());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Token : " + jToken);
		return jToken;
	}

	@Test
	public void enableHealchCheck() {
		//var blockchain = "Nexi-DPOS-V1";
		HttpResponse<String> response = Unirest
				.get("http://185.173.129.83:7001/api/v1/godaction/config/enableHealchCheck")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.asString();
		System.out.println("Result is : " + response.getBody());
	}

	//@Test
	public void disableHealchCheck() {
		//var blockchain = "Nexi-DPOS-V1";
		HttpResponse<String> response = Unirest
				.get("http://185.173.129.83:7001/api/v1/godaction/config/disableHealchCheck")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.asString();
		System.out.println("Result is : " + response.getBody());
	}

}
