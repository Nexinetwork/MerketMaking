package com.plgchain.app;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import org.json.JSONException;
import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSON;
import com.plgchain.app.plingaHelper.coingecko.type.AssetPlatform;
import com.plgchain.app.plingaHelper.coingecko.type.CoingeckoUtil;
import com.plgchain.app.plingaHelper.constant.BlockchainNodeType;
import com.plgchain.app.plingaHelper.constant.BlockchainTechType;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.entity.BlockchainNode;
import com.plgchain.app.plingaHelper.security.dao.request.SigninRequest;
import com.plgchain.app.plingaHelper.type.request.ContractReq;
import com.plgchain.app.plingaHelper.util.BlockscoutUtil;
import com.plgchain.app.plingaHelper.util.SecurityUtil;
import com.plgchain.app.plingaHelper.util.blockchain.BlockchainUtil;

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
		blockchain.setName("bsc");
		blockchain.setEvm(true);
		blockchain.setMustCheck(false);
		blockchain.setChainId(new BigInteger("56"));
		blockchain.setMainCoin("BNB");
		blockchain.setBlockExplorer("https://bscscan.com");
		blockchain.setBlockchainType(BlockchainTechType.DPOS);
		blockchain.setBlockDuration(2);
		blockchain.setRpcUrl("\"https://bsc.publicnode.com\"");
		blockchain.setHeight(new BigInteger("0"));
		blockchain.setCoingeckoId("binance-smart-chain");
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.244:7001/api/v1/godaction/blockchain/createNewBlockchain")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(JSON.toJSONString(blockchain)).asString();
		System.out.println("Result is : " + response.getBody());
	}

	// @Test
	public void createNodeTestCase() {
		var node = BlockchainNode.builder().blockchainId(Long.valueOf(1)).enabled(true).validator(true)
				.serverIp("185.128.137.241").sshPort(22).rpcUrl("http://www.plgscan.com")
				.nodeType(BlockchainNodeType.BLOCKSCOUT).validator(false).serviceNeme("plgscan.service").mustCheck(true)
				.build();
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.244:7001/api/v1/godaction/blockchain/createNewNode")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(JSON.toJSONString(node)).asString();
		System.out.println("Result is : " + response.getBody());
	}

	// @Test
	public void getBlockscoutResult() {
		System.out.println("Result is : " + BlockscoutUtil.getLatestBlock("https://www.plgscan.com"));
	}

	// @Test
	public void LastBlockTest() {
		System.out.println(BlockchainUtil.getLatestBlockNumber("https://bsc.publicnode.com"));
	}

	// @Test
	public void getCoingeckoNetworks() {
		var res = CoingeckoUtil.runGetCommand("https://api.coingecko.com/api/v3/asset_platforms");
		List<AssetPlatform> lst = JSON.parseArray(res, AssetPlatform.class);
		System.out.println(lst);
	}

	@Test
	public void generateSmartContract() {
		var contract = ContractReq.builder().blockchainCoingeckoId("plinga").coinCoingeckoId("tether")
				.contract("0xbD07cf23A43f13078716A015F3Cc27F7a1661e65").decimal(18).mustAdd(true).mustCheck(true)
				.isMain(false).build();
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.244:7001/api/v1/godaction/token/createNewContract")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(JSON.toJSONString(contract)).asString();
		System.out.println("Result is : " + response.getBody());
	}

}
