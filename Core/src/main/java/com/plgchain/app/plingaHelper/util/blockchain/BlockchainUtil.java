/**
 *
 */
package com.plgchain.app.plingaHelper.util.blockchain;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthBlockNumber;

/**
 *
 */
public class BlockchainUtil implements Serializable {

	private static final long serialVersionUID = -7284069974541384059L;

	public static BigInteger getLatestBlockNumber(String rpcUrl){
        Web3j web3j = Web3j.build(new HttpService(rpcUrl));

        Request<?, EthBlockNumber> request = web3j.ethBlockNumber();

        EthBlockNumber response;
		try {
			response = request.send();
			return response.getBlockNumber();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return null;
    }

}