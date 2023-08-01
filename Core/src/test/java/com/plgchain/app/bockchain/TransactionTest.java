/**
 *
 */
package com.plgchain.app.bockchain;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;
import com.plgchain.app.plingaHelper.util.blockchain.EVMUtil;

/**
 *
 */
public class TransactionTest implements Serializable {

	private static final long serialVersionUID = -110399061749216116L;

	//@Test
	public void doSingleTransaction() {
		System.out.println("GasPrice : " + EVMUtil.DefaultGasPrice);
		String privateKey = "d77b53b57eb48f4e01fe9bb607716da5b3cf566b1347c29fa105deff3cace01d";
		String address = "0xE2675D7a31859027A56A421eadf5D77C2cF5BB36";
		try {
			/*
			 * System.out.println(EVMUtil.createRawTransaction(
			 * "https://rpcurl.mainnet.plgchain.com", privateKey, address, new
			 * BigDecimal("1")).getTransactionHash());
			 */
			System.out.println(EVMUtil.createRawTransaction("http://185.128.137.240:8546", privateKey, address,
					new BigDecimal("1")).getTransactionHash());
			int a = 1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void getNonceTestCase() {
		System.out.println(EVMUtil.getEstimateGasPriceAsWei("http://185.110.191.217:8545"));
	}

	//@Test
	public void doTransferToken() {
		System.out.println("GasPrice : " + EVMUtil.DefaultGasPrice);
		String privateKey = "526442d47453e1fa52db0cf7243cb10dbd58e014b4b223b5245accce83d137b9";
		String address = "0xE2675D7a31859027A56A421eadf5D77C2cF5BB36";
		String contractAddress = "0xDC9E64123b6a801B48e68f69C5594B22C5544862";
		System.out.println(EVMUtil.transferToken("https://rpcurl.mainnet.plgchain.com", privateKey, contractAddress,address,
				new BigDecimal("30000000")));
		int a = 1;
	}

	//@Test
	public void getGasLimit() {
		System.out.println(EVMUtil.getGasLimit("https://rpcurl.mainnet.plgchain.com"));
	}

	//@Test
	public void getBalance() {
		Web3j web3j = Web3j.build(new HttpService("https://rpcurl.mainnet.plgchain.com"));
		try {
			String res = web3j
					.ethGetBalance("0x805743F19dE8836dAF1B63D3414833Daeb180350", DefaultBlockParameterName.LATEST)
					.send().getResult();
			System.out.println("Balance is : " + res);
			int a = 1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
