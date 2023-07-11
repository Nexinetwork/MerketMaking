package com.plgchain.app;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;

import org.json.JSONException;
import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSON;
import com.plgchain.app.plingaHelper.coingecko.type.AssetPlatform;
import com.plgchain.app.plingaHelper.constant.BlockchainNodeType;
import com.plgchain.app.plingaHelper.constant.BlockchainTechType;
import com.plgchain.app.plingaHelper.dto.BlockchainNodeDto;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.security.dao.request.SigninRequest;
import com.plgchain.app.plingaHelper.type.request.CoinReq;
import com.plgchain.app.plingaHelper.type.request.ContractReq;
import com.plgchain.app.plingaHelper.type.request.MarketMakingReq;
import com.plgchain.app.plingaHelper.type.request.SmartContractReq;
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
		blockchain.setName("Nexilix-Pos-V1");
		blockchain.setEvm(true);
		blockchain.setMustCheck(true);
		blockchain.setChainId(new BigInteger("240"));
		blockchain.setMainCoin("NEXILIX");
		blockchain.setBlockExplorer("https://scan.nexilix.com");
		blockchain.setBlockchainType(BlockchainTechType.POS);
		blockchain.setBlockDuration(2);
		blockchain.setRpcUrl("https://rpcurl.pos.nexilix.com/");
		blockchain.setHeight(new BigInteger("0"));
		blockchain.setCoingeckoId("nexilix");
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.244:7001/api/v1/godaction/blockchain/createNewBlockchain")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(JSON.toJSONString(blockchain)).asString();
		System.out.println("Result is : " + response.getBody());
	}

	// @Test
	public void createNodeTestCase() {
		var node = BlockchainNodeDto.builder().blockchain("Nexilix-Pos-V1").enabled(true).serverIp("185.173.129.244")
				.sshPort(22424).rpcUrl("http://185.173.129.244:8545").enabled(true)
				.nodeType(BlockchainNodeType.BLOCKCHAINNODE).validator(true).serviceNeme("plgchain1.service")
				.mustCheck(true).build();
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.244:7001/api/v1/godaction/blockchain/createNewNode")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(JSON.toJSONString(node)).asString();
		System.out.println("Result is : " + response.getBody());
	}

	// @Test
	public void setDomainAsMustCheckTestCase() {
		var conName = "ripple";
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.244:7001/api/v1/godaction/coin/setCoinAsMustCheckByCoingeckoId")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(conName).asString();
		System.out.println("Result is : " + response.getBody());
	}

	// @Test
	public void setDomainAsMustNotCheckTestCase() {
		var conName = "binance-peg-cardano";
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.244:7001/api/v1/godaction/coin/setCoinAsMustNotCheckByCoingeckoId")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(conName).asString();
		System.out.println("Result is : " + response.getBody());
	}

	//@Test
	public void createNewCoinTestCase() {
		// var req = CoinReq.builder().name("Cash USD").symbol("CASHUSD").priceInUsd(new
		// BigDecimal("1")).listed(true).build();
		var req = CoinReq.builder().name("ZEROX").symbol("ZEROX").listed(true).build();
		HttpResponse<String> response = Unirest.post("http://185.173.129.244:7001/api/v1/godaction/coin/createNewCoin")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(JSON.toJSONString(req)).asString();
		System.out.println("Result is : " + response.getBody());
	}

	// @Test
	public void getBlockscoutResult() {
		System.out.println("Result is : " + BlockscoutUtil.getLatestBlock("https://www.plgscan.com"));
	}

	// @Test
	public void LastBlockTest() {
		HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
		System.out.println(BlockchainUtil.getLatestBlockNumber(httpClient, "http://185.173.129.80:8545"));
	}

	// @Test
	public void getCoingeckoNetworks() {
		String res = "";
		try {
			// res =
			// CoingeckoUtil.runGetCommand("https://api.coingecko.com/api/v3/asset_platforms");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<AssetPlatform> lst = JSON.parseArray(res, AssetPlatform.class);
		System.out.println(lst);
	}

	// @Test
	public void generateSmartContract() {
		var contract = ContractReq.builder().blockchainCoingeckoId("nexi").coinCoingeckoId("tether")
				.contract("0xA60e7e82560165a150F05e75F59bb8499D76AE12").decimal(18).mustAdd(true).mustCheck(true)
				.isMain(false).build();
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.244:7001/api/v1/godaction/token/createNewContract")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(JSON.toJSONString(contract)).asString();
		System.out.println("Result is : " + response.getBody());
	}

	//@Test
	public void createNewSmartContract() {
		// var req = CoinReq.builder().name("Cash USD").symbol("CASHUSD").priceInUsd(new
		// BigDecimal("1")).listed(true).build();
		var req = SmartContractReq.builder().blockchain("Nexilix-Pos-V1").coinId(10137)
				.contractsAddress("0x30199Be78D0A2A885b3E03f7D5B08DE2ad251648").decimal(18).isMain(true)
				.marketMaking(true).mustAdd(true).mustCheck(true).build();
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.244:7001/api/v1/godaction/contract/createNewSmartContract")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(JSON.toJSONString(req)).asString();
		System.out.println("Result is : " + response.getBody());
	}

	//@Test
	public void getTankhahWallets() {
		// var req = CoinReq.builder().name("Cash USD").symbol("CASHUSD").priceInUsd(new
		// BigDecimal("1")).listed(true).build();
		HttpResponse<String> response = Unirest
				.get("http://185.173.129.244:7001/api/v1/godaction/contract/getTankhahWalletListAsResult")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.asString();
		System.out.println("Result is : " + response.getBody());
	}

	@Test
	public void createMarketMaking() {
		// var req = CoinReq.builder().name("Cash USD").symbol("CASHUSD").priceInUsd(new
		// BigDecimal("1")).listed(true).build();
		var req = MarketMakingReq.builder().currentTransferWalletCount(0).dailyAddWallet(1000).initialDecimal(2).initialWallet(70000).initialWalletCreationDone(false)
				.initialWalletFundingDone(false).minInitial(new BigDecimal(100)).maxInitial(new BigDecimal(1000)).smartContractId(12127).build();
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.244:7001/api/v1/godaction/marketMaking/createOrUpdateMarketMaking")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(JSON.toJSONString(req)).asString();
		System.out.println("Result is : " + response.getBody());
	}

}
