/**
 *
 */
package com.plgchain.app.plingaHelper.defi.nexi.v1;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import com.plgchain.app.plingaHelper.contracts.defi.nexi.v1.NexiSwapFactory;

/**
 *
 */
public class PairTest implements Serializable {

	private static final long serialVersionUID = 6741990516586119509L;

	public static final String FACTORY_ADDRESS = "0x48ad6A538a394fca92fF7dbA3fED417c9A420010";
	public static final String CASHUSD = "0x30199Be78D0A2A885b3E03f7D5B08DE2ad251648";
	public static final String WNEXI = "0xEC3ceC066E5b2331fCD0Eb7eE5A9B17F617A6efb";
	public static final String USDT = "0x69F6c3e18028012Fbad46A9e940889daF6b4241D";
	public static final String ORBITEX = "0x613d19fd91A62513e16Ecc1c0A4bFb16480bd2Bb";
	private String privKey = "85977658a1bd6e89b79a9e3b7630ed79241f6956a57f0848539205e31c06138e";

	// @Test
	public void getPair() {

	}

	@Test
	public void showPair() {
		LocalDateTime fue = LocalDateTime.now().plusHours(1);
		Web3j web3j = Web3j.build(new HttpService("http://185.128.137.243:18545"));
		// Web3j web3j = Web3j.build(new HttpService("http://10.42.50.7:18545"));
		Credentials credentials = Credentials.create(privKey);
		NexiSwapFactory nexiSwapFactory = NexiSwapFactory.load(FACTORY_ADDRESS, web3j, credentials,
				new DefaultGasProvider());
		// PancakeERC20 token = PancakeERC20.load(WCRYTO, web3j, credentials, new DefaultGasProvider());
		try {
			System.out.println(nexiSwapFactory.getPair(WNEXI, ORBITEX).send());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
