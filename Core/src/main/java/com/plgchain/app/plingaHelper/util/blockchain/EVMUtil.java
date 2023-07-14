/**
 *
 */
package com.plgchain.app.plingaHelper.util.blockchain;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.crypto.Credentials;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;

import com.alibaba.fastjson2.JSON;

import org.web3j.utils.Numeric;

/**
 *
 */
public class EVMUtil implements Serializable {

	private static final long serialVersionUID = -4942141276353870379L;

	public static BigInteger DefaultGasPrice = new BigInteger("12500000");

	public static BigInteger DefaultGasLimit = new BigInteger("21000");

	public static final String mainToken = "0x0000000000000000000000000000000000001010";

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

	public static BigInteger getNonce(String rpcUrl, String privateKeyHex) {
		Web3j web3j = Web3j.build(new HttpService(rpcUrl));
		Credentials credentials = Credentials.create(privateKeyHex);
		EthGetTransactionCount ethGetTransactionCount;
		while (true) {
			try {
				ethGetTransactionCount = web3j
						.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST).send();
				return ethGetTransactionCount.getTransactionCount();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static BigInteger getGasLimit(String rpcUrl) {
		Web3j web3j = Web3j.build(new HttpService(rpcUrl));

		try {
			EthBlock.Block latestBlock = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send()
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

	public static EthSendTransaction createRawTransaction(String rpcUrl, String privateKeyHex, String recipientAddress,
			BigDecimal amount) throws IOException {
		Web3j web3j = Web3j.build(new HttpService(rpcUrl));
		Credentials credentials = Credentials.create(privateKeyHex);
		/*
		 * EthGetTransactionCount ethGetTransactionCount = web3j
		 * .ethGetTransactionCount(credentials.getAddress(),
		 * DefaultBlockParameterName.LATEST).send();
		 */
		// BigInteger nonce = ethGetTransactionCount.getTransactionCount();
		RawTransaction rawTransaction = RawTransaction.createEtherTransaction(getNonce(rpcUrl, privateKeyHex),
				DefaultGasPrice, getGasLimit(rpcUrl), recipientAddress, getWei(amount));

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

	public static BigInteger getTransactionCount(String rpcUrl, String address) {
		Web3j web3j = Web3j.build(new HttpService(rpcUrl));
		EthGetTransactionCount result = new EthGetTransactionCount();
		try {
			result = web3j.ethGetTransactionCount(address, DefaultBlockParameter.valueOf("latest")).sendAsync().get();
			return result.getTransactionCount();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static boolean mostIncreaseNonce(EthSendTransaction etht) {
		if (!etht.hasError()) {
			return true;
		} else {
			if (etht.getError().getMessage().equals("nonce too low"))
				return true;
			else if (etht.getError().getMessage().equals("replacement transaction underpriced"))
				return true;
			else if (etht.getError().getMessage().equals("already known"))
				return true;

		}
		return false;
	}

	public static boolean haveSufficientFund(EthSendTransaction etht) {
		if (!etht.hasError()) {
			return true;
		} else {
			if (etht.getError().getMessage().contains("insufficient funds"))
				return false;

		}
		return true;
	}

	public static int hexToDecimal(String hex) {
		int decimal;
		if (hex.startsWith("0x")) {
			decimal = Integer.parseInt(hex.substring(2), 16);
		} else {
			decimal = Integer.parseInt(hex, 16);
		}
		return decimal;
	}

	public static BigInteger getEstimateGas(HttpClient httpClient, String rpcUrl) {
		HttpRequest request = null;
		HttpResponse<String> response = null;
		try {
			request = HttpRequest.newBuilder().uri(URI.create(rpcUrl))
					.POST(HttpRequest.BodyPublishers
							.ofString("{\"jsonrpc\":\"2.0\",\"method\":\"eth_estimateGas\",\"params\":[],\"id\":1}"))
					.header("Content-Type", "application/json").build();

			response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {
				String responseBody = response.body();
				String blockNumberHex = JSON.parseObject(responseBody).getString("result");

				// Parse the JSON response and extract the block number
				// Assuming the response is in the format {"result":"0x123..."}
				request = null;
				response = null;
				return new BigInteger(String.valueOf(EVMUtil.hexToDecimal(blockNumberHex)));
			} else {
				System.err.println("Error: " + response.statusCode() + " " + response.body());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			request = null;
			response = null;
		}

		return null;
	}

	public static BigInteger getEstimateGas(String rpcUrl) {
		HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
		HttpRequest request = null;
		HttpResponse<String> response = null;
		try {
			request = HttpRequest.newBuilder().uri(URI.create(rpcUrl))
					.POST(HttpRequest.BodyPublishers
							.ofString("{\"jsonrpc\":\"2.0\",\"method\":\"eth_estimateGas\",\"params\":[],\"id\":1}"))
					.header("Content-Type", "application/json").build();

			response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {
				String responseBody = response.body();
				String blockNumberHex = JSON.parseObject(responseBody).getString("result");

				// Parse the JSON response and extract the block number
				// Assuming the response is in the format {"result":"0x123..."}
				request = null;
				response = null;
				return new BigInteger(String.valueOf(EVMUtil.hexToDecimal(blockNumberHex)));
			} else {
				System.err.println("Error: " + response.statusCode() + " " + response.body());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			request = null;
			response = null;
		}

		return null;
	}

	public static CompletableFuture<EthSendTransaction> createRawTransactionAsync(String rpcUrl, String privateKeyHex,
			String recipientAddress, BigDecimal amount, BigInteger nonce, BigInteger gasPrice) throws IOException {
		Web3j web3j = Web3j.build(new HttpService(rpcUrl));
		Credentials credentials = Credentials.create(privateKeyHex);
		RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, requestCurrentGasPrice(rpcUrl),
				gasPrice, recipientAddress, getWei(amount));

		byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
		String hexValue = Numeric.toHexString(signedMessage);

		CompletableFuture<EthSendTransaction> ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync();
		return ethSendTransaction;
	}

	public static EthSendTransaction createRawTransactionSync(String rpcUrl, String privateKeyHex,
			String recipientAddress, BigDecimal amount, BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit)
			throws IOException {
		var web3j = Web3j.build(new HttpService(rpcUrl));
		var credentials = Credentials.create(privateKeyHex);
		var rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, recipientAddress,
				getWei(amount));

		byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
		String hexValue = Numeric.toHexString(signedMessage);

		return web3j.ethSendRawTransaction(hexValue).send();
	}

	public static BigDecimal getAccountBalance(String rpcUrl, String walletAddress) {
		Web3j web3j = Web3j.build(new HttpService(rpcUrl));
		EthGetBalance ethGetBalance = null;
		while (true) {
			try {
				ethGetBalance = web3j.ethGetBalance(walletAddress, DefaultBlockParameterName.LATEST).send();
				BigInteger wei = ethGetBalance.getBalance();
				BigDecimal tokenValue = Convert.fromWei(String.valueOf(wei), Convert.Unit.ETHER);
				return tokenValue;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static TransactionReceipt transferToken(String rpcUrl, String privateKey, String contractAddress,
			String address, BigInteger amount) {
		var web3j = Web3j.build(new HttpService(rpcUrl));
		var credentials = Credentials.create(privateKey);
		ERC20 javaToken = null;
		try {
			javaToken = ERC20.load(contractAddress, web3j, credentials, new DefaultGasProvider());
			return javaToken.transfer(address, amount).send();
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (web3j != null) {
				web3j.shutdown();
			}
		}
		return null;
	}

	public static CompletableFuture<EthSendTransaction> sendSmartContractTransactionAsync(String rpcUrl,
			String privateKey, String contractAddress, String address, BigInteger amount, BigInteger nonce,
			BigInteger gasPrice, BigInteger gasLimit)
			throws IOException, TransactionException, InterruptedException, ExecutionException {
		Web3j web3j = Web3j.build(new HttpService(rpcUrl));
		Credentials credentials = Credentials.create(privateKey);
		Function function = new Function("transfer", Arrays.asList(new Address(address), new Uint256(amount)),
				Collections.singletonList(new TypeReference<Bool>() {
				}));
		;
		String encodedFunction = FunctionEncoder.encode(function);
		/*
		 * RawTransaction rawTransaction = RawTransaction.createTransaction(nounce,
		 * DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT, address, txData);
		 */
		RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, contractAddress,
				encodedFunction);

		byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
		String hexValue = Numeric.toHexString(signedMessage);
		return web3j.ethSendRawTransaction(hexValue).sendAsync();

	}

	public static String sendSmartContractTransaction(String rpcUrl, String privateKey, String contractAddress,
			String address, BigInteger amount, BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit)
			throws IOException, TransactionException, InterruptedException, ExecutionException {
		var web3j = Web3j.build(new HttpService(rpcUrl));
		var credentials = Credentials.create(privateKey);
		var function = new Function("transfer", Arrays.asList(new Address(address), new Uint256(amount)),
				Collections.singletonList(new TypeReference<Bool>() {
				}));
		var encodedFunction = FunctionEncoder.encode(function);
		var rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, contractAddress,
				encodedFunction);

		var signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
		var hexValue = Numeric.toHexString(signedMessage);
		var ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
		return ethSendTransaction.getTransactionHash();
	}

	public static BigDecimal getTokenBalancSync(String rpcUrl, String privateKey, String contractAddress) {
		Web3j web3j = Web3j.build(new HttpService(rpcUrl));
		Credentials credentials = Credentials.create(privateKey);
		ERC20 javaToken = ERC20.load(contractAddress, web3j, credentials, new DefaultGasProvider());
		while (true) {
			try {
				RemoteCall<BigInteger> balanceWei = javaToken.balanceOf(credentials.getAddress());
				return convertWeiToEther(balanceWei.send());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static BigInteger getTokenBalanceAsWei(String rpcUrl, String privateKey, String contractAddress) {
		Web3j web3j = Web3j.build(new HttpService(rpcUrl));
		Credentials credentials = Credentials.create(privateKey);
		ERC20 javaToken = ERC20.load(contractAddress, web3j, credentials, new DefaultGasProvider());
		while (true) {
			try {
				RemoteCall<BigInteger> balanceWei = javaToken.balanceOf(credentials.getAddress());
				return balanceWei.send();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
