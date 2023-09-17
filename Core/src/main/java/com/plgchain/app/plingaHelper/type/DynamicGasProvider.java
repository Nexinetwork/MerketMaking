/**
 *
 */
package com.plgchain.app.plingaHelper.type;

import java.math.BigInteger;

import org.web3j.tx.gas.StaticGasProvider;

/**
 *
 */
public class DynamicGasProvider extends StaticGasProvider {

	public DynamicGasProvider(BigInteger gasPrice, BigInteger gasLimit) {
		super(gasPrice, gasLimit);
		// TODO Auto-generated constructor stub
	}

}
