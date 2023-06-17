package com.plgchain.app;

import java.io.Serializable;
import java.math.BigInteger;

import org.json.JSONException;
import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSON;
import com.plgchain.app.plingaHelper.constant.BlockchainNodeType;
import com.plgchain.app.plingaHelper.constant.BlockchainTechType;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.entity.BlockchainNode;
import com.plgchain.app.plingaHelper.security.dao.request.SigninRequest;
import com.plgchain.app.plingaHelper.util.SecurityUtil;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public class BlockchainActionTest implements Serializable {

	private static final long serialVersionUID = -7901677077317335292L;

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

	// @Test
	public void createBlockchaintestCase() {
		Blockchain blockchain = new Blockchain();
		blockchain.setName("Plinga");
		blockchain.setEvm(true);
		blockchain.setMustCheck(true);
		blockchain.setChainId(new BigInteger("242"));
		blockchain.setMainCoin("PLINGA");
		blockchain.setBlockExplorer("https://www.plgscan.com");
		blockchain.setBlockchainType(BlockchainTechType.DPOS);
		blockchain.setBlockDuration(2);
		blockchain.setHeight(new BigInteger("0"));
		HttpResponse<String> response = Unirest.post("http://185.173.129.244:7001/api/v1/godaction/createNewBlockchain")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(JSON.toJSONString(blockchain)).asString();
		System.out.println("Result is : " + response.getBody());
	}

	@Test
	public void createNodeTestCase() {
		var node = BlockchainNode.builder().blockchainId(Long.valueOf(1)).enabled(true).validator(true)
				.serverIp("185.128.137.240").sshPort(22).rpcUrl("http://185.128.137.240:8545").nodeType(BlockchainNodeType.BLOCKCHAINNODE)
				.serviceNeme("plgchain1.service").mustCheck(true).build();
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.244:7001/api/v1/godaction/blockchain/createNewNode")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(JSON.toJSONString(node)).asString();
		System.out.println("Result is : " + response.getBody());
	}

}
