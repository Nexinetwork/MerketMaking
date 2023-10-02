package com.plgchain.app.plingaHelper;

import org.json.JSONException;
import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSON;
import com.plgchain.app.plingaHelper.security.dao.request.SigninRequest;
import com.plgchain.app.plingaHelper.type.request.GeneralReq;
import com.plgchain.app.plingaHelper.util.SecurityUtil;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public class ContractTest {

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
	public void getContract() {
		// var req = CoinReq.builder().name("Cash USD").symbol("CASHUSD").priceInUsd(new
		// BigDecimal("1")).listed(true).build();
		var blockchain = "Plinga-DPOS";
		//var blockchain = "Nexi-DPOS-V1";
		// var blockchain = "Nexi-DPOS-V2";
		var req = GeneralReq.builder().blockchain(blockchain)
				.contractAddress("0x8Cd10c064C5118c75f1650F286772694c2E97f57").build();
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.83:7001/api/v1/godaction/contract/getContract")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(JSON.toJSONString(req)).asString();
		System.out.println("Result is : " + response.getBody());
	}

}
