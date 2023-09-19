package com.plgchain.app.plingaHelper.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.tx.gas.DefaultGasProvider;

import com.netflix.servo.util.Strings;
import com.plgchain.app.plingaHelper.constant.WalletType;
import com.plgchain.app.plingaHelper.dto.EvmWalletDto;
import com.plgchain.app.plingaHelper.dto.MarketMakingWalletDto;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.entity.TempTankhahWallet;
import com.plgchain.app.plingaHelper.entity.coingecko.Coin;
import com.plgchain.app.plingaHelper.entity.marketMaking.MarketMaking;
import com.plgchain.app.plingaHelper.entity.marketMaking.MarketMakingWallet;
import com.plgchain.app.plingaHelper.exception.InvalidMarketMaking;
import com.plgchain.app.plingaHelper.microService.MMWalletMicroService;
import com.plgchain.app.plingaHelper.microService.MarketMakingMicroService;
import com.plgchain.app.plingaHelper.microService.MarketMakingWalletMicroService;
import com.plgchain.app.plingaHelper.microService.SmartContractMicroService;
import com.plgchain.app.plingaHelper.microService.TankhahWalletMicroService;
import com.plgchain.app.plingaHelper.microService.TempTankhahWalletMicroService;
import com.plgchain.app.plingaHelper.service.MMWalletService;
import com.plgchain.app.plingaHelper.util.NumberUtil;
import com.plgchain.app.plingaHelper.util.SecurityUtil;
import com.plgchain.app.plingaHelper.util.blockchain.EVMUtil;
import com.plgchain.app.plingaHelper.util.blockchain.EvmWalletUtil;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@Service
public class WalletActionBean implements Serializable {

	private static final long serialVersionUID = -8145289049266025010L;

	private static final Logger logger = LoggerFactory.getLogger(WalletActionBean.class);

	private final InitBean initBean;
	private final MarketMakingMicroService marketMakingMicroService;
	private final TransferBean transferBean;
	private final TankhahWalletMicroService tankhahWalletMicroService;
	private final MarketMakingWalletMicroService marketMakingWalletMicroService;
	private final SmartContractMicroService smartContractMicroService;
	private final BlockchainBean blockchainBean;
	private final TempTankhahWalletMicroService tempTankhahWalletMicroService;
	private final MMWalletService mmWalletService;

	@Inject
	public WalletActionBean(InitBean initBean, MarketMakingMicroService marketMakingMicroService,
			TransferBean transferBean, TankhahWalletMicroService tankhahWalletMicroService,
			MarketMakingWalletMicroService marketMakingWalletMicroService,
			SmartContractMicroService smartContractMicroService, BlockchainBean blockchainBean,
			MMWalletMicroService mmWalletMicroService, TempTankhahWalletMicroService tempTankhahWalletMicroService,
			MMWalletService mmWalletService) {
		this.initBean = initBean;
		this.marketMakingMicroService = marketMakingMicroService;
		this.transferBean = transferBean;
		this.tankhahWalletMicroService = tankhahWalletMicroService;
		this.marketMakingWalletMicroService = marketMakingWalletMicroService;
		this.smartContractMicroService = smartContractMicroService;
		this.blockchainBean = blockchainBean;
		this.tempTankhahWalletMicroService = tempTankhahWalletMicroService;
		this.mmWalletService = mmWalletService;
	}

