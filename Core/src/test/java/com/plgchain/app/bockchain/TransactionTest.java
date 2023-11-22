/**
 *
 */
package com.plgchain.app.bockchain;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;

import com.plgchain.app.plingaHelper.util.NumberUtil;
import com.plgchain.app.plingaHelper.util.SecurityUtil;
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

	//@Test
	public void getNonceTestCase() {
		try {
			System.out.println(EVMUtil.calculateTransferGasLimit("http://185.173.129.242:18545","0x9032ba5aa0d59888E582E8aa5893b53b07DEceC1","0xacc7EbAd8102442c9EeE774de0E056A8dEF0550E","0x183A3dFadd2D9c702C71b021Db87fe6C34F2b387",new BigInteger("1000000000000000000000")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//@Test
	public void generateRandomTestCase() {
		for (int i=0;i<1000;i++)
			System.out.println(NumberUtil.generateRandomNumber(0, 2, 0));
	}

	// @Test
	public void walletEncryptionTestCase() {
		String privateKey = "0x28f845a6b5d1e45fa15dfef4fabad4866b8abac57e2204e7f87043c227ee1e9b";
		String secretKey = "E*PPX2$<7fE57o0gcNiJw50BoZZvKpKynC^NfrFd38!^7ccWidi61JaUt?F0d9<X?KExRd9@$7DWMdR3<7Y!3*)4jGVDzP4MzSr27Lyf@MbU$Vy_??EeC&!VgaUcmuXp";
		System.out.println(privateKey);
		String encryptedKey = SecurityUtil.encryptString(privateKey, secretKey);
		System.out.println(encryptedKey);
		System.out.println(SecurityUtil.decryptString(encryptedKey, secretKey));
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

	@Test
	public void getGasLimit() {
		System.out.println(EVMUtil.getGasLimit("https://rpc.chainv1.nexi.technology"));
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
