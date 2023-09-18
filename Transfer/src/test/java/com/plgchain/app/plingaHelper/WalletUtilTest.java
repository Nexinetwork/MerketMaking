/**
 *
 */
package com.plgchain.app.plingaHelper;

import java.io.Serializable;
import java.math.BigDecimal;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import com.alibaba.fastjson2.JSON;
import com.plgchain.app.plingaHelper.security.dao.request.SigninRequest;
import com.plgchain.app.plingaHelper.type.request.GeneralReq;
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

	// @Test
	public void getTankhahWalletByContractAddress() {
		// var req = CoinReq.builder().name("Cash USD").symbol("CASHUSD").priceInUsd(new
		// BigDecimal("1")).listed(true).build();
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.83:7001/api/v1/godaction/wallet/getTankhahWalletByContractAddress")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body("0x883277f7D623612034db92A2dC16A8BEC20a8FB5").asString();
		System.out.println("Result is : " + response.getBody());
	}

	// @Test
	public void getTankhahWalletByPublicKey() {
		// var req = CoinReq.builder().name("Cash USD").symbol("CASHUSD").priceInUsd(new
		// BigDecimal("1")).listed(true).build();
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.83:7001/api/v1/godaction/wallet/getTankhahWalletByPublicKey")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body("0x57277e39b0bf24a2bc3dd7aae78c2be45fc6dab8").asString();
		System.out.println("Result is : " + response.getBody());
	}

	// @Test
	public void getNonce() {
		String rpcUrl = "http://185.173.129.243:18545";
		String privateKey = "665a41f51a3b705c8b6cdc85e2b9cbb7baa636c806b9746a99e99966b304baf2";
		System.out.println(EVMUtil.getNonceByPrivateKey(rpcUrl, privateKey));
	}

	// @Test
	public void getTokenBalanceTestCase() {
		String rpcUrl = "http://185.173.129.83:8545";
		String privateKey = "a56c859dbf1b3911e2e3368302eb258e0ab4e34ee2152594bf9688d5522a9cbc";
		String contractAddress = "0xE61D3f41E12f7De653C68777A791A883f151f103";
		BigDecimal balance = EVMUtil.getTokenBalancSync(rpcUrl, privateKey, contractAddress);
		System.out.println("balance : " + balance.toString());
	}

	// @Test
	public void correctMetamaskTransWalletsFunding() {
		// var req = CoinReq.builder().name("Cash USD").symbol("CASHUSD").priceInUsd(new
		// BigDecimal("1")).listed(true).build();
		var blockchain = "Nexi-DPOS-V1";
		// var blockchain = "Nexi-DPOS-V2";
		var req = GeneralReq.builder().blockchain(blockchain)
				.contractAddress("0x883277f7D623612034db92A2dC16A8BEC20a8FB5").int1(0).build();
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.83:7001/api/v1/godaction/wallet/correctMetamaskTransWalletsFunding")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(JSON.toJSONString(req)).asString();
		System.out.println("Result is : " + response.getBody());
	}

	// @Test
	public void correctMetamaskTransWalletsFundingReverse() {
		// var req = CoinReq.builder().name("Cash USD").symbol("CASHUSD").priceInUsd(new
		// BigDecimal("1")).listed(true).build();
		// var blockchain = "Plinga-DPOS";
		var blockchain = "Nexi-DPOS-V1";
		// var blockchain = "Nexi-DPOS-V2";
		var req = GeneralReq.builder().blockchain(blockchain)
				.contractAddress("0x883277f7D623612034db92A2dC16A8BEC20a8FB5").int1(0).build();
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.83:7001/api/v1/godaction/wallet/correctMetamaskTransWalletsFundingReverse")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(JSON.toJSONString(req)).asString();
		System.out.println("Result is : " + response.getBody());
	}

	// @Test
	public void updateContractAddress() {
		// var req = CoinReq.builder().name("Cash USD").symbol("CASHUSD").priceInUsd(new
		// BigDecimal("1")).listed(true).build();
		// var blockchain = "Plinga-DPOS";
		var blockchain = "Nexi-DPOS-V1";
		// var blockchain = "Nexi-DPOS-V2";
		var req = GeneralReq.builder().blockchain(blockchain)
				.contractAddress("0x30199Be78D0A2A885b3E03f7D5B08DE2ad251648")
				.str1("0x40Aa6A2463fBAabEA6DB995aaB604C2393cbc37D").build();
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.83:7001/api/v1/godaction/contract/updateContractAddress")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(JSON.toJSONString(req)).asString();
		System.out.println("Result is : " + response.getBody());
	}

	// @Test
	public void backAllTokensToTankhah() {
		// var req = CoinReq.builder().name("Cash USD").symbol("CASHUSD").priceInUsd(new
		// BigDecimal("1")).listed(true).build();
		var req = GeneralReq.builder().blockchain("Nexi-DPOS-V2")
				.contractAddress("0x30276EE9e302564Cf2aE525524AE9e3D469c7839").int1(0).build();
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.83:7001/api/v1/godaction/wallet/backAllTokensToTankhah")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(JSON.toJSONString(req)).asString();
		System.out.println("Result is : " + response.getBody());
	}

	// @Test
	public void backAllTokensToTankhahReverse() {
		// var req = CoinReq.builder().name("Cash USD").symbol("CASHUSD").priceInUsd(new
		// BigDecimal("1")).listed(true).build();
		var req = GeneralReq.builder().long1(12140L).int1(0).build();
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.83:7001/api/v1/godaction/wallet/backAllTokensToTankhahReverse")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(JSON.toJSONString(req)).asString();
		System.out.println("Result is : " + response.getBody());
	}

	// @Test
	public void updateAllwalletsBalancesByContractId() {
		// var req = CoinReq.builder().name("Cash USD").symbol("CASHUSD").priceInUsd(new
		// BigDecimal("1")).listed(true).build();
		var req = GeneralReq.builder().long1(12139L).build();
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.83:7001/api/v1/godaction/wallet/updateAllwalletsBalancesByContractId")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(JSON.toJSONString(req)).asString();
		System.out.println("Result is : " + response.getBody());
	}

	// @Test
	public void updateAllwalletsBalancesByContractIdParallel() {
		// var req = CoinReq.builder().name("Cash USD").symbol("CASHUSD").priceInUsd(new
		// BigDecimal("1")).listed(true).build();
		var req = GeneralReq.builder().long1(12139L).build();
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.83:7001/api/v1/godaction/wallet/updateAllwalletsBalancesByContractIdParallel")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(JSON.toJSONString(req)).asString();
		System.out.println("Result is : " + response.getBody());
	}

	// @Test
	public void backAllTokensToTankhahParallel() {
		// var req = CoinReq.builder().name("Cash USD").symbol("CASHUSD").priceInUsd(new
		// BigDecimal("1")).listed(true).build();
		var req = GeneralReq.builder().contractId(12140L).int1(0).build();
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.83:7001/api/v1/godaction/wallet/backAllTokensToTankhahParallel")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(JSON.toJSONString(req)).asString();
		System.out.println("Result is : " + response.getBody());
	}

	// @Test
	public void creditMinimumMainCoinForTokenWallets() {
		// var req = CoinReq.builder().name("Cash USD").symbol("CASHUSD").priceInUsd(new
		// BigDecimal("1")).listed(true).build();
		var req = GeneralReq.builder().contractId(12140L).int1(0).build();
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.83:7001/api/v1/godaction/wallet/creditMinimumMainCoinForTokenWallets")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(JSON.toJSONString(req)).asString();
		System.out.println("Result is : " + response.getBody());
	}

	// @Test
	public void creditMinimumMainCoinForTokenWalletsReverse() {
		// var req = CoinReq.builder().name("Cash USD").symbol("CASHUSD").priceInUsd(new
		// BigDecimal("1")).listed(true).build();
		var req = GeneralReq.builder().long1(12140L).int1(0).build();
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.83:7001/api/v1/godaction/wallet/creditMinimumMainCoinForTokenWalletsReverse")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(JSON.toJSONString(req)).asString();
		System.out.println("Result is : " + response.getBody());
	}

	// @Test
	public void backAllTokensFromTempTankhahToTankhah() {
		// var req = CoinReq.builder().name("Cash USD").symbol("CASHUSD").priceInUsd(new
		// BigDecimal("1")).listed(true).build();
		int a = 1;
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.83:7001/api/v1/godaction/wallet/backAllTokensFromTempTankhahToTankhah")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body("12139").asString();
		System.out.println("Result is : " + response.getBody());
	}

	// @Test
	public void deleteTempTankhahWallet() {
		// var req = CoinReq.builder().name("Cash USD").symbol("CASHUSD").priceInUsd(new
		// BigDecimal("1")).listed(true).build();
		int a = 1;
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.83:7001/api/v1/godaction/wallet/deleteTempTankhahWallet")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body("12150").asString();
		System.out.println("Result is : " + response.getBody());
	}

	// @Test
	public void fixPrivateKeyByContract() {
		// var req = CoinReq.builder().name("Cash USD").symbol("CASHUSD").priceInUsd(new
		// BigDecimal("1")).listed(true).build();
		// var blockchain = "Plinga-DPOS";
		var blockchain = "Nexi-DPOS-V1";
		// var blockchain = "Nexi-DPOS-V2";
		var req = GeneralReq.builder().blockchain(blockchain)
				.contractAddress("0x883277f7D623612034db92A2dC16A8BEC20a8FB5").build();
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.83:7001/api/v1/godaction/wallet/fixPrivateKeyByContract")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(JSON.toJSONString(req)).asString();
		System.out.println("Result is : " + response.getBody());
	}

	@Test
	public void approveContractForTokenContract() {
		// var req = CoinReq.builder().name("Cash USD").symbol("CASHUSD").priceInUsd(new
		// BigDecimal("1")).listed(true).build();
		// var blockchain = "Plinga-DPOS";
		var blockchain = "Nexi-DPOS-V1";
		// var blockchain = "Nexi-DPOS-V2";
		var req = GeneralReq.builder().blockchain(blockchain)
				.contractAddress("0x0000000000000000000000000000000000001010")
				.str1("0x883277f7D623612034db92A2dC16A8BEC20a8FB5").build();
		HttpResponse<String> response = Unirest
				.post("http://185.173.129.83:7001/api/v1/godaction/contract/approveContractForTokenContract")
				.header("content-type", "application/json").header("Authorization", getAuthToken())
				// .header("x-api-key", "REPLACE_KEY_VALUE")
				.body(JSON.toJSONString(req)).asString();
		System.out.println("Result is : " + response.getBody());
	}

	//@Test
	public void testApprove() {
		TransactionReceipt tr = EVMUtil.approveContractOnWallet("http://185.173.129.243:18545",
				"a22af9370cd5798a1e2a8bea828014d35832666595bcf9b0ddd6e8a58e343900", "0xeef57E3356ef56B6b79F7449171DE0394fFA6d55", "0xEC3ceC066E5b2331fCD0Eb7eE5A9B17F617A6efb");
	}

}
