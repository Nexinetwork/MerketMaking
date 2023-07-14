/**
 *
 */
package com.plgchain.app.plingaHelper;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;

import org.json.JSONException;
import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSON;
import com.plgchain.app.plingaHelper.security.dao.request.SigninRequest;
import com.plgchain.app.plingaHelper.type.request.MarketMakingReq;
import com.plgchain.app.plingaHelper.util.SecurityUtil;
import com.plgchain.app.plingaHelper.util.blockchain.EVMUtil;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

/**
 *
 */
public class WalletUtilTest implements Serializable {

	private static final long serialVersionUID = -1196991786326411525L;

	public String getAuthToken() {
		String jToken = "";
		var auth = SigninRequest.builder().email("info@Plinga.technology").password("MYLoveArash2023plgchainLOGIN")
				.build();
		HttpResponse<String> response = Unirest.post("http://185.173.129.244:7001/api/v1/auth/signin")
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
	public void getTankhahWalletTestCase() {
			// var req = CoinReq.builder().name("Cash USD").symbol("CASHUSD").priceInUsd(new
			// BigDecimal("1")).listed(true).build();
			HttpResponse<String> response = Unirest
					.post("http://185.173.129.244:7001/api/v1/godaction/wallet/getTankhahWalletByPublicKey")
					.header("content-type", "application/json").header("Authorization", getAuthToken())
					// .header("x-api-key", "REPLACE_KEY_VALUE")
					.body("0xa7bed6c080850a7521f6f8852010cf6a176cddec").asString();
			System.out.println("Result is : " + response.getBody());
	}

	//@Test
	public void getNonce() {
		String rpcUrl = "http://185.173.129.243:18545";
		String privateKey = "665a41f51a3b705c8b6cdc85e2b9cbb7baa636c806b9746a99e99966b304baf2";
		System.out.println(EVMUtil.getNonce(rpcUrl,
				privateKey));
	}

}
