/**
 *
 */
package com.plgchain.app.plingaHelper.util.blockchain;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.apache.commons.lang3.RandomStringUtils;
import org.jsoup.Jsoup;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Bip44WalletUtils;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;
import org.web3j.utils.Numeric;

/**
 *
 */
public class EVMUtil implements Serializable {

	private static final long serialVersionUID = -4942141276353870379L;

	public static BigInteger DefaultGasPrice = new BigInteger("1100000000");

	public static BigInteger DefaultGasLimit = new BigInteger("12500000");

	public static BigInteger getWei(BigDecimal value) {
		return Convert.toWei(value.toString(), Unit.ETHER).toBigInteger();
	}

	public static String getWeiAsHex(BigDecimal value) {
		return Numeric.toHexStringWithPrefix(getWei(value));
	}

	public static String getWeiAsHex(String value) {
		return Numeric.toHexStringWithPrefix(getWei(value));
	}

	public static BigInteger getWei(String value) {
		return Convert.toWei(value, Unit.ETHER).toBigInteger();
	}

	public static BigDecimal getWeiBigDecimal(String value) {
		return Convert.toWei(value, Unit.ETHER);
	}

	public static BigDecimal getWeiBigDecimal(BigInteger value) {
		return Convert.toWei(value.toString(), Unit.ETHER);
	}

	public static BigDecimal convertWeiToEther(String val) {
		return Convert.fromWei(val, Convert.Unit.ETHER);
	}

	public static BigDecimal convertWeiToEther(BigInteger val) {
		return Convert.fromWei(val.toString(), Convert.Unit.ETHER);
	}

	public static BigDecimal convertWeiToEther(BigDecimal val) {
		return Convert.fromWei(val.toString(), Convert.Unit.ETHER);
	}

	public static byte[] signMessage(RawTransaction rawTransaction, Credentials credentials) {
		return TransactionEncoder.signMessage(rawTransaction, credentials);
	}

	public static BigInteger getNonce(String rpcUrl, String privateKeyHex) throws IOException {
		Web3j web3j = Web3j.build(new HttpService(rpcUrl));
		Credentials credentials = Credentials.create(privateKeyHex);
		EthGetTransactionCount ethGetTransactionCount = web3j
				.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST).send();
		return ethGetTransactionCount.getTransactionCount();
	}

	public static BigInteger getGasLimit(String rpcUrl) {
        Web3j web3j = Web3j.build(new HttpService(rpcUrl));

        try {
            EthBlock.Block latestBlock = web3j.ethGetBlockByNumber(
                    DefaultBlockParameterName.LATEST, false)
                    .send()
                    .getBlock();
            if (latestBlock != null) {
                return latestBlock.getGasLimit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return BigInteger.ZERO;
    }

	public static BigInteger requestCurrentGasPrice(String rpcUrl) throws IOException {
		Web3j web3j = Web3j.build(new HttpService(rpcUrl));
		EthGasPrice ethGasPrice = web3j.ethGasPrice().send();
		return ethGasPrice.getGasPrice();
	}

	public static EthSendTransaction createRawTransaction(String rpcUrl, String privateKeyHex,
			String recipientAddress, BigDecimal amount) throws IOException {
		Web3j web3j = Web3j.build(new HttpService(rpcUrl));
		Credentials credentials = Credentials.create(privateKeyHex);
		/*
		 * EthGetTransactionCount ethGetTransactionCount = web3j
		 * .ethGetTransactionCount(credentials.getAddress(),
		 * DefaultBlockParameterName.LATEST).send();
		 */
		// BigInteger nonce = ethGetTransactionCount.getTransactionCount();
		RawTransaction rawTransaction = RawTransaction.createEtherTransaction(getNonce(rpcUrl, privateKeyHex), DefaultGasPrice,
				getGasLimit(rpcUrl), recipientAddress, getWei(amount));

		// Sign the transaction
		byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
		String hexValue = Numeric.toHexString(signedMessage);

		// Send transaction
		EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();
		return ethSendTransaction;

	}

	public static TransactionReceipt transferToken(String rpcUrl, String privateKey, String contractAddress,
			String address, BigDecimal amount) {
		Web3j web3j = Web3j.build(new HttpService(rpcUrl));
		Credentials credentials = Credentials.create(privateKey);
		ERC20 javaToken = ERC20.load(contractAddress, web3j, credentials, new DefaultGasProvider());
		try {
			TransactionReceipt tr = javaToken
					.transfer(address, Convert.toWei(amount.toString(), Unit.ETHER).toBigInteger()).send();
			return tr;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

}
