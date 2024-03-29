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
import com.plgchain.app.plingaHelper.constant.TransactionParallelType;
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

	//@Test
	public void createBlockchaintestCase() {
		Blockchain blockchain = new Blockchain();
		blockchain.setName("Nexi-DPOS-V2");
		blockchain.setEvm(true);
		blockchain.setMustCheck(true);
		blockchain.setChainId(new BigInteger("4243"));
		blockchain.setMainCoin("NexiV2");
		blockchain.setBlockExplorer("https://ww.nexiscan.com");
		blockchain.setBlockchainType(BlockchainTechType.DPOS);
		blockchain.setBlockDuration(2);
		blockchain.setRpcUrl("http://10.20.7.1:28545");
		blockchain.setHeight(new BigInteger("0"));
		blockchain.setCoingeckoId("nexiV2");
		blockchain.setFullName("Nexi-DPOS-V2");
		blockchain.setAutoGas(true);
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.83:7001/api/v1/godaction/blockchain/createNewBlockchain")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(JSON.toJSONString(blockchain)).asString();
		System.out.println("Result is : " + response.getBody());
	}

	// @Test
	public void createNodeTestCase() {
		var node = BlockchainNodeDto.builder().blockchain("Plinga-DPOS").serverIp("82.115.26.181").sshPort(22)
				.rpcUrl("http://82.115.26.181:8545").enabled(true).mmNode(false)
				.nodeType(BlockchainNodeType.BLOCKCHAINNODE).validator(true).serviceNeme("plgchain3.service")
				.mustCheck(true).build();
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.83:7001/api/v1/godaction/blockchain/createNewNode")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(JSON.toJSONString(node)).asString();
		System.out.println("Result is : " + response.getBody());
	}

	// @Test
	public void deleteAllBlockchainNodesTestCase() {
		var blockchain = "Nexilix-Pos-V1";
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.83:7001/api/v1/godaction/blockchain/deleteAllBlockchainNode")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(blockchain).asString();
		System.out.println("Result is : " + response.getBody());
	}

	// @Test
	public void setDomainAsMustCheckTestCase() {
		var conName = "ripple";
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.83:7001/api/v1/godaction/coin/setCoinAsMustCheckByCoingeckoId")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(conName).asString();
		System.out.println("Result is : " + response.getBody());
	}

	// @Test
	public void setDomainAsMustNotCheckTestCase() {
		var conName = "binance-peg-cardano";
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.83:7001/api/v1/godaction/coin/setCoinAsMustNotCheckByCoingeckoId")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(conName).asString();
		System.out.println("Result is : " + response.getBody());
	}

	//@Test
	public void createNewCoinTestCase() {
		// var req = CoinReq.builder().name("Cash USD").symbol("CASHUSD").priceInUsd(new
		// BigDecimal("1")).listed(true).build();
		var req = CoinReq.builder().name("Lamborghini").symbol("LMBR").listed(true).build();
		HttpResponse<String> response = Unirest.post("http://185.173.129.83:7001/api/v1/godaction/coin/createNewCoin")
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
		// var blockchain = "Plinga-DPOS";
		var blockchain = "Nexi-DPOS-V1";
		// var blockchain = "Nexi-DPOS-V2";
		var contract = ContractReq.builder().blockchainCoingeckoId(blockchain).coinCoingeckoId("orbitex")
				.contract("0xA60e7e82560165a150F05e75F59bb8499D76AE12").decimal(18).mustAdd(true).mustCheck(true)
				.isMain(false).build();
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.83:7001/api/v1/godaction/token/createNewContract")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(JSON.toJSONString(contract)).asString();
		System.out.println("Result is : " + response.getBody());
	}

	//@Test
	public void createNewSmartContract() {
		// var req = CoinReq.builder().name("Cash USD").symbol("CASHUSD").priceInUsd(new
		// BigDecimal("1")).listed(true).build();
		//var blockchain = "Plinga-DPOS";
		//var blockchain = "Nexi-DPOS-V1";
		var blockchain = "Nexi-DPOS-V2";
		var req = SmartContractReq.builder().blockchain(blockchain).coinId(10145)
				.contractsAddress("0x48FDd05F8C5e9F46A6860dDc13337948141ea268").decimal(18).isMain(true)
				.marketMaking(true).mustAdd(true).mustCheck(true).build();
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.83:7001/api/v1/godaction/contract/createNewSmartContract")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(JSON.toJSONString(req)).asString();
		System.out.println("Result is : " + response.getBody());
	}

	//@Test
	public void findMarketmakingByBlockchainAndContractAddress() {
		// var req = CoinReq.builder().name("Cash USD").symbol("CASHUSD").priceInUsd(new
		// BigDecimal("1")).listed(true).build();
		var blockchain = "Nexi-DPOS-V1";
		//var blockchain = "Plinga-DPOS";
		// var blockchain = "Nexi-DPOS-V2";
		var req = SmartContractReq.builder().blockchain(blockchain)
				.contractsAddress("0xC8be0e4CE4d6315001Cc8dC21737e1f8583acd47").build();
		HttpResponse<String> response = Unirest.post(
				"http://185.173.129.83:7001/api/v1/godaction/marketMaking/findMarketmakingByBlockchainAndContractAddress")
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
				.get("http://185.173.129.83:7001/api/v1/godaction/contract/getTankhahWalletListAsResult")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.asString();
		System.out.println("Result is : " + response.getBody());
	}

	//@Test
	public void createMarketMaking() {
		// var req = CoinReq.builder().name("Cash USD").symbol("CASHUSD").priceInUsd(new
		// BigDecimal("1")).listed(true).build();
		var req = MarketMakingReq.builder().currentTransferWalletCount(0).dailyAddWallet(1000).initialDecimal(2)
				.initialWallet(47619).initialWalletCreationDone(false).initialWalletFundingDone(false)
				.minInitial(new BigDecimal(1.5)).maxInitial(new BigDecimal(20)).smartContractId(12167)
				.maxDefiInitial(new BigDecimal(20000)).minDefiInitial(new BigDecimal(10000)).mustUpdateMongoDefi(true).mustUpdateMongoTransfer(true)
				.transactionParallelType(TransactionParallelType.SYNC).build();
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.83:7001/api/v1/godaction/marketMaking/createOrUpdateMarketMaking")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(JSON.toJSONString(req)).asString();
		System.out.println("Result is : " + response.getBody());
	}

	@Test
	public void findContractsByContractAddress() {
		// var req = CoinReq.builder().name("Cash USD").symbol("CASHUSD").priceInUsd(new
		// BigDecimal("1")).listed(true).build();
		String contract = "0xdF397Aeee4950Aafb7DaD6345747337B510B4951";
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.83:7001/api/v1/godaction/contract/findContractsByContractAddress")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(contract).asString();
		System.out.println("Result is : " + response.getBody());
	}

	//@Test
	public void correctWalletTestCase() {
		// var req = CoinReq.builder().name("Cash USD").symbol("CASHUSD").priceInUsd(new
		// BigDecimal("1")).listed(true).build();
		HttpResponse<String> response = Unirest
				.get("http://185.173.129.83:7001/api/v1/godaction/wallet/fixWalletPrivatekeys")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.asString();
		System.out.println("Result is : " + response.getBody());
	}

	// @Test
	public void stopAllNodesOfBlockchain() {
		var blockchain = "Plinga-DPOS";
		// var blockchain = "Nexi-DPOS-V1";
		// var blockchain = "Nexi-DPOS-V2";
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.83:7001/api/v1/godaction/blockchain/stopAllNodesOfBlockchain")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(blockchain).asString();
		System.out.println("Result is : " + response.getBody());
	}

	// @Test
	public void startAllNodesOfBlockchain() {
		// var blockchain = "Nexi-DPOS-V1";
		var blockchain = "Plinga-DPOS";
		// var blockchain = "Nexi-DPOS-V2";
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.83:7001/api/v1/godaction/blockchain/startAllNodesOfBlockchain")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(blockchain).asString();
		System.out.println("Result is : " + response.getBody());
	}

	// @Test
	public void restartAllNodesOfBlockchain() {
		var blockchain = "Plinga-DPOS";
		// var blockchain = "Nexi-DPOS-V1";
		// var blockchain = "Nexi-DPOS-V2";
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.83:7001/api/v1/godaction/blockchain/restartAllNodesOfBlockchain")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(blockchain).asString();
		System.out.println("Result is : " + response.getBody());
	}

}
