/**
 *
 */
package com.plgchain.app.plingaHelper;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.tx.gas.DefaultGasProvider;

import com.plgchain.app.plingaHelper.util.blockchain.EVMUtil;

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
		boolean[] shouldBreak = { false };
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

	public void transferBetweenToAccount(String rpcUrl, String privateKey, String from, String to,String contract, BigDecimal amount,
			BigInteger gasPrice, BigInteger gasLimit, BigInteger nonce) {
		EthSendTransaction result = null;
		BigInteger[] finalNonce = { nonce };
		BigInteger[] finalGasPrice = { gasPrice };
		BigInteger[] finalGasLimit = { gasLimit };
		boolean [] shouldBreak = {false};
		while (!shouldBreak[0]) {
			try {
				result = EVMUtil.sendSmartContractTransactionSync(rpcUrl, privateKey, contract,to, amount, finalNonce[0],
						finalGasPrice[0], finalGasLimit[0]);

				Optional.ofNullable(result).filter(r -> !r.hasError())
						.filter(r -> r.getTransactionHash() != null && !r.getTransactionHash().isBlank())
						.ifPresent(r -> {
							System.out.println(String.format(
									"Transfered %s token %s from %s to %s and txHash is %s with nonce %s with gasPrice %s and gaslimit %s",
									amount, contract,from, to, r.getTransactionHash(), finalNonce[0].toString(),
									finalGasPrice[0].toString(), finalGasLimit[0].toString()));
							shouldBreak[0] = true;
						});
				if (result != null) {
					if (EVMUtil.mostIncreaseNonce(result))
						finalNonce[0] = finalNonce[0].add(BigInteger.ONE);
					else {
						System.out.println(String.format("message is %s and Error is %s but try again.",result.getResult(), result.getError().getMessage()));
						if (result.getError().getMessage().contains("insufficient funds for gas")) {
							shouldBreak[0] = true;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				shouldBreak[0] = true;
			}
		}
	}

	//@Test
	public void transferTestCase() {
		System.out.println("GAS_LIMIT : " + DefaultGasProvider.GAS_LIMIT.toString());
		System.out.println("GAS_PRICE : " + DefaultGasProvider.GAS_PRICE.toString());
		System.out.println(EVMUtil.getNonce("http://185.128.137.240:8545",
				"d77b53b57eb48f4e01fe9bb607716da5b3cf566b1347c29fa105deff3cace01d"));
		final BigInteger[] tankhahNonce = { BigInteger.ZERO };
		tankhahNonce[0] = EVMUtil.getNonce("http://185.128.137.240:8546",
				"d77b53b57eb48f4e01fe9bb607716da5b3cf566b1347c29fa105deff3cace01d");
		System.out.println("Current none of tankhah wallet is : " + tankhahNonce[0].toString());
		var gasPrice = new BigInteger("110000000000");
		try {
			System.out.println("Current gas price : " + EVMUtil.requestCurrentGasPrice("http://185.173.129.244:8545"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Estimate gas price : " + EVMUtil.getEstimateGas("http://185.173.129.244:8545"));
		//for (int i = 0; i < 10000; i++) {
			transferBetweenToAccount("http://185.128.137.240:8546",
					"7784750f322c77d57be67a30dfb8bc15fcd5e1b322a38009905de4142b9d97ec",
					"e6934f80c7390f3952e0f03bf43583cf9d57d4a1", "0x65B84D90CaF1Eb50888504F7Eb19B5a77BE9890f","0xE61D3f41E12f7De653C68777A791A883f151f103",
					new BigDecimal("0.0001"), EVMUtil.DefaultGasPrice ,EVMUtil.DefaultTokenGasLimit, tankhahNonce[0]);
			tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
		//}

		// System.out.println(EvmWalletUtil.generateWallet(new
		// BigInteger("46295382012785272375870482308001729876667835349322542732133821350485638298354")));
	}

	@Test
	public void transferTestTokenWithDynamicGasPriceCase() {
		var privateKey = "233a0f2308d201fa548e06a81080062efd58dbd77adfa68f660a8a6601711a61";
		var rpcUrl = "http://185.128.137.240:8545";
		var contract = "0x47fbc1D04511bfB1C3d64DA950c88815D02114F4";
		var to = "0x8f6101eA765e70950918B7D3bFcfA0931C7dEA78";
		var amount  = new BigDecimal(1);
		BigInteger gasPrice = EVMUtil.getEstimateGasPriceAsWei(rpcUrl);
		BigInteger nonce = EVMUtil.getNonce(rpcUrl,
				privateKey);
		System.out.println("Current none of tankhah wallet is : " + nonce.toString());
		System.out.println("Estimate gas price : " + gasPrice);
		try {
			var result = EVMUtil.sendSmartContractTransactionSync(rpcUrl, privateKey, contract, to, amount,
					nonce, gasPrice, EVMUtil.DefaultTokenGasLimit);
			System.out.println(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransactionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
