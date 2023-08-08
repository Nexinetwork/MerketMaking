/**
 *
 */
package com.plgchain.app.plingaHelper.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.EthSendTransaction;

import com.plgchain.app.plingaHelper.constant.WalletType;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.entity.coingecko.Coin;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.entity.marketMaking.MarketMaking;
import com.plgchain.app.plingaHelper.entity.marketMaking.MarketMakingWallet;
import com.plgchain.app.plingaHelper.service.MarketMakingWalletService;
import com.plgchain.app.plingaHelper.util.blockchain.EVMUtil;
import com.plgchain.app.plingaHelper.util.blockchain.EvmWalletUtil;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 *
 */
@Component
public class TransferBean implements Serializable {

	private static final long serialVersionUID = -8213356057891230140L;

	private static final Logger logger = LoggerFactory.getLogger(TransferBean.class);

	private static final int waitOnMaxEnqueIsSeconds = 2;

	@Inject
	private MarketMakingWalletService marketMakingWalletService;

	public void generateWalletsForMarketmaking(MarketMaking mm, int jpaBatchCount, SmartContract contract,
			Blockchain blockchain, Coin coin, WalletType walletType) {
		List<MarketMakingWallet> wList = EvmWalletUtil.generateRandomWallet(mm.getInitialWallet()).stream()
				.map(w -> MarketMakingWallet.builder().balance(w.getBalance()).blockchain(blockchain).coin(coin)
						.contract(contract).contractAddress(contract.getContractsAddress())
						.privateKey(w.getPrivateKey()).privateKeyHex(w.getHexKey()).publicKey(w.getPublicKey())
						.walletType(walletType).mainCoinBalance(BigDecimal.ZERO).build())
				.collect(Collectors.toList());

		marketMakingWalletService.batchSaveAll(wList, jpaBatchCount);
	}

	/*
	 * public void transferBetweenToAccount(String rpcUrl,String privateKey,String
	 * from,String to,BigDecimal amount,BigInteger gasPrice,BigInteger nonce) {
	 * boolean mustRetry = true; EthSendTransaction result = null; while (mustRetry)
	 * { try { result = EVMUtil.createRawTransactionSync(rpcUrl, privateKey, to,
	 * amount,nonce,gasPrice); if (result != null) { if (!result.hasError()) { if
	 * (result.getTransactionHash() != null) { if
	 * (!result.getTransactionHash().isBlank()) { logger.info(String.format(
	 * "ُTransfered %s Maincoin from %s to %s and txHash is %s with nonce %s with gasPrice %s"
	 * , amount, from, to, result.getTransactionHash(),
	 * nonce.toString(),gasPrice.toString())); mustRetry = false; } } } } } catch
	 * (Exception e) { logger.error(e.getMessage()); } if (result != null) { if
	 * (EVMUtil.mostIncreaseNonce(result)) nonce = nonce.add(new BigInteger("1"));
	 * else mustRetry = false; } } }
	 */

	@Async
	public void transferBetweenToAccount(String rpcUrl, String privateKey, String from, String to, BigDecimal amount,
			BigInteger gasPrice, BigInteger gasLimit, BigInteger nonce) {
		EthSendTransaction result = null;
		BigInteger[] finalNonce = { nonce };
		BigInteger[] finalGasPrice = { gasPrice };
		BigInteger[] finalGasLimit = { gasLimit };
		boolean[] shouldBreak = { false };
		if (finalGasPrice[0] == null) {
			logger.error("Transaction Gasprice is null");
			throw new RuntimeException("Gas price is Zero");
		}
		else if (finalGasPrice[0].equals(BigInteger.ZERO)) {
			logger.error("Transaction Gasprice is zero");
			throw new RuntimeException("Gas price is Zero");
		}
		else if (finalGasLimit[0] == null) {
			logger.error("Transaction Gaslimit is null");
			throw new RuntimeException("Gas Limit is Zero");
		}
		else if (finalGasLimit[0].equals(BigInteger.ZERO)) {
			logger.error("Transaction Gaslimit is zero");
			throw new RuntimeException("Gas Limit is Zero");
		}
		while (!shouldBreak[0]) {
			try {
				result = EVMUtil.createRawTransactionSync(rpcUrl, privateKey, to, amount, finalNonce[0],
						finalGasPrice[0], finalGasLimit[0]);

				Optional.ofNullable(result).filter(r -> !r.hasError())
						.filter(r -> r.getTransactionHash() != null && !r.getTransactionHash().isBlank())
						.ifPresent(r -> {
							/*
							 * logger.info(String.format(
							 * "Transfered %s Maincoin from %s to %s and txHash is %s with nonce %s with gasPrice %s and gaslimit %s"
							 * , amount, from, to, r.getTransactionHash(), finalNonce[0].toString(),
							 * finalGasPrice[0].toString(), finalGasLimit[0].toString()));
							 */
							shouldBreak[0] = true;
						});
				if (result != null) {
					if (EVMUtil.mostIncreaseNonce(result))
						finalNonce[0] = finalNonce[0].add(BigInteger.ONE);
					else {
						logger.error(String.format("message is %s and Error is %s but try again.", result.getResult(),
								result.getError().getMessage()));
						if (result.getError().getMessage().contains("insufficient funds for gas")) {
							logger.error(String.format("Insufficent main coin for wallet %s", from));
							shouldBreak[0] = true;
						}
					}
				}
			} catch (Exception e) {
				logger.error("Error is : " + e.getMessage());
				shouldBreak[0] = true;
			}
		}
	}