	@Async
	@Transactional
	public void fixAllTransferWalletsByContractId(long contractId) {
		var sm = smartContractMicroService.findById(contractId).get();
		Blockchain blockchain = sm.getBlockchain();
		Coin coin = sm.getCoin();
		var tankhahWallet = tankhahWalletMicroService.findByContract(sm).get(0);
		var mm = marketMakingMicroService.findBySmartContract(sm).get();
		if (!(mm.isInitialWalletCreationDone() && mm.isInitialWalletFundingDone()))
			throw new InvalidMarketMaking();
		final int[] enqueued = { 0 };
		final BigInteger[] tankhahNonce = {
				EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(), tankhahWallet.getPrivateKeyHex()) };
		logger.info(String.format("Nonce for wallet %s of Contract address %s and coin %s and blockchain %s is %s",
				tankhahWallet.getPublicKey(), sm.getContractsAddress(), coin.getSymbol(), blockchain.getName(),
				tankhahNonce[0]));
		int page = 0;
		Page<MarketMakingWalletDto> mmWalletPageDto = null;
		do {
			PageRequest pageable = PageRequest.of(page, initBean.getFixTransferWalletBalancePerRound());
			marketMakingWalletMicroService
					.findAllWalletsByContractIdAndWalletTypeNativePaged(contractId, WalletType.TRANSFER, pageable)
					.stream().forEach(wallet -> {
						if (sm.getContractsAddress().equals(EVMUtil.mainToken)) {
							BigDecimal balance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(),
									wallet.getPublicKey());
							if (balance.compareTo(mm.getMaxInitial()) > 0) {
								var amount = NumberUtil.generateRandomNumber(mm.getMinInitial(), mm.getMaxInitial(),
										mm.getInitialDecimal());
								var mustReturn = balance.subtract(amount);
								BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(),
										wallet.getPrivateKeyHex());
								if (blockchain.isAutoGas()) {
									transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
											wallet.getPrivateKeyHex(), wallet.getPublicKey(),
											tankhahWallet.getPublicKey(), mustReturn, EVMUtil.DefaultGasLimit, nonce);
								} else {
									transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
											wallet.getPrivateKeyHex(), wallet.getPublicKey(),
											tankhahWallet.getPublicKey(), mustReturn, EVMUtil.DefaultGasPrice,
											EVMUtil.DefaultGasLimit, nonce);
								}
							} else if (balance.equals(BigDecimal.ZERO)) {
								var amount = NumberUtil.generateRandomNumber(initBean.getMinMaincoinInContractWallet(),
										initBean.getMaxMaincoinInContractWallet(),
										initBean.getDecimalMaincoinInContractWallet());
								if (blockchain.isAutoGas()) {
									transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
											tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
											wallet.getPublicKey(), amount, EVMUtil.DefaultGasLimit, tankhahNonce[0]);
								} else {
									transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
											tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
											wallet.getPublicKey(), amount, EVMUtil.DefaultGasPrice,
											EVMUtil.DefaultGasLimit, tankhahNonce[0]);
								}
								tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
								enqueued[0]++;
								if (enqueued[0] >= 100) {
									try {
										Thread.sleep(initBean.getSleepInSeconds() * 1000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									enqueued[0] = 0;
								}
							}
						} else {
							BigDecimal balance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(),
									wallet.getPublicKey());

							BigDecimal tokenBalance = EVMUtil.getTokenBalancSync(blockchain.getRpcUrl(),
									wallet.getPrivateKeyHex(), sm.getContractsAddress());
							if (balance.compareTo(initBean.getMaxMaincoinInContractWallet()) > 0) {
								var amount = NumberUtil.generateRandomNumber(initBean.getMinMaincoinInContractWallet(),
										initBean.getMaxMaincoinInContractWallet(),
										initBean.getDecimalMaincoinInContractWallet());
								var mustReturn = balance.subtract(amount);
								BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(),
										wallet.getPrivateKeyHex());
								if (blockchain.isAutoGas()) {
									transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
											wallet.getPrivateKeyHex(), wallet.getPublicKey(),
											tankhahWallet.getPublicKey(), mustReturn, EVMUtil.DefaultGasLimit, nonce);
								} else {
									transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
											wallet.getPrivateKeyHex(), wallet.getPublicKey(),
											tankhahWallet.getPublicKey(), mustReturn, EVMUtil.DefaultGasPrice,
											EVMUtil.DefaultGasLimit, nonce);
								}
							} else if (balance.equals(BigDecimal.ZERO)) {
								var amount = NumberUtil.generateRandomNumber(initBean.getMinMaincoinInContractWallet(),
										initBean.getMaxMaincoinInContractWallet(),
										initBean.getDecimalMaincoinInContractWallet());
								if (blockchain.isAutoGas()) {
									transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
											tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
											wallet.getPublicKey(), amount, EVMUtil.DefaultGasLimit, tankhahNonce[0]);
								} else {
									transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
											tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
											wallet.getPublicKey(), amount, EVMUtil.DefaultGasPrice,
											EVMUtil.DefaultGasLimit, tankhahNonce[0]);
								}
								tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
								enqueued[0]++;
								if (enqueued[0] >= 100) {
									try {
										Thread.sleep(initBean.getSleepInSeconds() * 1000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									enqueued[0] = 0;
								}
							}
							var amount = NumberUtil.generateRandomNumber(mm.getMinInitial(), mm.getMaxInitial(),
									mm.getInitialDecimal());
							if (tokenBalance.compareTo(mm.getMaxInitial()) > 0) {
								var mustReturn = tokenBalance.subtract(amount);
								BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(),
										wallet.getPrivateKeyHex());
								if (blockchain.isAutoGas()) {
									transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
											wallet.getPrivateKeyHex(), wallet.getPublicKey(),
											tankhahWallet.getPublicKey(), sm.getContractsAddress(), mustReturn,
											EVMUtil.DefaultTokenGasLimit, nonce);
								} else {
									transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
											wallet.getPrivateKeyHex(), wallet.getPublicKey(),
											tankhahWallet.getPublicKey(), sm.getContractsAddress(), mustReturn,
											EVMUtil.DefaultGasPrice, EVMUtil.DefaultTokenGasLimit, nonce);
								}
							} else if (tokenBalance.equals(BigDecimal.ZERO)) {
								if (blockchain.isAutoGas()) {
									transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
											tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
											wallet.getPublicKey(), sm.getContractsAddress(), amount,
											EVMUtil.DefaultTokenGasLimit, tankhahNonce[0]);
								} else {
									transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
											tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
											wallet.getPublicKey(), sm.getContractsAddress(), amount,
											EVMUtil.DefaultGasPrice, EVMUtil.DefaultTokenGasLimit, tankhahNonce[0]);
								}
								tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
								enqueued[0]++;
								if (enqueued[0] >= 100) {
									try {
										Thread.sleep(initBean.getSleepInSeconds() * 1000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									enqueued[0] = 0;
								}
							}
						}
					});
			page++;
		} while (mmWalletPageDto.hasNext());

	}

	@Async
	@Transactional
	public void fixAllTransferWalletsByContractIdInOneAction(long contractId) {
		logger.info(String.format("Try to fill contract %s", contractId));
		var sm = smartContractMicroService.findById(contractId).get();
		Blockchain blockchain = sm.getBlockchain();
		Coin coin = sm.getCoin();
		var tankhahWallet = tankhahWalletMicroService.findByContract(sm).get(0);
		var mm = marketMakingMicroService.findBySmartContract(sm).get();
		if (!mm.isInitialWalletCreationDone())
			throw new InvalidMarketMaking();
		final int[] enqueued = { 0 };
		final BigInteger[] tankhahNonce = {
				EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(), tankhahWallet.getPrivateKeyHex()) };
		logger.info(String.format("Nonce for wallet %s of Contract address %s and coin %s and blockchain %s is %s",
				tankhahWallet.getPublicKey(), sm.getContractsAddress(), coin.getSymbol(), blockchain.getName(),
				tankhahNonce[0]));
		marketMakingWalletMicroService.findAllWalletsByContractIdAndWalletTypeNative(contractId, WalletType.TRANSFER)
				.stream().forEach(wallet -> {
					if (sm.getContractsAddress().equals(EVMUtil.mainToken)) {
						BigDecimal balance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(), wallet.getPublicKey());
						if (balance.compareTo(mm.getMaxInitial()) > 0) {
							var amount = NumberUtil.generateRandomNumber(mm.getMinInitial(), mm.getMaxInitial(),
									mm.getInitialDecimal());
							var mustReturn = balance.subtract(amount);
							BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(),
									wallet.getPrivateKeyHex());
							if (blockchain.isAutoGas()) {
								transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
										wallet.getPrivateKeyHex(), wallet.getPublicKey(), tankhahWallet.getPublicKey(),
										mustReturn, EVMUtil.DefaultGasLimit, nonce);
							} else {
								transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
										wallet.getPrivateKeyHex(), wallet.getPublicKey(), tankhahWallet.getPublicKey(),
										mustReturn, EVMUtil.DefaultGasPrice, EVMUtil.DefaultGasLimit, nonce);
							}
						} else if (balance.equals(BigDecimal.ZERO)) {
							boolean mustRetry = true;
							while (mustRetry) {
								var amount = NumberUtil.generateRandomNumber(mm.getMinInitial(), mm.getMaxInitial(),
										mm.getInitialDecimal());
								if (blockchain.isAutoGas()) {
									try {
										transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
												tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
												wallet.getPublicKey(), amount, EVMUtil.DefaultGasLimit,
												tankhahNonce[0]);
										mustRetry = false;
									} catch (RuntimeException e) {
										if (e.getMessage().equals("maximum number of enqueued transactions reached")) {
											blockchainBean.stopAndStartMMNode(blockchain);
											// logger.info(String.format("Blockchain %s has been restarted for enqueued
											// transactions reached", blockchain.getName()));
											mustRetry = true;
										}
									}
								} else {
									try {
										transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
												tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
												wallet.getPublicKey(), amount, EVMUtil.DefaultGasPrice,
												EVMUtil.DefaultGasLimit, tankhahNonce[0]);
										mustRetry = false;
									} catch (RuntimeException e) {
										if (e.getMessage().equals("maximum number of enqueued transactions reached")) {
											blockchainBean.restartBlockchain(blockchain);
											mustRetry = true;
										}
									}
								}
								tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
								enqueued[0]++;
								if (enqueued[0] >= 100) {
									try {
										Thread.sleep(initBean.getSleepInSeconds() * 1000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									enqueued[0] = 0;
								}
							}
						}
					} else {
						BigDecimal balance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(), wallet.getPublicKey());

						BigDecimal tokenBalance = EVMUtil.getTokenBalancSync(blockchain.getRpcUrl(),
								wallet.getPrivateKeyHex(), sm.getContractsAddress());
						if (balance.compareTo(initBean.getMaxMaincoinInContractWallet()) > 0) {
							var amount = NumberUtil.generateRandomNumber(initBean.getMinMaincoinInContractWallet(),
									initBean.getMaxMaincoinInContractWallet(),
									initBean.getDecimalMaincoinInContractWallet());
							var mustReturn = balance.subtract(amount);
							BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(),
									wallet.getPrivateKeyHex());
							if (blockchain.isAutoGas()) {
								transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
										wallet.getPrivateKeyHex(), wallet.getPublicKey(), tankhahWallet.getPublicKey(),
										mustReturn, EVMUtil.DefaultGasLimit, nonce);
							} else {
								transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
										wallet.getPrivateKeyHex(), wallet.getPublicKey(), tankhahWallet.getPublicKey(),
										mustReturn, EVMUtil.DefaultGasPrice, EVMUtil.DefaultGasLimit, nonce);
							}
						} else if (balance.equals(BigDecimal.ZERO)) {
							boolean mustRetry = true;
							while (mustRetry) {
								var amount = NumberUtil.generateRandomNumber(initBean.getMinMaincoinInContractWallet(),
										initBean.getMaxMaincoinInContractWallet(),
										initBean.getDecimalMaincoinInContractWallet());
								if (blockchain.isAutoGas()) {
									try {
										transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
												tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
												wallet.getPublicKey(), amount, EVMUtil.DefaultGasLimit,
												tankhahNonce[0]);
										mustRetry = false;
									} catch (RuntimeException e) {
										if (e.getMessage().equals("maximum number of enqueued transactions reached")) {
											blockchainBean.stopAndStartMMNode(blockchain);
											mustRetry = true;
										}
									}
								} else {
									try {
										transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
												tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
												wallet.getPublicKey(), amount, EVMUtil.DefaultGasPrice,
												EVMUtil.DefaultGasLimit, tankhahNonce[0]);
										mustRetry = false;
									} catch (RuntimeException e) {
										if (e.getMessage().equals("maximum number of enqueued transactions reached")) {
											blockchainBean.restartBlockchain(blockchain);
											mustRetry = true;
										}
									}
								}
								tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
								enqueued[0]++;
								if (enqueued[0] >= 100) {
									try {
										Thread.sleep(initBean.getSleepInSeconds() * 1000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									enqueued[0] = 0;
								}
							}
						}
						var amount = NumberUtil.generateRandomNumber(mm.getMinInitial(), mm.getMaxInitial(),
								mm.getInitialDecimal());
						if (tokenBalance.compareTo(mm.getMaxInitial()) > 0) {
							var mustReturn = tokenBalance.subtract(amount);
							BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(),
									wallet.getPrivateKeyHex());
							if (blockchain.isAutoGas()) {
								transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
										wallet.getPrivateKeyHex(), wallet.getPublicKey(), tankhahWallet.getPublicKey(),
										sm.getContractsAddress(), mustReturn, EVMUtil.DefaultTokenGasLimit, nonce);
							} else {
								transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
										wallet.getPrivateKeyHex(), wallet.getPublicKey(), tankhahWallet.getPublicKey(),
										sm.getContractsAddress(), mustReturn, EVMUtil.DefaultGasPrice,
										EVMUtil.DefaultTokenGasLimit, nonce);
							}
						} else if (tokenBalance.equals(BigDecimal.ZERO)) {
							boolean mustRetry = true;
							while (mustRetry) {
								if (blockchain.isAutoGas()) {
									try {
										transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
												tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
												wallet.getPublicKey(), sm.getContractsAddress(), amount,
												EVMUtil.DefaultTokenGasLimit, tankhahNonce[0]);
										mustRetry = false;
									} catch (RuntimeException e) {
										if (e.getMessage().equals("maximum number of enqueued transactions reached")) {
											blockchainBean.stopAndStartMMNode(blockchain);
											mustRetry = true;
										}
									}
								} else {
									try {
										transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
												tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
												wallet.getPublicKey(), sm.getContractsAddress(), amount,
												EVMUtil.DefaultGasPrice, EVMUtil.DefaultTokenGasLimit, tankhahNonce[0]);
										mustRetry = false;
									} catch (RuntimeException e) {
										if (e.getMessage().equals("maximum number of enqueued transactions reached")) {
											blockchainBean.restartBlockchain(blockchain);
											mustRetry = true;
										}
									}
								}
								tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
								enqueued[0]++;
								if (enqueued[0] >= 100) {
									try {
										Thread.sleep(initBean.getSleepInSeconds() * 1000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									enqueued[0] = 0;
								}
							}
						}
					}
				});

	}

	@Async
	@Transactional
	public void fixAllTransferWalletsByContractIdInOneActionWithTempTankhahReverse(long contractId) {
		logger.info(String.format("Try to fill contract %s", contractId));
		var sm = smartContractMicroService.findById(contractId).get();
		Blockchain blockchain = sm.getBlockchain();
		Coin coin = sm.getCoin();
		var tankhahWallet = tankhahWalletMicroService.findByContract(sm).get(0);
		var mm = marketMakingMicroService.findBySmartContract(sm).get();
		if (!mm.isInitialWalletCreationDone())
			throw new InvalidMarketMaking();
		final BigInteger[] tankhahNonce = {
				EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(), tankhahWallet.getPrivateKeyHex()) };
		logger.info(String.format("Nonce for wallet %s of Contract address %s and coin %s and blockchain %s is %s",
				tankhahWallet.getPublicKey(), sm.getContractsAddress(), coin.getSymbol(), blockchain.getName(),
				tankhahNonce[0]));
		IntStream.rangeClosed(0, mm.getChunkCount()).mapToObj(i -> mm.getChunkCount() - i).forEach(idx -> {
			int[] count = { 0 };
			mmWalletService.findByContractIdAndChunk(contractId, idx).ifPresent(mmw -> {
				mmw.getTransferWalletList().forEach(wallet -> {
					wallet.setPrivateKeyHex(SecurityUtil.decryptString(wallet.getEncryptedPrivateKey(), mm.getTrPid()));
					if (sm.getContractsAddress().equals(EVMUtil.mainToken)) {
						BigDecimal balance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(), wallet.getPublicKey());
						if (balance.compareTo(mm.getMaxInitial()) > 0) {
							var amount = NumberUtil.generateRandomNumber(mm.getMinInitial(), mm.getMaxInitial(),
									mm.getInitialDecimal());
							var mustReturn = balance.subtract(amount);
							BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(),
									wallet.getPrivateKeyHex());
							if (blockchain.isAutoGas()) {
								transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
										wallet.getPrivateKeyHex(), wallet.getPublicKey(), tankhahWallet.getPublicKey(),
										mustReturn, EVMUtil.DefaultGasLimit, nonce);
							} else {
								transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
										wallet.getPrivateKeyHex(), wallet.getPublicKey(), tankhahWallet.getPublicKey(),
										mustReturn, EVMUtil.DefaultGasPrice, EVMUtil.DefaultGasLimit, nonce);
							}
						} else if (balance.equals(BigDecimal.ZERO)) {
							boolean mustRetry = true;
							while (mustRetry) {
								var amount = NumberUtil.generateRandomNumber(mm.getMinInitial(), mm.getMaxInitial(),
										mm.getInitialDecimal());
								EvmWalletDto tmpTankhah = initBean.getRandomTmpTankhahWallet(sm);
								BigDecimal tmpTankhahBalance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(),
										tmpTankhah.getPublicKey());
								if (tmpTankhahBalance.compareTo(amount) < 0) {
									if (blockchain.isAutoGas()) {
										try {
											transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
													tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
													tmpTankhah.getPublicKey(),
													amount.multiply(
															new BigDecimal(initBean.getTmpTankhahWalletCount())),
													EVMUtil.DefaultGasLimit, tankhahNonce[0]);
											mustRetry = false;
										} catch (RuntimeException e) {
											if (e.getMessage()
													.equals("maximum number of enqueued transactions reached")) {
												blockchainBean.stopAndStartMMNode(blockchain);
												// logger.info(String.format("Blockchain %s has been restarted for
												// enqueued
												// transactions reached", blockchain.getName()));
												mustRetry = true;
											}
										}
									} else {
										try {
											transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
													tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
													tmpTankhah.getPublicKey(),
													amount.multiply(
															new BigDecimal(initBean.getTmpTankhahWalletCount())),
													EVMUtil.DefaultGasPrice, EVMUtil.DefaultGasLimit, tankhahNonce[0]);
											mustRetry = false;
										} catch (RuntimeException e) {
											if (e.getMessage()
													.equals("maximum number of enqueued transactions reached")) {
												blockchainBean.restartBlockchain(blockchain);
												mustRetry = true;
											}
										}
									}
									tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
								} else {
									BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(),
											tmpTankhah.getPrivateKey());
									if (blockchain.isAutoGas()) {
										try {
											transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
													tmpTankhah.getPrivateKey(), tmpTankhah.getPublicKey(),
													wallet.getPublicKey(), amount, EVMUtil.DefaultGasLimit, nonce);
											mustRetry = false;
										} catch (RuntimeException e) {
											if (e.getMessage()
													.equals("maximum number of enqueued transactions reached")) {
												blockchainBean.stopAndStartMMNode(blockchain);
												// logger.info(String.format("Blockchain %s has been restarted for
												// enqueued
												// transactions reached", blockchain.getName()));
												mustRetry = true;
											}
										}
									} else {
										try {
											transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
													tmpTankhah.getPrivateKey(), tmpTankhah.getPublicKey(),
													wallet.getPublicKey(), amount, EVMUtil.DefaultGasPrice,
													EVMUtil.DefaultGasLimit, nonce);
											mustRetry = false;
										} catch (RuntimeException e) {
											if (e.getMessage()
													.equals("maximum number of enqueued transactions reached")) {
												blockchainBean.restartBlockchain(blockchain);
												mustRetry = true;
											}
										}
									}
									initBean.setNonceOfTmpTankhahWallet(sm, tmpTankhah,
											tmpTankhah.getNonce().add(BigInteger.ONE));
								}
							}
						}
					} else {
						BigDecimal balance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(), wallet.getPublicKey());

						BigDecimal tokenBalance = EVMUtil.getTokenBalancSync(blockchain.getRpcUrl(),
								wallet.getPrivateKeyHex(), sm.getContractsAddress());
						if (balance.compareTo(initBean.getMaxMaincoinInContractWallet()) > 0) {
							var amount = NumberUtil.generateRandomNumber(initBean.getMinMaincoinInContractWallet(),
									initBean.getMaxMaincoinInContractWallet(),
									initBean.getDecimalMaincoinInContractWallet());
							var mustReturn = balance.subtract(amount);
							BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(),
									wallet.getPrivateKeyHex());
							if (blockchain.isAutoGas()) {
								transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
										wallet.getPrivateKeyHex(), wallet.getPublicKey(), tankhahWallet.getPublicKey(),
										mustReturn, EVMUtil.DefaultGasLimit, nonce);
							} else {
								transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
										wallet.getPrivateKeyHex(), wallet.getPublicKey(), tankhahWallet.getPublicKey(),
										mustReturn, EVMUtil.DefaultGasPrice, EVMUtil.DefaultGasLimit, nonce);
							}
						} else if (balance.equals(BigDecimal.ZERO)) {
							boolean mustRetry = true;
							while (mustRetry) {
								EvmWalletDto tmpTankhah = initBean.getRandomTmpTankhahWallet(sm);
								var amount = NumberUtil.generateRandomNumber(initBean.getMinMaincoinInContractWallet(),
										initBean.getMaxMaincoinInContractWallet(),
										initBean.getDecimalMaincoinInContractWallet());
								BigDecimal tmpTankhahBalance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(),
										tmpTankhah.getPublicKey());
								if (tmpTankhahBalance.compareTo(amount) < 0) {
									BigDecimal tankhahBalance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(),
											tankhahWallet.getPublicKey());
									if (tankhahBalance.compareTo(amount
											.multiply(new BigDecimal(initBean.getTmpTankhahWalletCount()))) >= 0) {
										if (blockchain.isAutoGas()) {
											try {
												transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
														tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
														tmpTankhah.getPublicKey(),
														amount.multiply(
																new BigDecimal(initBean.getTmpTankhahWalletCount())),
														EVMUtil.DefaultGasLimit, tankhahNonce[0]);
												mustRetry = false;
											} catch (RuntimeException e) {
												if (e.getMessage()
														.equals("maximum number of enqueued transactions reached")) {
													blockchainBean.stopAndStartMMNode(blockchain);
													mustRetry = false;
												}
											}
										} else {
											try {
												transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
														tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
														tmpTankhah.getPublicKey(),
														amount.multiply(
																new BigDecimal(initBean.getTmpTankhahWalletCount())),
														EVMUtil.DefaultGasPrice, EVMUtil.DefaultGasLimit,
														tankhahNonce[0]);
												mustRetry = false;
											} catch (RuntimeException e) {
												if (e.getMessage()
														.equals("maximum number of enqueued transactions reached")) {
													blockchainBean.restartBlockchain(blockchain);
													mustRetry = false;
												}
											}
										}
										tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
									} else {
										logger.info("Tankhah does not enough balance skip it.");
									}
								} else {
									BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(),
											tmpTankhah.getPrivateKey());
									if (blockchain.isAutoGas()) {
										try {
											transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
													tmpTankhah.getPrivateKey(), tmpTankhah.getPublicKey(),
													wallet.getPublicKey(), amount, EVMUtil.DefaultGasLimit, nonce);
											mustRetry = false;
										} catch (RuntimeException e) {
											if (e.getMessage()
													.equals("maximum number of enqueued transactions reached")) {
												blockchainBean.stopAndStartMMNode(blockchain);
												mustRetry = true;
											}
										}
									} else {
										try {
											transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
													tmpTankhah.getPrivateKey(), tmpTankhah.getPublicKey(),
													wallet.getPublicKey(), amount, EVMUtil.DefaultGasPrice,
													EVMUtil.DefaultGasLimit, nonce);
											mustRetry = false;
										} catch (RuntimeException e) {
											if (e.getMessage()
													.equals("maximum number of enqueued transactions reached")) {
												blockchainBean.restartBlockchain(blockchain);
												mustRetry = true;
											}
										}
									}
									initBean.setNonceOfTmpTankhahWallet(sm, tmpTankhah,
											tmpTankhah.getNonce().add(BigInteger.ONE));
								}
							}
						} else {
							logger.info(
									String.format(" wallet %s has enogh main coin balance with chunk %s and count %s",
											wallet.getPublicKey(), idx, count[0]));
						}
						var amount = NumberUtil.generateRandomNumber(mm.getMinInitial(), mm.getMaxInitial(),
								mm.getInitialDecimal());
						if (tokenBalance.compareTo(mm.getMaxInitial()) > 0) {
							var mustReturn = tokenBalance.subtract(amount);
							BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(),
									wallet.getPrivateKeyHex());
							if (blockchain.isAutoGas()) {
								transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
										wallet.getPrivateKeyHex(), wallet.getPublicKey(), tankhahWallet.getPublicKey(),
										sm.getContractsAddress(), mustReturn, EVMUtil.DefaultTokenGasLimit, nonce);
							} else {
								transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
										wallet.getPrivateKeyHex(), wallet.getPublicKey(), tankhahWallet.getPublicKey(),
										sm.getContractsAddress(), mustReturn, EVMUtil.DefaultGasPrice,
										EVMUtil.DefaultTokenGasLimit, nonce);
							}
						} else if (tokenBalance.equals(BigDecimal.ZERO)) {
							boolean mustRetry = true;
							while (mustRetry) {
								EvmWalletDto tmpTankhah = initBean.getRandomTmpTankhahWallet(sm);
								BigDecimal tmpTankhahTokenBalance = EVMUtil.getTokenBalancSync(blockchain.getRpcUrl(),
										tmpTankhah.getPrivateKey(), sm.getContractsAddress());
								if (tmpTankhahTokenBalance.compareTo(amount) < 0) {
									BigDecimal tankhahBalance = EVMUtil.getTokenBalancSync(blockchain.getRpcUrl(),
											tankhahWallet.getPrivateKeyHex(), sm.getContractsAddress());
									if (tankhahBalance.compareTo(amount
											.multiply(new BigDecimal(initBean.getTmpTankhahWalletCount()))) >= 0) {
										if (blockchain.isAutoGas()) {
											try {
												transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
														tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
														tmpTankhah.getPublicKey(), sm.getContractsAddress(),
														amount.multiply(
																new BigDecimal(initBean.getTmpTankhahWalletCount())),
														EVMUtil.DefaultTokenGasLimit, tankhahNonce[0]);
												mustRetry = false;
											} catch (RuntimeException e) {
												if (e.getMessage()
														.equals("maximum number of enqueued transactions reached")) {
													blockchainBean.stopAndStartMMNode(blockchain);
													mustRetry = false;
												}
											}
										} else {
											try {
												transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
														tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
														tmpTankhah.getPublicKey(), sm.getContractsAddress(),
														amount.multiply(
																new BigDecimal(initBean.getTmpTankhahWalletCount())),
														EVMUtil.DefaultGasPrice, EVMUtil.DefaultTokenGasLimit,
														tankhahNonce[0]);
												mustRetry = false;
											} catch (RuntimeException e) {
												if (e.getMessage()
														.equals("maximum number of enqueued transactions reached")) {
													blockchainBean.restartBlockchain(blockchain);
													mustRetry = false;
												}
											}
										}
										tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
									}
								} else {
									BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(),
											tmpTankhah.getPrivateKey());
									if (blockchain.isAutoGas()) {
										try {
											transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
													tmpTankhah.getPrivateKey(), tmpTankhah.getPublicKey(),
													wallet.getPublicKey(), sm.getContractsAddress(), amount,
													EVMUtil.DefaultTokenGasLimit, nonce);
											mustRetry = false;
										} catch (RuntimeException e) {
											if (e.getMessage()
													.equals("maximum number of enqueued transactions reached")) {
												blockchainBean.stopAndStartMMNode(blockchain);
												mustRetry = true;
											}
										}
									} else {
										try {
											transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
													tmpTankhah.getPrivateKey(), tmpTankhah.getPublicKey(),
													wallet.getPublicKey(), sm.getContractsAddress(), amount,
													EVMUtil.DefaultGasPrice, EVMUtil.DefaultTokenGasLimit, nonce);
											mustRetry = false;
										} catch (RuntimeException e) {
											if (e.getMessage()
													.equals("maximum number of enqueued transactions reached")) {
												blockchainBean.restartBlockchain(blockchain);
												mustRetry = true;
											}
										}
									}
									initBean.setNonceOfTmpTankhahWallet(sm, tmpTankhah,
											tmpTankhah.getNonce().add(BigInteger.ONE));
								}
							}
						} else {
							logger.info(String.format(
									" wallet %s has enogh main token %s balance and chunk %s and count %s",
									wallet.getPublicKey(), sm.getContractsAddress(), idx, count[0]));
						}
					}
				});
			});
		});
	}

	@Async
	@Transactional
	public void fixAllTransferWalletsByContractIdInOneActionWithTempTankhah(long contractId) {
		logger.info(String.format("Try to fill contract %s", contractId));
		var sm = smartContractMicroService.findById(contractId).get();
		Blockchain blockchain = sm.getBlockchain();
		Coin coin = sm.getCoin();
		var tankhahWallet = tankhahWalletMicroService.findByContract(sm).get(0);
		var mm = marketMakingMicroService.findBySmartContract(sm).get();
		if (!mm.isInitialWalletCreationDone())
			throw new InvalidMarketMaking();
		final BigInteger[] tankhahNonce = {
				EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(), tankhahWallet.getPrivateKeyHex()) };
		logger.info(String.format("Nonce for wallet %s of Contract address %s and coin %s and blockchain %s is %s",
				tankhahWallet.getPublicKey(), sm.getContractsAddress(), coin.getSymbol(), blockchain.getName(),
				tankhahNonce[0]));
		IntStream.range(0, mm.getChunkCount()).forEach(idx -> {
			int count = 1;
			mmWalletService.findByContractIdAndChunk(contractId, idx).ifPresent(mmw -> {
				mmw.getTransferWalletList().forEach(wallet -> {
					wallet.setPrivateKeyHex(SecurityUtil.decryptString(wallet.getEncryptedPrivateKey(), mm.getTrPid()));
					if (sm.getContractsAddress().equals(EVMUtil.mainToken)) {
						BigDecimal balance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(), wallet.getPublicKey());
						if (balance.compareTo(mm.getMaxInitial()) > 0) {
							var amount = NumberUtil.generateRandomNumber(mm.getMinInitial(), mm.getMaxInitial(),
									mm.getInitialDecimal());
							var mustReturn = balance.subtract(amount);
							BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(),
									wallet.getPrivateKeyHex());
							if (blockchain.isAutoGas()) {
								transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
										wallet.getPrivateKeyHex(), wallet.getPublicKey(), tankhahWallet.getPublicKey(),
										mustReturn, EVMUtil.DefaultGasLimit, nonce);
							} else {
								transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
										wallet.getPrivateKeyHex(), wallet.getPublicKey(), tankhahWallet.getPublicKey(),
										mustReturn, EVMUtil.DefaultGasPrice, EVMUtil.DefaultGasLimit, nonce);
							}
						} else if (balance.equals(BigDecimal.ZERO)) {
							boolean mustRetry = true;
							while (mustRetry) {
								var amount = NumberUtil.generateRandomNumber(mm.getMinInitial(), mm.getMaxInitial(),
										mm.getInitialDecimal());
								EvmWalletDto tmpTankhah = initBean.getRandomTmpTankhahWallet(sm);
								BigDecimal tmpTankhahBalance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(),
										tmpTankhah.getPublicKey());
								if (tmpTankhahBalance.compareTo(amount) < 0) {
									if (blockchain.isAutoGas()) {
										try {
											transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
													tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
													tmpTankhah.getPublicKey(),
													amount.multiply(
															new BigDecimal(initBean.getTmpTankhahWalletCount())),
													EVMUtil.DefaultGasLimit, tankhahNonce[0]);
											mustRetry = false;
										} catch (RuntimeException e) {
											if (e.getMessage()
													.equals("maximum number of enqueued transactions reached")) {
												blockchainBean.stopAndStartMMNode(blockchain);
												// logger.info(String.format("Blockchain %s has been restarted for
												// enqueued
												// transactions reached", blockchain.getName()));
												mustRetry = true;
											}
										}
									} else {
										try {
											transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
													tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
													tmpTankhah.getPublicKey(),
													amount.multiply(
															new BigDecimal(initBean.getTmpTankhahWalletCount())),
													EVMUtil.DefaultGasPrice, EVMUtil.DefaultGasLimit, tankhahNonce[0]);
											mustRetry = false;
										} catch (RuntimeException e) {
											if (e.getMessage()
													.equals("maximum number of enqueued transactions reached")) {
												blockchainBean.restartBlockchain(blockchain);
												mustRetry = true;
											}
										}
									}
									tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
								} else {
									BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(),
											tmpTankhah.getPrivateKey());
									if (blockchain.isAutoGas()) {
										try {
											transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
													tmpTankhah.getPrivateKey(), tmpTankhah.getPublicKey(),
													wallet.getPublicKey(), amount, EVMUtil.DefaultGasLimit, nonce);
											mustRetry = false;
										} catch (RuntimeException e) {
											if (e.getMessage()
													.equals("maximum number of enqueued transactions reached")) {
												blockchainBean.stopAndStartMMNode(blockchain);
												// logger.info(String.format("Blockchain %s has been restarted for
												// enqueued
												// transactions reached", blockchain.getName()));
												mustRetry = true;
											}
										}
									} else {
										try {
											transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
													tmpTankhah.getPrivateKey(), tmpTankhah.getPublicKey(),
													wallet.getPublicKey(), amount, EVMUtil.DefaultGasPrice,
													EVMUtil.DefaultGasLimit, nonce);
											mustRetry = false;
										} catch (RuntimeException e) {
											if (e.getMessage()
													.equals("maximum number of enqueued transactions reached")) {
												blockchainBean.restartBlockchain(blockchain);
												mustRetry = true;
											}
										}
									}
									initBean.setNonceOfTmpTankhahWallet(sm, tmpTankhah,
											tmpTankhah.getNonce().add(BigInteger.ONE));
								}
							}
						}
					} else {
						BigDecimal balance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(), wallet.getPublicKey());

						BigDecimal tokenBalance = EVMUtil.getTokenBalancSync(blockchain.getRpcUrl(),
								wallet.getPrivateKeyHex(), sm.getContractsAddress());
						if (balance.compareTo(initBean.getMaxMaincoinInContractWallet()) > 0) {
							var amount = NumberUtil.generateRandomNumber(initBean.getMinMaincoinInContractWallet(),
									initBean.getMaxMaincoinInContractWallet(),
									initBean.getDecimalMaincoinInContractWallet());
							var mustReturn = balance.subtract(amount);
							BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(),
									wallet.getPrivateKeyHex());
							if (blockchain.isAutoGas()) {
								transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
										wallet.getPrivateKeyHex(), wallet.getPublicKey(), tankhahWallet.getPublicKey(),
										mustReturn, EVMUtil.DefaultGasLimit, nonce);
							} else {
								transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
										wallet.getPrivateKeyHex(), wallet.getPublicKey(), tankhahWallet.getPublicKey(),
										mustReturn, EVMUtil.DefaultGasPrice, EVMUtil.DefaultGasLimit, nonce);
							}
						} else if (balance.equals(BigDecimal.ZERO)) {
							boolean mustRetry = true;
							while (mustRetry) {
								EvmWalletDto tmpTankhah = initBean.getRandomTmpTankhahWallet(sm);
								var amount = NumberUtil.generateRandomNumber(initBean.getMinMaincoinInContractWallet(),
										initBean.getMaxMaincoinInContractWallet(),
										initBean.getDecimalMaincoinInContractWallet());
								BigDecimal tmpTankhahBalance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(),
										tmpTankhah.getPublicKey());
								if (tmpTankhahBalance.compareTo(amount) < 0) {
									BigDecimal tankhahBalance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(),
											tankhahWallet.getPublicKey());
									if (tankhahBalance.compareTo(amount
											.multiply(new BigDecimal(initBean.getTmpTankhahWalletCount()))) >= 0) {
										if (blockchain.isAutoGas()) {
											try {
												transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
														tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
														tmpTankhah.getPublicKey(),
														amount.multiply(
																new BigDecimal(initBean.getTmpTankhahWalletCount())),
														EVMUtil.DefaultGasLimit, tankhahNonce[0]);
												mustRetry = false;
											} catch (RuntimeException e) {
												if (e.getMessage()
														.equals("maximum number of enqueued transactions reached")) {
													blockchainBean.stopAndStartMMNode(blockchain);
													mustRetry = false;
												}
											}
										} else {
											try {
												transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
														tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
														tmpTankhah.getPublicKey(),
														amount.multiply(
																new BigDecimal(initBean.getTmpTankhahWalletCount())),
														EVMUtil.DefaultGasPrice, EVMUtil.DefaultGasLimit,
														tankhahNonce[0]);
												mustRetry = false;
											} catch (RuntimeException e) {
												if (e.getMessage()
														.equals("maximum number of enqueued transactions reached")) {
													blockchainBean.restartBlockchain(blockchain);
													mustRetry = false;
												}
											}
										}
										tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
									} else {
										logger.info("Tankhah does not enough balance skip it.");
									}
								} else {
									BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(),
											tmpTankhah.getPrivateKey());
									if (blockchain.isAutoGas()) {
										try {
											transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
													tmpTankhah.getPrivateKey(), tmpTankhah.getPublicKey(),
													wallet.getPublicKey(), amount, EVMUtil.DefaultGasLimit, nonce);
											mustRetry = false;
										} catch (RuntimeException e) {
											if (e.getMessage()
													.equals("maximum number of enqueued transactions reached")) {
												blockchainBean.stopAndStartMMNode(blockchain);
												mustRetry = true;
											}
										}
									} else {
										try {
											transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
													tmpTankhah.getPrivateKey(), tmpTankhah.getPublicKey(),
													wallet.getPublicKey(), amount, EVMUtil.DefaultGasPrice,
													EVMUtil.DefaultGasLimit, nonce);
											mustRetry = false;
										} catch (RuntimeException e) {
											if (e.getMessage()
													.equals("maximum number of enqueued transactions reached")) {
												blockchainBean.restartBlockchain(blockchain);
												mustRetry = true;
											}
										}
									}
									initBean.setNonceOfTmpTankhahWallet(sm, tmpTankhah,
											tmpTankhah.getNonce().add(BigInteger.ONE));
								}
							}
						} else {
							logger.info(
									String.format(" wallet %s has enogh main coin balance with chunk %s and count %s",
											wallet.getPublicKey(), idx, count));
						}
						var amount = NumberUtil.generateRandomNumber(mm.getMinInitial(), mm.getMaxInitial(),
								mm.getInitialDecimal());
						if (tokenBalance.compareTo(mm.getMaxInitial()) > 0) {
							var mustReturn = tokenBalance.subtract(amount);
							BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(),
									wallet.getPrivateKeyHex());
							if (blockchain.isAutoGas()) {
								transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
										wallet.getPrivateKeyHex(), wallet.getPublicKey(), tankhahWallet.getPublicKey(),
										sm.getContractsAddress(), mustReturn, EVMUtil.DefaultTokenGasLimit, nonce);
							} else {
								transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
										wallet.getPrivateKeyHex(), wallet.getPublicKey(), tankhahWallet.getPublicKey(),
										sm.getContractsAddress(), mustReturn, EVMUtil.DefaultGasPrice,
										EVMUtil.DefaultTokenGasLimit, nonce);
							}
						} else if (tokenBalance.equals(BigDecimal.ZERO)) {
							boolean mustRetry = true;
							while (mustRetry) {
								EvmWalletDto tmpTankhah = initBean.getRandomTmpTankhahWallet(sm);
								BigDecimal tmpTankhahTokenBalance = EVMUtil.getTokenBalancSync(blockchain.getRpcUrl(),
										tmpTankhah.getPrivateKey(), sm.getContractsAddress());
								if (tmpTankhahTokenBalance.compareTo(amount) < 0) {
									BigDecimal tankhahBalance = EVMUtil.getTokenBalancSync(blockchain.getRpcUrl(),
											tankhahWallet.getPrivateKeyHex(), sm.getContractsAddress());
									if (tankhahBalance.compareTo(amount
											.multiply(new BigDecimal(initBean.getTmpTankhahWalletCount()))) >= 0) {
										if (blockchain.isAutoGas()) {
											try {
												transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
														tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
														tmpTankhah.getPublicKey(), sm.getContractsAddress(),
														amount.multiply(
																new BigDecimal(initBean.getTmpTankhahWalletCount())),
														EVMUtil.DefaultTokenGasLimit, tankhahNonce[0]);
												mustRetry = false;
											} catch (RuntimeException e) {
												if (e.getMessage()
														.equals("maximum number of enqueued transactions reached")) {
													blockchainBean.stopAndStartMMNode(blockchain);
													mustRetry = false;
												}
											}
										} else {
											try {
												transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
														tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
														tmpTankhah.getPublicKey(), sm.getContractsAddress(),
														amount.multiply(
																new BigDecimal(initBean.getTmpTankhahWalletCount())),
														EVMUtil.DefaultGasPrice, EVMUtil.DefaultTokenGasLimit,
														tankhahNonce[0]);
												mustRetry = false;
											} catch (RuntimeException e) {
												if (e.getMessage()
														.equals("maximum number of enqueued transactions reached")) {
													blockchainBean.restartBlockchain(blockchain);
													mustRetry = false;
												}
											}
										}
										tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
									}
								} else {
									BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(),
											tmpTankhah.getPrivateKey());
									if (blockchain.isAutoGas()) {
										try {
											transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
													tmpTankhah.getPrivateKey(), tmpTankhah.getPublicKey(),
													wallet.getPublicKey(), sm.getContractsAddress(), amount,
													EVMUtil.DefaultTokenGasLimit, nonce);
											mustRetry = false;
										} catch (RuntimeException e) {
											if (e.getMessage()
													.equals("maximum number of enqueued transactions reached")) {
												blockchainBean.stopAndStartMMNode(blockchain);
												mustRetry = true;
											}
										}
									} else {
										try {
											transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
													tmpTankhah.getPrivateKey(), tmpTankhah.getPublicKey(),
													wallet.getPublicKey(), sm.getContractsAddress(), amount,
													EVMUtil.DefaultGasPrice, EVMUtil.DefaultTokenGasLimit, nonce);
											mustRetry = false;
										} catch (RuntimeException e) {
											if (e.getMessage()
													.equals("maximum number of enqueued transactions reached")) {
												blockchainBean.restartBlockchain(blockchain);
												mustRetry = true;
											}
										}
									}
									initBean.setNonceOfTmpTankhahWallet(sm, tmpTankhah,
											tmpTankhah.getNonce().add(BigInteger.ONE));
								}
							}
						} else {
							logger.info(String.format(
									" wallet %s has enogh main token %s balance and chunk %s and count %s",
									wallet.getPublicKey(), sm.getContractsAddress(), idx, count));
						}
					}
				});
			});
		});
	}

	@Async
	@Transactional
	public void backAllTokenToTankhah(long contractId) {
		List<MarketMakingWalletDto> tmpWalletList = new ArrayList<MarketMakingWalletDto>();
		logger.info(String.format("Try to back all tokens to tankhah for contract %s", contractId));
		var sm = smartContractMicroService.findById(contractId).get();
		Blockchain blockchain = sm.getBlockchain();
		Coin coin = sm.getCoin();
		var tankhahWallet = tankhahWalletMicroService.findByContract(sm).get(0);
		// var mm = marketMakingMicroService.findBySmartContract(sm).get();
		final BigInteger[] tankhahNonce = {
				EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(), tankhahWallet.getPrivateKeyHex()) };
		logger.info(String.format("Nonce for wallet %s of Contract address %s and coin %s and blockchain %s is %s",
				tankhahWallet.getPublicKey(), sm.getContractsAddress(), coin.getSymbol(), blockchain.getName(),
				tankhahNonce[0]));
		marketMakingMicroService.findBySmartContract(sm).ifPresent(mm -> {
			final int[] count = { 0 };
			IntStream.range(0, mm.getChunkCount()).forEach(idx -> {
				mmWalletService.findByContractIdAndChunk(contractId, idx).ifPresent(mmw -> {
					mmw.getTransferWalletList().forEach(wallet -> {
						wallet.setPrivateKeyHex(
								SecurityUtil.decryptString(wallet.getEncryptedPrivateKey(), mm.getTrPid()));
						EVMUtil.getAccountBalance(blockchain.getRpcUrl(), wallet.getPublicKey());

						BigDecimal tokenBalance = EVMUtil.getTokenBalancSync(blockchain.getRpcUrl(),
								wallet.getPrivateKeyHex(), sm.getContractsAddress());
						BigDecimal mainCoinBalance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(),
								wallet.getPublicKey());
						if (tokenBalance.compareTo(BigDecimal.ZERO) > 0) {
							if (mainCoinBalance.compareTo(new BigDecimal("0.1")) < 0) {
								var amount = new BigDecimal("0.1");
								if (blockchain.isAutoGas()) {
									transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
											tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
											wallet.getPublicKey(), amount, EVMUtil.DefaultGasLimit, tankhahNonce[0]);
								} else {
									transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
											tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
											wallet.getPublicKey(), amount, EVMUtil.DefaultGasPrice,
											EVMUtil.DefaultGasLimit, tankhahNonce[0]);

								}
								tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
								tmpWalletList.add(wallet);
							} else {
								BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(),
										wallet.getPrivateKeyHex());
								if (blockchain.isAutoGas()) {
									transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
											wallet.getPrivateKeyHex(), wallet.getPublicKey(),
											tankhahWallet.getPublicKey(), sm.getContractsAddress(), tokenBalance,
											EVMUtil.DefaultTokenGasLimit, nonce);
								} else {
									transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
											wallet.getPrivateKeyHex(), wallet.getPublicKey(),
											tankhahWallet.getPublicKey(), sm.getContractsAddress(), tokenBalance,
											EVMUtil.DefaultGasPrice, EVMUtil.DefaultTokenGasLimit, nonce);
								}
							}
						} else {
							logger.info(String.format("Wallet %s/%s at chunk %s has balance 0 and skip",
									wallet.getPublicKey(), count[0], idx));
							count[0]++;
						}
					});
				});
			});
		});
		tmpWalletList.forEach(wallet -> {
			BigDecimal tokenBalance = EVMUtil.getTokenBalancSync(blockchain.getRpcUrl(), wallet.getPrivateKeyHex(),
					sm.getContractsAddress());
			BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(), wallet.getPrivateKeyHex());
			if (blockchain.isAutoGas()) {
				transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(), wallet.getPrivateKeyHex(),
						wallet.getPublicKey(), tankhahWallet.getPublicKey(), sm.getContractsAddress(), tokenBalance,
						EVMUtil.DefaultTokenGasLimit, nonce);
			} else {
				transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(), wallet.getPrivateKeyHex(),
						wallet.getPublicKey(), tankhahWallet.getPublicKey(), sm.getContractsAddress(), tokenBalance,
						EVMUtil.DefaultGasPrice, EVMUtil.DefaultTokenGasLimit, nonce);
			}
		});

	}

	@Async
	@Transactional
	public void creditWalletByContractId(long contractId, String privateKey, String publicKey) {
		var sm = smartContractMicroService.findById(contractId).get();
		var mm = marketMakingMicroService.findBySmartContract(sm).get();
		var blockchain = sm.getBlockchain();
		var tankhahWallet = tankhahWalletMicroService.findByContract(sm).get(0);
		BigDecimal balance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(), publicKey);
		final BigInteger[] tankhahNonce = {
				EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(), tankhahWallet.getPrivateKeyHex()) };
		if (balance.compareTo(initBean.getMinimumMainCoin()) < 0) {
			var amount = NumberUtil.generateRandomNumber(initBean.getMinimumMainCoin(),
					initBean.getMinimumMainCoin().multiply(new BigDecimal(2)), 2);
			if (blockchain.isAutoGas()) {
				transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(), tankhahWallet.getPrivateKeyHex(),
						tankhahWallet.getPublicKey(), publicKey, amount, EVMUtil.DefaultGasLimit, tankhahNonce[0]);
			} else {
				transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(), tankhahWallet.getPrivateKeyHex(),
						tankhahWallet.getPublicKey(), publicKey, amount, EVMUtil.DefaultGasPrice,
						EVMUtil.DefaultGasLimit, tankhahNonce[0]);
			}
			tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
		}
		if (!sm.getContractsAddress().equals(EVMUtil.mainToken)) {
			BigDecimal tokenBalance = EVMUtil.getTokenBalancSync(blockchain.getRpcUrl(), privateKey,
					sm.getContractsAddress());
			if (tokenBalance.compareTo(new BigDecimal(10)) < 0) {
				var amount = NumberUtil.generateRandomNumber(new BigDecimal(10), new BigDecimal(20), 2);
				if (blockchain.isAutoGas()) {
					transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(), tankhahWallet.getPrivateKeyHex(),
							tankhahWallet.getPublicKey(), publicKey, sm.getContractsAddress(), amount,
							EVMUtil.DefaultTokenGasLimit, tankhahNonce[0]);
				} else {
					transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(), tankhahWallet.getPrivateKeyHex(),
							tankhahWallet.getPublicKey(), publicKey, sm.getContractsAddress(), amount,
							EVMUtil.DefaultGasPrice, EVMUtil.DefaultTokenGasLimit, tankhahNonce[0]);
				}
				tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
			}

		}
	}

	@Async
	@Transactional
	public void creditWalletByContractId(long contractId, String privateKey, String publicKey, BigDecimal amount) {
		var sm = smartContractMicroService.findById(contractId).get();
		var mm = marketMakingMicroService.findBySmartContract(sm).get();
		var blockchain = sm.getBlockchain();
		var tankhahWallet = tankhahWalletMicroService.findByContract(sm).get(0);
		BigDecimal balance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(), publicKey);
		final BigInteger[] tankhahNonce = {
				EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(), tankhahWallet.getPrivateKeyHex()) };
		if (!sm.getContractsAddress().equals(EVMUtil.mainToken)) {
			if (blockchain.isAutoGas()) {
				transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(), tankhahWallet.getPrivateKeyHex(),
						tankhahWallet.getPublicKey(), publicKey, sm.getContractsAddress(), amount,
						EVMUtil.DefaultTokenGasLimit, tankhahNonce[0]);
				tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
				transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(), tankhahWallet.getPrivateKeyHex(),
						tankhahWallet.getPublicKey(), publicKey, initBean.getMinimumMainCoin(), EVMUtil.DefaultGasLimit, tankhahNonce[0]);

			} else {
				transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(), tankhahWallet.getPrivateKeyHex(),
						tankhahWallet.getPublicKey(), publicKey, sm.getContractsAddress(), amount,
						new DefaultGasProvider().getGasPrice(), new DefaultGasProvider().getGasLimit(), tankhahNonce[0]);
				tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
				transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(), tankhahWallet.getPrivateKeyHex(),
						tankhahWallet.getPublicKey(), publicKey, initBean.getMinimumMainCoin(), new DefaultGasProvider().getGasPrice(),
						new DefaultGasProvider().getGasLimit(), tankhahNonce[0]);
			}
		} else {
			if (blockchain.isAutoGas()) {
				transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(), tankhahWallet.getPrivateKeyHex(),
						tankhahWallet.getPublicKey(), publicKey, amount, EVMUtil.DefaultGasLimit, tankhahNonce[0]);
			} else {
				transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(), tankhahWallet.getPrivateKeyHex(),
						tankhahWallet.getPublicKey(), publicKey, amount, new DefaultGasProvider().getGasPrice(),
						new DefaultGasProvider().getGasLimit(), tankhahNonce[0]);
			}
		}
	}

	@Async
	@Transactional
	public void creditMinimumMainCoinForTokenWallets(long contractId, int chunk) {
		logger.info(String.format("Try to fill main coin for contract %s", contractId));
		var sm = smartContractMicroService.findById(contractId).get();
		Blockchain blockchain = sm.getBlockchain();
		Coin coin = sm.getCoin();
		var tankhahWallet = tankhahWalletMicroService.findByContract(sm).get(0);
		var mm = marketMakingMicroService.findBySmartContract(sm).get();
		if (!mm.isInitialWalletCreationDone())
			throw new InvalidMarketMaking();
		final BigInteger[] tankhahNonce = {
				EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(), tankhahWallet.getPrivateKeyHex()) };
		logger.info(String.format("Nonce for wallet %s of Contract address %s and coin %s and blockchain %s is %s",
				tankhahWallet.getPublicKey(), sm.getContractsAddress(), coin.getSymbol(), blockchain.getName(),
				tankhahNonce[0]));
		IntStream.range(0, mm.getChunkCount()).forEach(idx -> {
			int[] count = { 0 };
			mmWalletService.findByContractIdAndChunk(contractId, idx).ifPresent(mmw -> {
				mmw.getTransferWalletList().forEach(wallet -> {
					wallet.setPrivateKeyHex(SecurityUtil.decryptString(wallet.getEncryptedPrivateKey(), mm.getTrPid()));
					BigDecimal balance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(), wallet.getPublicKey());
					if (balance.compareTo(initBean.getMinimumMainCoin()) < 0) {
						boolean mustRetry = true;
						while (mustRetry) {
							EvmWalletDto tmpTankhah = initBean.getRandomTmpTankhahWallet(sm);
							var amount = NumberUtil.generateRandomNumber(initBean.getMinMaincoinInContractWallet(),
									initBean.getMaxMaincoinInContractWallet(),
									initBean.getDecimalMaincoinInContractWallet());
							BigDecimal tmpTankhahBalance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(),
									tmpTankhah.getPublicKey());
							if (tmpTankhahBalance.compareTo(amount) < 0) {
								BigDecimal tankhahBalance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(),
										tankhahWallet.getPublicKey());
								if (tankhahBalance.compareTo(
										amount.multiply(new BigDecimal(initBean.getTmpTankhahWalletCount()))) >= 0) {
									if (blockchain.isAutoGas()) {
										try {
											transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
													tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
													tmpTankhah.getPublicKey(),
													amount.multiply(
															new BigDecimal(initBean.getTmpTankhahWalletCount())),
													EVMUtil.DefaultGasLimit, tankhahNonce[0]);
											mustRetry = false;
										} catch (RuntimeException e) {
											if (e.getMessage()
													.equals("maximum number of enqueued transactions reached")) {
												blockchainBean.stopAndStartMMNode(blockchain);
												mustRetry = false;
											}
										}
									} else {
										try {
											transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
													tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
													tmpTankhah.getPublicKey(),
													amount.multiply(
															new BigDecimal(initBean.getTmpTankhahWalletCount())),
													EVMUtil.DefaultGasPrice, EVMUtil.DefaultGasLimit, tankhahNonce[0]);
											mustRetry = false;
										} catch (RuntimeException e) {
											if (e.getMessage()
													.equals("maximum number of enqueued transactions reached")) {
												blockchainBean.restartBlockchain(blockchain);
												mustRetry = false;
											}
										}
									}
									tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
								} else {
									logger.info("Tankhah does not enough balance skip it.");
								}
							} else {
								BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(),
										tmpTankhah.getPrivateKey());
								if (blockchain.isAutoGas()) {
									try {
										transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
												tmpTankhah.getPrivateKey(), tmpTankhah.getPublicKey(),
												wallet.getPublicKey(), amount, EVMUtil.DefaultGasLimit, nonce);
										mustRetry = false;
									} catch (RuntimeException e) {
										if (e.getMessage().equals("maximum number of enqueued transactions reached")) {
											blockchainBean.stopAndStartMMNode(blockchain);
											mustRetry = true;
										}
									}
								} else {
									try {
										transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
												tmpTankhah.getPrivateKey(), tmpTankhah.getPublicKey(),
												wallet.getPublicKey(), amount, EVMUtil.DefaultGasPrice,
												EVMUtil.DefaultGasLimit, nonce);
										mustRetry = false;
									} catch (RuntimeException e) {
										if (e.getMessage().equals("maximum number of enqueued transactions reached")) {
											blockchainBean.restartBlockchain(blockchain);
											mustRetry = true;
										}
									}
								}
								initBean.setNonceOfTmpTankhahWallet(sm, tmpTankhah,
										tmpTankhah.getNonce().add(BigInteger.ONE));
							}
						}
					} else {
						logger.info(
								String.format("wallet %s has enough main coin balance: %s with chunk %s and count %s",
										wallet.getPublicKey(), balance.toString(), idx, ++count[0]));
					}
				});
			});
		});
	}

	public void creditMinimumMainCoinForTokenWalletsReverse(long contractId, int chunk) {
		logger.info(String.format("Try to fill main coin for contract %s", contractId));
		var sm = smartContractMicroService.findById(contractId).get();
		Blockchain blockchain = sm.getBlockchain();
		Coin coin = sm.getCoin();
		var tankhahWallet = tankhahWalletMicroService.findByContract(sm).get(0);
		var mm = marketMakingMicroService.findBySmartContract(sm).get();
		if (!mm.isInitialWalletCreationDone())
			throw new InvalidMarketMaking();
		final BigInteger[] tankhahNonce = {
				EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(), tankhahWallet.getPrivateKeyHex()) };
		logger.info(String.format("Nonce for wallet %s of Contract address %s and coin %s and blockchain %s is %s",
				tankhahWallet.getPublicKey(), sm.getContractsAddress(), coin.getSymbol(), blockchain.getName(),
				tankhahNonce[0]));
		IntStream.rangeClosed(0, mm.getChunkCount()).mapToObj(i -> mm.getChunkCount() - i).forEach(idx -> {
			int[] count = { 0 };
			mmWalletService.findByContractIdAndChunk(contractId, idx).ifPresent(mmw -> {
				mmw.getTransferWalletList().forEach(wallet -> {
					wallet.setPrivateKeyHex(SecurityUtil.decryptString(wallet.getEncryptedPrivateKey(), mm.getTrPid()));
					BigDecimal balance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(), wallet.getPublicKey());
					if (balance.compareTo(initBean.getMinimumMainCoin()) < 0) {
						boolean mustRetry = true;
						while (mustRetry) {
							EvmWalletDto tmpTankhah = initBean.getRandomTmpTankhahWallet(sm);
							var amount = NumberUtil.generateRandomNumber(initBean.getMinMaincoinInContractWallet(),
									initBean.getMaxMaincoinInContractWallet(),
									initBean.getDecimalMaincoinInContractWallet());
							BigDecimal tmpTankhahBalance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(),
									tmpTankhah.getPublicKey());
							if (tmpTankhahBalance.compareTo(amount) < 0) {
								BigDecimal tankhahBalance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(),
										tankhahWallet.getPublicKey());
								if (tankhahBalance.compareTo(
										amount.multiply(new BigDecimal(initBean.getTmpTankhahWalletCount()))) >= 0) {
									if (blockchain.isAutoGas()) {
										try {
											transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
													tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
													tmpTankhah.getPublicKey(),
													amount.multiply(
															new BigDecimal(initBean.getTmpTankhahWalletCount())),
													EVMUtil.DefaultGasLimit, tankhahNonce[0]);
											mustRetry = false;
										} catch (RuntimeException e) {
											if (e.getMessage()
													.equals("maximum number of enqueued transactions reached")) {
												blockchainBean.stopAndStartMMNode(blockchain);
												mustRetry = false;
											}
										}
									} else {
										try {
											transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
													tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
													tmpTankhah.getPublicKey(),
													amount.multiply(
															new BigDecimal(initBean.getTmpTankhahWalletCount())),
													EVMUtil.DefaultGasPrice, EVMUtil.DefaultGasLimit, tankhahNonce[0]);
											mustRetry = false;
										} catch (RuntimeException e) {
											if (e.getMessage()
													.equals("maximum number of enqueued transactions reached")) {
												blockchainBean.restartBlockchain(blockchain);
												mustRetry = false;
											}
										}
									}
									tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
								} else {
									logger.info("Tankhah does not enough balance skip it.");
								}
							} else {
								BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(),
										tmpTankhah.getPrivateKey());
								if (blockchain.isAutoGas()) {
									try {
										transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
												tmpTankhah.getPrivateKey(), tmpTankhah.getPublicKey(),
												wallet.getPublicKey(), amount, EVMUtil.DefaultGasLimit, nonce);
										mustRetry = false;
									} catch (RuntimeException e) {
										if (e.getMessage().equals("maximum number of enqueued transactions reached")) {
											blockchainBean.stopAndStartMMNode(blockchain);
											mustRetry = true;
										}
									}
								} else {
									try {
										transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
												tmpTankhah.getPrivateKey(), tmpTankhah.getPublicKey(),
												wallet.getPublicKey(), amount, EVMUtil.DefaultGasPrice,
												EVMUtil.DefaultGasLimit, nonce);
										mustRetry = false;
									} catch (RuntimeException e) {
										if (e.getMessage().equals("maximum number of enqueued transactions reached")) {
											blockchainBean.restartBlockchain(blockchain);
											mustRetry = true;
										}
									}
								}
								initBean.setNonceOfTmpTankhahWallet(sm, tmpTankhah,
										tmpTankhah.getNonce().add(BigInteger.ONE));
							}
						}
					} else {
						logger.info(
								String.format("wallet %s has enough main coin balance: %s with chunk %s and count %s",
										wallet.getPublicKey(), balance.toString(), idx, ++count[0]));
					}
				});
			});
		});
	}

	@Async
	@Transactional
	public void backAllTokenToTankhah(long contractId, int chunk) {
		List<MarketMakingWalletDto> tmpWalletList = new ArrayList<MarketMakingWalletDto>();
		logger.info(String.format("Try to back all tokens to tankhah for contract %s", contractId));
		var sm = smartContractMicroService.findById(contractId).get();
		Blockchain blockchain = sm.getBlockchain();
		Coin coin = sm.getCoin();
		var tankhahWallet = tankhahWalletMicroService.findByContract(sm).get(0);
		// var mm = marketMakingMicroService.findBySmartContract(sm).get();
		final BigInteger[] tankhahNonce = {
				EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(), tankhahWallet.getPrivateKeyHex()) };
		logger.info(String.format("Nonce for wallet %s of Contract address %s and coin %s and blockchain %s is %s",
				tankhahWallet.getPublicKey(), sm.getContractsAddress(), coin.getSymbol(), blockchain.getName(),
				tankhahNonce[0]));
		marketMakingMicroService.findBySmartContract(sm).ifPresent(mm -> {
			final int[] count = { 0 };
			IntStream.range(chunk, mm.getChunkCount()).forEach(idx -> {
				mmWalletService.findByContractIdAndChunk(contractId, idx).ifPresent(mmw -> {
					mmw.getTransferWalletList().forEach(wallet -> {
						wallet.setPrivateKeyHex(
								SecurityUtil.decryptString(wallet.getEncryptedPrivateKey(), mm.getTrPid()));
						EVMUtil.getAccountBalance(blockchain.getRpcUrl(), wallet.getPublicKey());

						BigDecimal tokenBalance = EVMUtil.getTokenBalancSync(blockchain.getRpcUrl(),
								wallet.getPrivateKeyHex(), sm.getContractsAddress());
						BigDecimal mainCoinBalance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(),
								wallet.getPublicKey());
						if (tokenBalance.compareTo(BigDecimal.ZERO) > 0) {
							if (mainCoinBalance.compareTo(new BigDecimal("0.1")) < 0) {
								var amount = new BigDecimal("0.1");
								if (blockchain.isAutoGas()) {
									transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
											tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
											wallet.getPublicKey(), amount, EVMUtil.DefaultGasLimit, tankhahNonce[0]);
								} else {
									transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
											tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
											wallet.getPublicKey(), amount, EVMUtil.DefaultGasPrice,
											EVMUtil.DefaultGasLimit, tankhahNonce[0]);

								}
								tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
								tmpWalletList.add(wallet);
							} else {
								BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(),
										wallet.getPrivateKeyHex());
								if (blockchain.isAutoGas()) {
									transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
											wallet.getPrivateKeyHex(), wallet.getPublicKey(),
											tankhahWallet.getPublicKey(), sm.getContractsAddress(), tokenBalance,
											EVMUtil.DefaultTokenGasLimit, nonce);
								} else {
									transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
											wallet.getPrivateKeyHex(), wallet.getPublicKey(),
											tankhahWallet.getPublicKey(), sm.getContractsAddress(), tokenBalance,
											EVMUtil.DefaultGasPrice, EVMUtil.DefaultTokenGasLimit, nonce);
								}
							}
						} else {
							logger.info(String.format("Wallet %s/%s at chunk %s has balance 0 and skip",
									wallet.getPublicKey(), count[0], idx));
							count[0]++;
						}
					});
				});
			});
		});
		tmpWalletList.forEach(wallet -> {
			BigDecimal tokenBalance = EVMUtil.getTokenBalancSync(blockchain.getRpcUrl(), wallet.getPrivateKeyHex(),
					sm.getContractsAddress());
			BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(), wallet.getPrivateKeyHex());
			if (blockchain.isAutoGas()) {
				transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(), wallet.getPrivateKeyHex(),
						wallet.getPublicKey(), tankhahWallet.getPublicKey(), sm.getContractsAddress(), tokenBalance,
						EVMUtil.DefaultTokenGasLimit, nonce);
			} else {
				transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(), wallet.getPrivateKeyHex(),
						wallet.getPublicKey(), tankhahWallet.getPublicKey(), sm.getContractsAddress(), tokenBalance,
						EVMUtil.DefaultGasPrice, EVMUtil.DefaultTokenGasLimit, nonce);
			}
		});

	}

	@Async
	@Transactional
	public void backAllTokenToTankhahParallel(long contractId, int chunk) {
		List<MarketMakingWalletDto> tmpWalletList = new ArrayList<MarketMakingWalletDto>();
		logger.info(String.format("Try to back all tokens to tankhah for contract %s", contractId));
		var sm = smartContractMicroService.findById(contractId).get();
		Blockchain blockchain = sm.getBlockchain();
		Coin coin = sm.getCoin();
		var tankhahWallet = tankhahWalletMicroService.findByContract(sm).get(0);
		// var mm = marketMakingMicroService.findBySmartContract(sm).get();
		final BigInteger[] tankhahNonce = {
				EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(), tankhahWallet.getPrivateKeyHex()) };
		logger.info(String.format("Nonce for wallet %s of Contract address %s and coin %s and blockchain %s is %s",
				tankhahWallet.getPublicKey(), sm.getContractsAddress(), coin.getSymbol(), blockchain.getName(),
				tankhahNonce[0]));
		marketMakingMicroService.findBySmartContract(sm).ifPresent(mm -> {
			final int[] count = { 0 };
			IntStream.range(chunk, mm.getChunkCount()).forEach(idx -> {
				mmWalletService.findByContractIdAndChunk(contractId, idx).ifPresent(mmw -> {
					mmw.getTransferWalletList().parallelStream().forEach(wallet -> {
						wallet.setPrivateKeyHex(
								SecurityUtil.decryptString(wallet.getEncryptedPrivateKey(), mm.getTrPid()));
						EVMUtil.getAccountBalance(blockchain.getRpcUrl(), wallet.getPublicKey());

						BigDecimal tokenBalance = EVMUtil.getTokenBalancSync(blockchain.getRpcUrl(),
								wallet.getPrivateKeyHex(), sm.getContractsAddress());
						BigDecimal mainCoinBalance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(),
								wallet.getPublicKey());
						if (tokenBalance.compareTo(BigDecimal.ZERO) > 0) {
							if (mainCoinBalance.compareTo(new BigDecimal("0.1")) < 0) {
								var amount = new BigDecimal("0.1");
								if (blockchain.isAutoGas()) {
									transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
											tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
											wallet.getPublicKey(), amount, EVMUtil.DefaultGasLimit, tankhahNonce[0]);
								} else {
									transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
											tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
											wallet.getPublicKey(), amount, EVMUtil.DefaultGasPrice,
											EVMUtil.DefaultGasLimit, tankhahNonce[0]);

								}
								tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
								tmpWalletList.add(wallet);
							} else {
								BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(),
										wallet.getPrivateKeyHex());
								if (blockchain.isAutoGas()) {
									transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
											wallet.getPrivateKeyHex(), wallet.getPublicKey(),
											tankhahWallet.getPublicKey(), sm.getContractsAddress(), tokenBalance,
											EVMUtil.DefaultTokenGasLimit, nonce);
								} else {
									transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
											wallet.getPrivateKeyHex(), wallet.getPublicKey(),
											tankhahWallet.getPublicKey(), sm.getContractsAddress(), tokenBalance,
											EVMUtil.DefaultGasPrice, EVMUtil.DefaultTokenGasLimit, nonce);
								}
							}
						} else {
							logger.info(String.format("Wallet %s/%s at chunk %s has balance 0 and skip",
									wallet.getPublicKey(), count[0], idx));
							count[0]++;
						}
					});
				});
			});
		});
		tmpWalletList.forEach(wallet -> {
			BigDecimal tokenBalance = EVMUtil.getTokenBalancSync(blockchain.getRpcUrl(), wallet.getPrivateKeyHex(),
					sm.getContractsAddress());
			BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(), wallet.getPrivateKeyHex());
			if (blockchain.isAutoGas()) {
				transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(), wallet.getPrivateKeyHex(),
						wallet.getPublicKey(), tankhahWallet.getPublicKey(), sm.getContractsAddress(), tokenBalance,
						EVMUtil.DefaultTokenGasLimit, nonce);
			} else {
				transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(), wallet.getPrivateKeyHex(),
						wallet.getPublicKey(), tankhahWallet.getPublicKey(), sm.getContractsAddress(), tokenBalance,
						EVMUtil.DefaultGasPrice, EVMUtil.DefaultTokenGasLimit, nonce);
			}
		});

	}

	@Async
	@Transactional
	public void backAllTokenToTankhahReverse(long contractId) {
		List<MarketMakingWalletDto> tmpWalletList = new ArrayList<MarketMakingWalletDto>();
		logger.info(String.format("Try to back all tokens to tankhah for contract %s", contractId));
		var sm = smartContractMicroService.findById(contractId).get();
		Blockchain blockchain = sm.getBlockchain();
		Coin coin = sm.getCoin();
		var tankhahWallet = tankhahWalletMicroService.findByContract(sm).get(0);
		// var mm = marketMakingMicroService.findBySmartContract(sm).get();
		final BigInteger[] tankhahNonce = {
				EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(), tankhahWallet.getPrivateKeyHex()) };
		logger.info(String.format("Nonce for wallet %s of Contract address %s and coin %s and blockchain %s is %s",
				tankhahWallet.getPublicKey(), sm.getContractsAddress(), coin.getSymbol(), blockchain.getName(),
				tankhahNonce[0]));
		marketMakingMicroService.findBySmartContract(sm).ifPresent(mm -> {
			IntStream.rangeClosed(0, mm.getChunkCount()).mapToObj(i -> mm.getChunkCount() - i).forEach(idx -> {
				mmWalletService.findByContractIdAndChunk(contractId, idx).ifPresent(mmw -> {
					mmw.getTransferWalletList().forEach(wallet -> {
						wallet.setPrivateKeyHex(
								SecurityUtil.decryptString(wallet.getEncryptedPrivateKey(), mm.getTrPid()));
						EVMUtil.getAccountBalance(blockchain.getRpcUrl(), wallet.getPublicKey());

						BigDecimal tokenBalance = EVMUtil.getTokenBalancSync(blockchain.getRpcUrl(),
								wallet.getPrivateKeyHex(), sm.getContractsAddress());
						BigDecimal mainCoinBalance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(),
								wallet.getPublicKey());
						if (tokenBalance.compareTo(BigDecimal.ZERO) > 0) {
							if (mainCoinBalance.compareTo(new BigDecimal("0.1")) < 0) {
								var amount = new BigDecimal("0.1");
								if (blockchain.isAutoGas()) {
									transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
											tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
											wallet.getPublicKey(), amount, EVMUtil.DefaultGasLimit, tankhahNonce[0]);
								} else {
									transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
											tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
											wallet.getPublicKey(), amount, EVMUtil.DefaultGasPrice,
											EVMUtil.DefaultGasLimit, tankhahNonce[0]);

								}
								tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
								tmpWalletList.add(wallet);
							} else {
								BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(),
										wallet.getPrivateKeyHex());
								if (blockchain.isAutoGas()) {
									transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
											wallet.getPrivateKeyHex(), wallet.getPublicKey(),
											tankhahWallet.getPublicKey(), sm.getContractsAddress(), tokenBalance,
											EVMUtil.DefaultTokenGasLimit, nonce);
								} else {
									transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
											wallet.getPrivateKeyHex(), wallet.getPublicKey(),
											tankhahWallet.getPublicKey(), sm.getContractsAddress(), tokenBalance,
											EVMUtil.DefaultGasPrice, EVMUtil.DefaultTokenGasLimit, nonce);
								}
							}
						}
					});
				});
			});
		});
		tmpWalletList.forEach(wallet -> {
			BigDecimal tokenBalance = EVMUtil.getTokenBalancSync(blockchain.getRpcUrl(), wallet.getPrivateKeyHex(),
					sm.getContractsAddress());
			BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(), wallet.getPrivateKeyHex());
			if (blockchain.isAutoGas()) {
				transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(), wallet.getPrivateKeyHex(),
						wallet.getPublicKey(), tankhahWallet.getPublicKey(), sm.getContractsAddress(), tokenBalance,
						EVMUtil.DefaultTokenGasLimit, nonce);
			} else {
				transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(), wallet.getPrivateKeyHex(),
						wallet.getPublicKey(), tankhahWallet.getPublicKey(), sm.getContractsAddress(), tokenBalance,
						EVMUtil.DefaultGasPrice, EVMUtil.DefaultTokenGasLimit, nonce);
			}
		});

	}

	@Transactional
	public void generateTempTankhahWallet(long contractId) {
		smartContractMicroService.findById(contractId).ifPresent(sm -> {
			if (!tempTankhahWalletMicroService.existsBySmartContractAndWalletType(sm, WalletType.TRANSFER)) {
				var lst = EvmWalletUtil.generateRandomWallet(initBean.getTmpTankhahWalletCount());
				lst.stream().map(ewDto -> new TempTankhahWallet(ewDto)).peek(ttw -> {
					ttw.setSmartContract(sm);
					ttw.setWalletType(WalletType.TRANSFER);
					try {
						ttw = tempTankhahWalletMicroService.saveAndFlush(ttw);
						// logger.info("TempTankhahWallet has been saved to database : " + ttw);
					} catch (Exception e) {
						logger.error("Error occurred while saving TempTankhahWallet:", e);
						e.printStackTrace();
					}
				}).collect(Collectors.toList());

				// tempTankhahWalletMicroService.saveAll(ttwLst);
			}
		});
	}

	@Async
	@Transactional
	public void backAllTokenFromTempTankhahToTankhah(long contractId) {
		logger.info(String.format("Try to back all tokens from temp tankhah to tankhah for contract %s", contractId));
		var sm = smartContractMicroService.findById(contractId).get();
		Blockchain blockchain = sm.getBlockchain();
		Coin coin = sm.getCoin();
		var tankhahWallet = tankhahWalletMicroService.findByContract(sm).get(0);
		tempTankhahWalletMicroService.findBySmartContractAndWalletType(sm, WalletType.TRANSFER).stream()
				.forEach(wallet -> {
					BigDecimal tokenBalance = EVMUtil.getTokenBalancSync(blockchain.getRpcUrl(), wallet.getPrivateKey(),
							sm.getContractsAddress());
					if (tokenBalance.compareTo(BigDecimal.ZERO) > 0) {
						BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(), wallet.getPrivateKey());
						if (blockchain.isAutoGas()) {
							transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(), wallet.getPrivateKey(),
									wallet.getPublicKey(), tankhahWallet.getPublicKey(), sm.getContractsAddress(),
									tokenBalance, EVMUtil.DefaultTokenGasLimit, nonce);
						} else {
							transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(), wallet.getPrivateKey(),
									wallet.getPublicKey(), tankhahWallet.getPublicKey(), sm.getContractsAddress(),
									tokenBalance, EVMUtil.DefaultGasPrice, EVMUtil.DefaultTokenGasLimit, nonce);
						}
					}
					BigDecimal mainCoinBalance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(),
							wallet.getPublicKey());
					if (mainCoinBalance.compareTo(initBean.getMinimumBalanceForTransfer()) > 0) {
						BigDecimal amount = mainCoinBalance.subtract(initBean.getMinimumBalanceForTransfer());
						BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(), wallet.getPrivateKey());
						if (blockchain.isAutoGas()) {
							transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(), wallet.getPrivateKey(),
									wallet.getPublicKey(), tankhahWallet.getPublicKey(), amount,
									EVMUtil.DefaultGasLimit, nonce);
						} else {
							transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(), wallet.getPrivateKey(),
									wallet.getPublicKey(), tankhahWallet.getPublicKey(), amount,
									EVMUtil.DefaultGasPrice, EVMUtil.DefaultGasLimit, nonce);
						}
					}
				});

	}

	@Async
	@Transactional
	public void deleteTempTankhahWallet(long contractId) {
		logger.info(String.format("Try to delete temp tankhah wallets for contract %s", contractId));
		var sm = smartContractMicroService.findById(contractId).get();
		tempTankhahWalletMicroService.findBySmartContractAndWalletType(sm, WalletType.TRANSFER).forEach(wallet -> {
			tempTankhahWalletMicroService.delete(wallet);
		});
		logger.info(String.format("All temp tankhah wallets for contract %s has been deleted.", contractId));
	}

	@Async
	@Transactional
	public void updateAllwalletsBalancesByContractId(long contractId) {
		smartContractMicroService.findById(contractId).ifPresent(sm -> {
			Blockchain blockchain = sm.getBlockchain();
			Coin coin = sm.getCoin();
			final int[] count = { Math
					.toIntExact(marketMakingWalletMicroService.countByContractAndWalletType(sm, WalletType.TRANSFER)) };
			final int[] idx = { 0 };
			marketMakingWalletMicroService
					.findAllWalletsByContractIdAndWalletTypeNative(contractId, WalletType.TRANSFER).forEach(wallet -> {
						BigDecimal tokenBalance = EVMUtil.getTokenBalancSync(blockchain.getRpcUrl(),
								wallet.getPrivateKey(), sm.getContractsAddress());
						BigDecimal mainCoinBalance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(),
								wallet.getPublicKey());
						if (!wallet.getBalance().equals(tokenBalance)) {
							MarketMakingWallet mmw = marketMakingWalletMicroService.findById(wallet.getMmWalletId())
									.get();
							BigDecimal oldBalance = mmw.getBalance();
							mmw.setBalance(tokenBalance);
							mmw.setMainCoinBalance(mainCoinBalance);
							mmw = marketMakingWalletMicroService.saveAndFlush(mmw);
							logger.info(String.format("%s/%s wallet %s balance has been changed from %s to %s",
									++idx[0], count[0], wallet.getPublicKey(), oldBalance.toString(),
									tokenBalance.toString()));
						} else {
							logger.info(String.format("%s/%s wallet %s balance not changed and is %s.", ++idx[0],
									count[0], wallet.getPublicKey(), tokenBalance.toString()));
						}

					});
		});
	}

	@Async
	@Transactional
	public void updateAllwalletsBalancesByContractIdParallel(long contractId) {
		smartContractMicroService.findById(contractId).ifPresent(sm -> {
			Blockchain blockchain = sm.getBlockchain();
			Coin coin = sm.getCoin();
			final int[] count = { Math
					.toIntExact(marketMakingWalletMicroService.countByContractAndWalletType(sm, WalletType.TRANSFER)) };
			final int[] idx = { 0 };
			marketMakingWalletMicroService
					.findAllWalletsByContractIdAndWalletTypeNative(contractId, WalletType.TRANSFER).parallelStream()
					.forEach(wallet -> {
						BigDecimal tokenBalance = EVMUtil.getTokenBalancSync(blockchain.getRpcUrl(),
								wallet.getPrivateKey(), sm.getContractsAddress());
						BigDecimal mainCoinBalance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(),
								wallet.getPublicKey());
						if (!wallet.getBalance().equals(tokenBalance)) {
							MarketMakingWallet mmw = marketMakingWalletMicroService.findById(wallet.getMmWalletId())
									.get();
							BigDecimal oldBalance = mmw.getBalance();
							mmw.setBalance(tokenBalance);
							mmw.setMainCoinBalance(mainCoinBalance);
							mmw = marketMakingWalletMicroService.saveAndFlush(mmw);
							logger.info(String.format("%s/%s wallet %s balance has been changed from %s to %s",
									++idx[0], count[0], wallet.getPublicKey(), oldBalance.toString(),
									tokenBalance.toString()));
						} else {
							logger.info(String.format("%s/%s wallet %s balance not changed and is %s.", ++idx[0],
									count[0], wallet.getPublicKey(), tokenBalance.toString()));
						}

					});
		});
	}

	@Async
	@Transactional
	public void fixPrivateKeyByContractId(long contractId) {
		smartContractMicroService.findById(contractId).ifPresent(sm -> {
			final int[] count = { Math
					.toIntExact(marketMakingWalletMicroService.countByContractAndWalletType(sm, WalletType.TRANSFER)) };
			final int[] idx = { 0 };
			marketMakingWalletMicroService
					.findAllWalletsByContractIdAndWalletTypeNative(contractId, WalletType.TRANSFER).forEach(wallet -> {
						if (Strings.isNullOrEmpty(wallet.getPrivateKeyHex())) {
							Optional<MarketMakingWallet> mmw = marketMakingWalletMicroService
									.findById(wallet.getMmWalletId());
							if (mmw.isPresent()) {
								EvmWalletDto ewmwdto = EvmWalletUtil
										.generateWallet(new BigInteger(mmw.get().getPrivateKey()));
								logger.info(String.format("(%s/%s) private key for wallet %s is %s", ++idx[0], count[0],
										wallet.getPublicKey(), ewmwdto.getHexKey()));
							}

						} else {
							try {
								var credentials = Credentials.create(wallet.getPrivateKeyHex());
								logger.info(String.format("(%s/%s) private key for wallet %s is %s and ok.", ++idx[0],
										count[0], wallet.getPublicKey(), wallet.getPrivateKeyHex()));
							} catch (Exception e) {
								Optional<MarketMakingWallet> mmw = marketMakingWalletMicroService
										.findById(wallet.getMmWalletId());
								if (mmw.isPresent()) {
									EvmWalletDto ewmwdto = EvmWalletUtil
											.generateWallet(new BigInteger(mmw.get().getPrivateKey()));
									logger.info(String.format("(%s/%s) private key for wallet %s is %s", ++idx[0],
											count[0], wallet.getPublicKey(), ewmwdto.getHexKey()));
								}
							}
						}
					});
		});
	}
}
