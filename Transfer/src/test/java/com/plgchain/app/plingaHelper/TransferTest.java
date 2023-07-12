/**
 *
 */
package com.plgchain.app.plingaHelper;

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

	public void transferBetweenToAccount(String rpcUrl, String privateKey, String from, String to, BigDecimal amount, BigInteger gasPrice, BigInteger nonce) {
	    EthSendTransaction result = null;
	    BigInteger[] finalNonce = {nonce};
	    BigInteger[] finalGasPrice = {gasPrice};
	    System.out.println(String.format("ُTry to transfer %s Maincoin from %s/%s to %s and with nonce %s with gasPrice %s with rpcurl %s",
                amount, from,privateKey, to, finalNonce[0].toString(), finalGasPrice[0].toString(),rpcUrl));
	    while (true) {
	        try {
	            //result = EVMUtil.createRawTransactionSync(rpcUrl, privateKey, to, amount, finalNonce[0], finalGasPrice[0]);

	            Optional.ofNullable(result)
	                    .filter(r -> !r.hasError())
	                    .filter(r -> r.getTransactionHash() != null && !r.getTransactionHash().isBlank())
	                    .ifPresent(r -> {
	                    	System.out.println(String.format("Transfered %s Maincoin from %s to %s and txHash is %s with nonce %s with gasPrice %s",
	                                amount, from, to, r.getTransactionHash(), finalNonce[0].toString(), finalGasPrice[0].toString()));
	                    });

	            if (result != null && EVMUtil.mostIncreaseNonce(result)) {
	                finalNonce[0] = finalNonce[0].add(BigInteger.ONE);
	            } else {
	                break;
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            break;
	        }
	    }
	}

	@Test
	public void transferTestCase() {
		/*
		 * transferBetweenToAccount("http://185.173.129.243:18545",
		 * "46295382012785272375870482308001729876667835349322542732133821350485638298354",
		 * "e6934f80c7390f3952e0f03bf43583cf9d57d4a1",
		 * "0x3b939cd104194aa7d359b79de3e935bd7c20afa5", new BigDecimal("359.95"), new
		 * BigInteger("1100000000"), BigInteger.ZERO);
		 */

		System.out.println(EvmWalletUtil.generateWallet(new BigInteger("46295382012785272375870482308001729876667835349322542732133821350485638298354")));
	}

}