	public void transferBetweenToAccountSync(String rpcUrl, String privateKey, String from, String to,
			BigDecimal amount, BigInteger gasPrice, BigInteger gasLimit, BigInteger nonce) {
		EthSendTransaction result = null;
		BigInteger[] finalNonce = { nonce };
		BigInteger[] finalGasPrice = { gasPrice };
		BigInteger[] finalGasLimit = { gasLimit };
		boolean[] shouldBreak = { false };
		if (finalGasPrice[0] == null) {
			logger.error("Transaction Gasprice is null");
			throw new RuntimeException("Gas price is Zero");
		}
		else if (finalGasPrice[0].equals(BigInteger.ZERO)) {
			logger.error("Transaction Gasprice is zero");
			throw new RuntimeException("Gas price is Zero");
		}
		else if (finalGasLimit[0] == null) {
			logger.error("Transaction Gaslimit is null");
			throw new RuntimeException("Gas Limit is Zero");
		}
		else if (finalGasLimit[0].equals(BigInteger.ZERO)) {
			logger.error("Transaction Gaslimit is zero");
			throw new RuntimeException("Gas Limit is Zero");
		}
		while (!shouldBreak[0]) {
			try {
				result = EVMUtil.createRawTransactionSync(rpcUrl, privateKey, to, amount, finalNonce[0],
						finalGasPrice[0], finalGasLimit[0]);

				Optional.ofNullable(result).filter(r -> !r.hasError())
						.filter(r -> r.getTransactionHash() != null && !r.getTransactionHash().isBlank())
						.ifPresent(r -> {
							logger.info(String.format(
									"Transfered %s Maincoin from %s to %s and txHash is %s with nonce %s with gasPrice %s and gaslimit %s",
									amount, from, to, r.getTransactionHash(), finalNonce[0].toString(),
									finalGasPrice[0].toString(), finalGasLimit[0].toString()));
							shouldBreak[0] = true;
						});
				if (result != null) {
					if (EVMUtil.mostIncreaseNonce(result))
						finalNonce[0] = finalNonce[0].add(BigInteger.ONE);
					else {
						if (result.getError().getMessage().contains("insufficient funds for gas")) {
							logger.error(String.format("Insufficent main coin for wallet %s", from));
							shouldBreak[0] = true;
						} else if (result.getError().getMessage()
								.contains("maximum number of enqueued transactions reached")) {
							throw new RuntimeException("maximum number of enqueued transactions reached");
						}
					}
				}
			} catch (Exception e) {
				logger.error("Transfer Error is : " + e.getMessage());
				if (e.getMessage().contains("Cannot assign requested address"))
					try {
						Thread.sleep(waitOnMaxEnqueIsSeconds * 1000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						logger.error(e1.getMessage());
						;
					}
				else
					shouldBreak[0] = true;
			}
		}
	}

	@Async
	public void transferBetweenToAccount(String rpcUrl, String privateKey, String from, String to, BigDecimal amount,
			BigInteger gasLimit, BigInteger nonce) {
		EthSendTransaction result = null;
		BigInteger[] finalNonce = { nonce };
		BigInteger[] finalGasPrice = { BigInteger.ZERO };
		BigInteger[] finalGasLimit = { gasLimit };
		boolean[] shouldBreak = { false };
		while (!shouldBreak[0]) {
			try {
				finalGasPrice[0] = EVMUtil.getEstimateGasPriceAsWei(rpcUrl);
				if (finalGasPrice[0] == null) {
					logger.error("Transaction Gasprice is null");
					throw new RuntimeException("Gas price is Zero");
				}
				else if (finalGasPrice[0].equals(BigInteger.ZERO)) {
					logger.error("Transaction Gasprice is zero");
					throw new RuntimeException("Gas price is Zero");
				}
				else if (finalGasLimit[0] == null) {
					logger.error("Transaction Gaslimit is null");
					throw new RuntimeException("Gas Limit is Zero");
				}
				else if (finalGasLimit[0].equals(BigInteger.ZERO)) {
					logger.error("Transaction Gaslimit is zero");
					throw new RuntimeException("Gas Limit is Zero");
				}
				logger.info(String.format(
						"Try to transfer maincoin for contract as fee in %s from %s/%s to %s with amount %s with gaslimit %s and gasprice %s with nonce %s",
						rpcUrl, from, privateKey, to, amount, gasLimit, finalGasPrice[0], finalNonce[0]));
				result = EVMUtil.createRawTransactionSync(rpcUrl, privateKey, to, amount, finalNonce[0],
						finalGasPrice[0], finalGasLimit[0]);

				Optional.ofNullable(result).filter(r -> !r.hasError())
						.filter(r -> r.getTransactionHash() != null && !r.getTransactionHash().isBlank())
						.ifPresent(r -> {
							logger.info(String.format(
									"Transfered %s Maincoin from %s to %s and txHash is %s with nonce %s with gasPrice %s and gaslimit %s",
									amount, from, to, r.getTransactionHash(), finalNonce[0].toString(),
									finalGasPrice[0].toString(), finalGasLimit[0].toString()));
							shouldBreak[0] = true;
						});
				if (result != null) {
					if (EVMUtil.mostIncreaseNonce(result))
						finalNonce[0] = finalNonce[0].add(BigInteger.ONE);
					else {
						logger.error(String.format("message is %s and Error is %s but try again.", result.getResult(),
								result.getError().getMessage()));
						if (result.getError().getMessage().contains("insufficient funds for gas")) {
							logger.error(String.format("Insufficent main coin for wallet %s", from));
							shouldBreak[0] = true;
						}
					}
				}
			} catch (Exception e) {
				logger.error("Error is : " + e.getMessage());
				shouldBreak[0] = true;
			}
		}
	}

	public void transferBetweenToAccountSync(String rpcUrl, String privateKey, String from, String to,
			BigDecimal amount, BigInteger gasLimit, BigInteger nonce) {
		EthSendTransaction result = null;
		BigInteger[] finalNonce = { nonce };
		BigInteger[] finalGasPrice = { BigInteger.ZERO };
		BigInteger[] finalGasLimit = { gasLimit };
		boolean[] shouldBreak = { false };
		while (!shouldBreak[0]) {
			try {
				finalGasPrice[0] = EVMUtil.getEstimateGasPriceAsWei(rpcUrl);
				if (finalGasPrice[0] == null) {
					logger.error("Transaction Gasprice is null");
					throw new RuntimeException("Gas price is Zero");
				}
				else if (finalGasPrice[0].equals(BigInteger.ZERO)) {
					logger.error("Transaction Gasprice is zero");
					throw new RuntimeException("Gas price is Zero");
				}
				else if (finalGasLimit[0] == null) {
					logger.error("Transaction Gaslimit is null");
					throw new RuntimeException("Gas Limit is Zero");
				}
				else if (finalGasLimit[0].equals(BigInteger.ZERO)) {
					logger.error("Transaction Gaslimit is zero");
					throw new RuntimeException("Gas Limit is Zero");
				}
				result = EVMUtil.createRawTransactionSync(rpcUrl, privateKey, to, amount, finalNonce[0],
						finalGasPrice[0], finalGasLimit[0]);

				Optional.ofNullable(result).filter(r -> !r.hasError())
						.filter(r -> r.getTransactionHash() != null && !r.getTransactionHash().isBlank())
						.ifPresent(r -> {
							logger.info(String.format(
									"Transfered %s Maincoin from %s to %s and txHash is %s with nonce %s with gasPrice %s and gaslimit %s",
									amount, from, to, r.getTransactionHash(), finalNonce[0].toString(),
									finalGasPrice[0].toString(), finalGasLimit[0].toString()));
							shouldBreak[0] = true;
						});
				if (result != null) {
					if (EVMUtil.mostIncreaseNonce(result))
						finalNonce[0] = finalNonce[0].add(BigInteger.ONE);
					else {
						if (result.getError().getMessage().contains("insufficient funds for gas")) {
							logger.error(String.format("Insufficent main coin for wallet %s", from));
							shouldBreak[0] = true;
						} else if (result.getError().getMessage()
								.contains("maximum number of enqueued transactions reached")) {
							throw new RuntimeException("maximum number of enqueued transactions reached");
						}
					}
				}
			} catch (Exception e) {
				// logger.error("Error is : " + e.getMessage());
				if (e.getMessage().contains("Cannot assign requested address"))
					try {
						Thread.sleep(waitOnMaxEnqueIsSeconds * 1000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						logger.error(e1.getMessage());
						;
					}
				else
					shouldBreak[0] = true;
			}
		}
	}

	@Async
	public void transferBetweenToAccount(String rpcUrl, String privateKey, String from, String to, String contract,
			BigDecimal amount, BigInteger gasPrice, BigInteger gasLimit, BigInteger nonce) {
		EthSendTransaction result = null;
		BigInteger[] finalNonce = { nonce };
		BigInteger[] finalGasPrice = { gasPrice };
		BigInteger[] finalGasLimit = { gasLimit };
		boolean[] shouldBreak = { false };
		if (finalGasPrice[0] == null) {
			logger.error("Transaction Gasprice is null");
			throw new RuntimeException("Gas price is Zero");
		}
		else if (finalGasPrice[0].equals(BigInteger.ZERO)) {
			logger.error("Transaction Gasprice is zero");
			throw new RuntimeException("Gas price is Zero");
		}
		else if (finalGasLimit[0] == null) {
			logger.error("Transaction Gaslimit is null");
			throw new RuntimeException("Gas Limit is Zero");
		}
		else if (finalGasLimit[0].equals(BigInteger.ZERO)) {
			logger.error("Transaction Gaslimit is zero");
			throw new RuntimeException("Gas Limit is Zero");
		}
		/*
		 * logger.info(String.format(
		 * "try to transfer %s token %s from %s/%s to %s and nonce %s with gasPrice %s and gaslimit %s"
		 * , amount, contract, from, privateKey, to, finalNonce[0].toString(),
		 * finalGasPrice[0].toString(), finalGasLimit[0].toString()));
		 */
		while (!shouldBreak[0]) {
			try {
				result = EVMUtil.sendSmartContractTransactionSync(rpcUrl, privateKey, contract, to, amount,
						finalNonce[0], finalGasPrice[0], finalGasLimit[0]);

				Optional.ofNullable(result).filter(r -> !r.hasError())
						.filter(r -> r.getTransactionHash() != null && !r.getTransactionHash().isBlank())
						.ifPresent(r -> {
							logger.info(String.format(
									"Transfered %s token %s from %s to %s and txHash is %s with nonce %s with gasPrice %s and gaslimit %s",
									amount, contract, from, to, r.getTransactionHash(), finalNonce[0].toString(),
									finalGasPrice[0].toString(), finalGasLimit[0].toString()));
							shouldBreak[0] = true;
						});
				if (result != null) {
					if (EVMUtil.mostIncreaseNonce(result))
						finalNonce[0] = finalNonce[0].add(BigInteger.ONE);
					else {
						logger.error(String.format("message is %s and Error is %s but try again.", result.getResult(),
								result.getError().getMessage()));
						if (result.getError().getMessage().contains("insufficient funds for gas")) {
							logger.error(String.format("Insufficent main coin for wallet %s and contract %s", from,
									contract));
							shouldBreak[0] = true;
						}
					}
				}
			} catch (Exception e) {
				logger.error("Error is : " + e.getMessage());
				shouldBreak[0] = true;
			}
		}
	}

	public void transferBetweenToAccountSync(String rpcUrl, String privateKey, String from, String to, String contract,
			BigDecimal amount, BigInteger gasPrice, BigInteger gasLimit, BigInteger nonce) {
		EthSendTransaction result = null;
		BigInteger[] finalNonce = { nonce };
		BigInteger[] finalGasPrice = { gasPrice };
		BigInteger[] finalGasLimit = { gasLimit };
		boolean[] shouldBreak = { false };
		if (finalGasPrice[0] == null) {
			logger.error("Transaction Gasprice is null");
			throw new RuntimeException("Gas price is Zero");
		}
		else if (finalGasPrice[0].equals(BigInteger.ZERO)) {
			logger.error("Transaction Gasprice is zero");
			throw new RuntimeException("Gas price is Zero");
		}
		else if (finalGasLimit[0] == null) {
			logger.error("Transaction Gaslimit is null");
			throw new RuntimeException("Gas Limit is Zero");
		}
		else if (finalGasLimit[0].equals(BigInteger.ZERO)) {
			logger.error("Transaction Gaslimit is zero");
			throw new RuntimeException("Gas Limit is Zero");
		}
		logger.info(String.format(
				"try to transfer %s token %s from %s/%s to %s and nonce %s with gasPrice %s and gaslimit %s", amount,
				contract, from, privateKey, to, finalNonce[0].toString(), finalGasPrice[0].toString(),
				finalGasLimit[0].toString()));
		while (!shouldBreak[0]) {
			try {
				result = EVMUtil.sendSmartContractTransactionSync(rpcUrl, privateKey, contract, to, amount,
						finalNonce[0], finalGasPrice[0], finalGasLimit[0]);

				Optional.ofNullable(result).filter(r -> !r.hasError())
						.filter(r -> r.getTransactionHash() != null && !r.getTransactionHash().isBlank())
						.ifPresent(r -> {
							logger.info(String.format(
									"Transfered %s token %s from %s to %s and txHash is %s with nonce %s with gasPrice %s and gaslimit %s",
									amount, contract, from, to, r.getTransactionHash(), finalNonce[0].toString(),
									finalGasPrice[0].toString(), finalGasLimit[0].toString()));
							shouldBreak[0] = true;
						});
				if (result != null) {
					if (EVMUtil.mostIncreaseNonce(result))
						finalNonce[0] = finalNonce[0].add(BigInteger.ONE);
					else {
						if (result.getError().getMessage().contains("insufficient funds for gas")) {
							logger.error(String.format("Insufficent main coin for wallet %s and contract %s", from,
									contract));
							shouldBreak[0] = true;
						} else if (result.getError().getMessage()
								.contains("maximum number of enqueued transactions reached")) {
							throw new RuntimeException("maximum number of enqueued transactions reached");
						}
					}
				}
			} catch (Exception e) {
				// logger.error("Error is : " + e.getMessage());
				if (e.getMessage().contains("Cannot assign requested address"))
					try {
						Thread.sleep(waitOnMaxEnqueIsSeconds * 1000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						logger.error(e1.getMessage());
						;
					}
				else
					shouldBreak[0] = true;
			}
		}
	}

	@Async
	public void transferBetweenToAccount(String rpcUrl, String privateKey, String from, String to, String contract,
			BigDecimal amount, BigInteger gasLimit, BigInteger nonce) {
		EthSendTransaction result = null;
		BigInteger[] finalNonce = { nonce };
		BigInteger[] finalGasPrice = { BigInteger.ZERO };
		BigInteger[] finalGasLimit = { gasLimit };
		boolean[] shouldBreak = { false };
		while (!shouldBreak[0]) {
			try {
				finalGasPrice[0] = EVMUtil.getEstimateGasPriceAsWei(rpcUrl);
				if (finalGasPrice[0] == null) {
					logger.error("Transaction Gasprice is null");
					throw new RuntimeException("Gas price is Zero");
				}
				else if (finalGasPrice[0].equals(BigInteger.ZERO)) {
					logger.error("Transaction Gasprice is zero");
					throw new RuntimeException("Gas price is Zero");
				}
				else if (finalGasLimit[0] == null) {
					logger.error("Transaction Gaslimit is null");
					throw new RuntimeException("Gas Limit is Zero");
				}
				else if (finalGasLimit[0].equals(BigInteger.ZERO)) {
					logger.error("Transaction Gaslimit is zero");
					throw new RuntimeException("Gas Limit is Zero");
				}
				logger.info(String.format(
						"try to transfer %s token %s from %s/%s to %s and nonce %s with gasPrice %s and gaslimit %s",
						amount, contract, from, privateKey, to, finalNonce[0].toString(), finalGasPrice[0].toString(),
						finalGasLimit[0].toString()));
				result = EVMUtil.sendSmartContractTransactionSync(rpcUrl, privateKey, contract, to, amount,
						finalNonce[0], finalGasPrice[0], finalGasLimit[0]);

				Optional.ofNullable(result).filter(r -> !r.hasError())
						.filter(r -> r.getTransactionHash() != null && !r.getTransactionHash().isBlank())
						.ifPresent(r -> {
							logger.info(String.format(
									"Transfered %s token %s from %s to %s and txHash is %s with nonce %s with gasPrice %s and gaslimit %s",
									amount, contract, from, to, r.getTransactionHash(), finalNonce[0].toString(),
									finalGasPrice[0].toString(), finalGasLimit[0].toString()));
							shouldBreak[0] = true;
						});
				if (result != null) {
					if (EVMUtil.mostIncreaseNonce(result))
						finalNonce[0] = finalNonce[0].add(BigInteger.ONE);
					else {
						logger.error(String.format("message is %s and Error is %s but try again.", result.getResult(),
								result.getError().getMessage()));
						if (result.getError().getMessage().contains("insufficient funds for gas")) {
							logger.error(String.format("Insufficent main coin for wallet %s and contract %s", from,
									contract));
							shouldBreak[0] = true;
						}
					}
				}
			} catch (Exception e) {
				logger.error("Error is : " + e.getMessage());
				shouldBreak[0] = true;
			}
		}
	}

	public void transferBetweenToAccountSync(String rpcUrl, String privateKey, String from, String to, String contract,
			BigDecimal amount, BigInteger gasLimit, BigInteger nonce) {
		EthSendTransaction result = null;
		BigInteger[] finalNonce = { nonce };
		BigInteger[] finalGasPrice = { BigInteger.ZERO };
		BigInteger[] finalGasLimit = { gasLimit };
		boolean[] shouldBreak = { false };
		while (!shouldBreak[0]) {
			try {
				finalGasPrice[0] = EVMUtil.getEstimateGasPriceAsWei(rpcUrl);
				if (finalGasPrice[0] == null) {
					logger.error("Transaction Gasprice is null");
					throw new RuntimeException("Gas price is Zero");
				}
				else if (finalGasPrice[0].equals(BigInteger.ZERO)) {
					logger.error("Transaction Gasprice is zero");
					throw new RuntimeException("Gas price is Zero");
				}
				else if (finalGasLimit[0] == null) {
					logger.error("Transaction Gaslimit is null");
					throw new RuntimeException("Gas Limit is Zero");
				}
				else if (finalGasLimit[0].equals(BigInteger.ZERO)) {
					logger.error("Transaction Gaslimit is zero");
					throw new RuntimeException("Gas Limit is Zero");
				}
				logger.info(String.format(
						"try to transfer %s token %s from %s/%s to %s and nonce %s with gasPrice %s and gaslimit %s",
						amount, contract, from, privateKey, to, finalNonce[0].toString(), finalGasPrice[0].toString(),
						finalGasLimit[0].toString()));
				result = EVMUtil.sendSmartContractTransactionSync(rpcUrl, privateKey, contract, to, amount,
						finalNonce[0], finalGasPrice[0], finalGasLimit[0]);

				Optional.ofNullable(result).filter(r -> !r.hasError())
						.filter(r -> r.getTransactionHash() != null && !r.getTransactionHash().isBlank())
						.ifPresent(r -> {
							logger.info(String.format(
									"Transfered %s token %s from %s to %s and txHash is %s with nonce %s with gasPrice %s and gaslimit %s",
									amount, contract, from, to, r.getTransactionHash(), finalNonce[0].toString(),
									finalGasPrice[0].toString(), finalGasLimit[0].toString()));
							shouldBreak[0] = true;
						});
				if (result != null) {
					if (EVMUtil.mostIncreaseNonce(result))
						finalNonce[0] = finalNonce[0].add(BigInteger.ONE);
					else {
						if (result.getError().getMessage().contains("insufficient funds for gas")) {
							logger.error(String.format("Insufficent main coin for wallet %s and contract %s", from,
									contract));
							shouldBreak[0] = true;
						} else if (result.getError().getMessage()
								.contains("maximum number of enqueued transactions reached")) {
							throw new RuntimeException("maximum number of enqueued transactions reached");
						}
					}
				}
			} catch (Exception e) {
				// logger.error("Error is : " + e.getMessage());
				if (e.getMessage().contains("Cannot assign requested address"))
					try {
						Thread.sleep(waitOnMaxEnqueIsSeconds * 1000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						logger.error(e1.getMessage());
						;
					}
				else
					shouldBreak[0] = true;
			}
		}
	}

	@Transactional
	public void restartBlockchain(Blockchain blockchain) {

	}

}
