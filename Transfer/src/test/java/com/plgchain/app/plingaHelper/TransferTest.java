/**
 *
 */
package com.plgchain.app.plingaHelper;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.web3j.protocol.core.methods.response.EthSendTransaction;

import com.plgchain.app.plingaHelper.util.blockchain.EVMUtil;
import com.plgchain.app.plingaHelper.util.blockchain.EvmWalletUtil;

/**
 *
 */
public class TransferTest implements Serializable {

	private static final long serialVersionUID = 3966991804414789715L;

	public void transferBetweenToAccount(String rpcUrl, String privateKey, String from, String to, BigDecimal amount,
			BigInteger gasPrice, BigInteger gasLimit, BigInteger nonce) {
		EthSendTransaction result = null;
		BigInteger[] finalNonce = { nonce };
		BigInteger[] finalGasPrice = { gasPrice };
		BigInteger[] finalGasLimit = { gasLimit };
		boolean [] shouldBreak = {false};
		System.out.println(String.format(
				"ُTry to transfer %s Maincoin from %s/%s to %s and with nonce %s with gasPrice %s with rpcurl %s",
				amount, from, privateKey, to, finalNonce[0].toString(), finalGasPrice[0].toString(), rpcUrl));
		while (!shouldBreak[0]) {
			try {
				result = EVMUtil.createRawTransactionSync(rpcUrl, privateKey, to, amount, finalNonce[0],
						finalGasPrice[0], finalGasLimit[0]);

				Optional.ofNullable(result).filter(r -> !r.hasError())
						.filter(r -> r.getTransactionHash() != null && !r.getTransactionHash().isBlank())
						.ifPresent(r -> {
							System.out.println(String.format(
									"Transfered %s Maincoin from %s to %s and txHash is %s with nonce %s with gasPrice %s and gaslimit %s",
									amount, from, to, r.getTransactionHash(), finalNonce[0].toString(),
									finalGasPrice[0].toString(), finalGasLimit[0].toString()));
							shouldBreak[0] = true;
						});

				if (result != null && EVMUtil.mostIncreaseNonce(result)) {
					finalNonce[0] = finalNonce[0].add(BigInteger.ONE);
				} else {
					shouldBreak[0] = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				shouldBreak[0] = true;
			}
		}
	}

	@Test
	public void transferTestCase() {
		try {
			System.out.println(EVMUtil.getNonce("http://185.128.137.240:8545",
						"d77b53b57eb48f4e01fe9bb607716da5b3cf566b1347c29fa105deff3cace01d"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final BigInteger[] tankhahNonce = { BigInteger.ZERO };
		try {
			tankhahNonce[0] = EVMUtil.getNonce("http://185.128.137.240:8545",
					"d77b53b57eb48f4e01fe9bb607716da5b3cf566b1347c29fa105deff3cace01d");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Current none of tankhah wallet is : " + tankhahNonce[0].toString());
		var gasPrice = new BigInteger("110000000000");
		try {
			System.out.println("Current gas price : " + EVMUtil.requestCurrentGasPrice("http://185.173.129.244:8545"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Estimate gas price : " + EVMUtil.getEstimateGas("http://185.173.129.244:8545"));
		for (int i = 0; i < 1; i++) {
			transferBetweenToAccount("http://185.128.137.240:8545",
					"d77b53b57eb48f4e01fe9bb607716da5b3cf566b1347c29fa105deff3cace01d",
					"e6934f80c7390f3952e0f03bf43583cf9d57d4a1", "0x65B84D90CaF1Eb50888504F7Eb19B5a77BE9890f",
					new BigDecimal("1"), EVMUtil.DefaultGasPrice,
					new BigInteger("12500000"), tankhahNonce[0]);
			tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
		}

		// System.out.println(EvmWalletUtil.generateWallet(new
		// BigInteger("46295382012785272375870482308001729876667835349322542732133821350485638298354")));
	}

}
