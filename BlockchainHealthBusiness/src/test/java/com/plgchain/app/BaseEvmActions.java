/**
 *
 */
package com.plgchain.app;

import java.io.IOException;
import java.io.Serializable;

import org.junit.jupiter.api.Test;

import com.plgchain.app.plingaHelper.util.blockchain.EVMUtil;

/**
 *
 */
public class BaseEvmActions implements Serializable {

	private static final long serialVersionUID = 2821972374571321960L;

	//@Test
	public void getGasPriceTestCase() {
		try {
			System.out.println(EVMUtil.requestCurrentGasPrice("https://rpcurl.mainnet.plgchain.com").toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
