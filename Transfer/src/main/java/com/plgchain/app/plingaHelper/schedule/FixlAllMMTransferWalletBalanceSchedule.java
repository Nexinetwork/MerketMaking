package com.plgchain.app.plingaHelper.schedule;

import com.plgchain.app.plingaHelper.bean.InitBean;
import com.plgchain.app.plingaHelper.bean.TransferBean;
import com.plgchain.app.plingaHelper.dto.MarketMakingWalletDto;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.microService.MarketMakingMicroService;
import com.plgchain.app.plingaHelper.microService.MarketMakingWalletMicroService;
import com.plgchain.app.plingaHelper.microService.TankhahWalletMicroService;
import com.plgchain.app.plingaHelper.util.NumberUtil;
import com.plgchain.app.plingaHelper.util.blockchain.EVMUtil;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;

@Component
public class FixlAllMMTransferWalletBalanceSchedule {

	private static final Logger logger = LoggerFactory.getLogger(FixlAllMMTransferWalletBalanceSchedule.class);

	private final InitBean initBean;
	private final MarketMakingMicroService marketMakingMicroService;
	private final TransferBean transferBean;
	private final TankhahWalletMicroService tankhahWalletMicroService;
	private final MarketMakingWalletMicroService mmWalletMicroService;

	@Inject
	public FixlAllMMTransferWalletBalanceSchedule(InitBean initBean, MarketMakingMicroService marketMakingMicroService,
			TransferBean transferBean, TankhahWalletMicroService tankhahWalletMicroService,
			MarketMakingWalletMicroService mmWalletMicroService) {
		this.initBean = initBean;
		this.marketMakingMicroService = marketMakingMicroService;
		this.transferBean = transferBean;
		this.tankhahWalletMicroService = tankhahWalletMicroService;
		this.mmWalletMicroService = mmWalletMicroService;
	}

	//@Scheduled(cron = "0 */10 * * * *", zone = "GMT")
	@Transactional
	public void fixlAllMMTransferWalletBalance() {
		if (!initBean.doesActionRunning("fixlAllMMTransferWalletBalance")) {
			initBean.startActionRunning("fixlAllMMTransferWalletBalance");
			logger.info("fixlAllMMTransferWalletBalance started.");
			try {
				marketMakingMicroService.findByInitialWalletCreationDoneAndInitialWalletFundingDoneOrderByRandom(true, true)
						.parallelStream().forEach(mm -> {
							SmartContract sm = mm.getSmartContract();
							var blockchain = sm.getBlockchain();
							var coin = sm.getCoin();
							final int[] enqueued = { 0 };
							logger.info("Try to fix for coin {}", coin.getSymbol());
							var tankhahWallet = tankhahWalletMicroService.findByContract(sm).get(0);
							final BigInteger[] tankhahNonce = {
									EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(), tankhahWallet.getPrivateKeyHex()) };
							logger.info(String.format(
									"Nonce for wallet %s of Contract address %s and coin %s and blockchain %s is %s",
									tankhahWallet.getPublicKey(), sm.getContractsAddress(), coin.getSymbol(),
									blockchain.getName(), tankhahNonce[0]));
							int page = 0;
							Page<MarketMakingWalletDto> mmWalletPageDto = null;
							do {
								PageRequest pageable = PageRequest.of(page,
										initBean.getFixTransferWalletBalancePerRound());
								mmWalletPageDto = initBean
										.getMMWalletList(sm.getContractId(), pageable);
								mmWalletPageDto.stream().forEach(wallet -> {
									if (sm.getContractsAddress().equals(EVMUtil.mainToken)) {
										BigDecimal balance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(),
												wallet.getPublicKey());
										if (balance.compareTo(mm.getMaxInitial()) > 0) {
											var amount = NumberUtil.generateRandomNumber(mm.getMinInitial(),
													mm.getMaxInitial(), mm.getInitialDecimal());
											var mustReturn = balance.subtract(amount);
											BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(),
													wallet.getPrivateKeyHex());
											if (blockchain.isAutoGas()) {
												transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
														wallet.getPrivateKeyHex(), wallet.getPublicKey(),
														tankhahWallet.getPublicKey(), mustReturn,
														EVMUtil.DefaultGasLimit, nonce);
											} else {
												transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
														wallet.getPrivateKeyHex(), wallet.getPublicKey(),
														tankhahWallet.getPublicKey(), mustReturn,
														EVMUtil.DefaultGasPrice, EVMUtil.DefaultGasLimit, nonce);
											}
										} else if (balance.equals(BigDecimal.ZERO)) {
											var amount = NumberUtil.generateRandomNumber(mm.getMinInitial(),
													mm.getMaxInitial(), mm.getInitialDecimal());
											if (blockchain.isAutoGas()) {
												transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
														tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
														wallet.getPublicKey(), amount, EVMUtil.DefaultGasLimit,
														tankhahNonce[0]);
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
											var amount = NumberUtil.generateRandomNumber(
													initBean.getMinMaincoinInContractWallet(),
													initBean.getMaxMaincoinInContractWallet(),
													initBean.getDecimalMaincoinInContractWallet());
											var mustReturn = balance.subtract(amount);
											BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(),
													wallet.getPrivateKeyHex());
											if (blockchain.isAutoGas()) {
												transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
														wallet.getPrivateKeyHex(), wallet.getPublicKey(),
														tankhahWallet.getPublicKey(), mustReturn,
														EVMUtil.DefaultGasLimit, nonce);
											} else {
												transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
														wallet.getPrivateKeyHex(), wallet.getPublicKey(),
														tankhahWallet.getPublicKey(), mustReturn,
														EVMUtil.DefaultGasPrice, EVMUtil.DefaultGasLimit, nonce);
											}
										} else if (balance.equals(BigDecimal.ZERO)) {
											var amount = NumberUtil.generateRandomNumber(initBean.getMinMaincoinInContractWallet(), initBean.getMaxMaincoinInContractWallet(),
													initBean.getDecimalMaincoinInContractWallet());
											if (blockchain.isAutoGas()) {
												transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
														tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
														wallet.getPublicKey(), amount, EVMUtil.DefaultGasLimit,
														tankhahNonce[0]);
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
										var amount = NumberUtil.generateRandomNumber(mm.getMinInitial(),
												mm.getMaxInitial(), mm.getInitialDecimal());
										if (tokenBalance.compareTo(mm.getMaxInitial()) > 0) {
											var mustReturn = tokenBalance.subtract(amount);
											BigInteger nonce = EVMUtil.getNonceByPrivateKey(blockchain.getRpcUrl(),
													wallet.getPrivateKeyHex());
											if (blockchain.isAutoGas()) {
												transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
														wallet.getPrivateKeyHex(), wallet.getPublicKey(),
														tankhahWallet.getPublicKey(), sm.getContractsAddress(),
														mustReturn, EVMUtil.DefaultTokenGasLimit, nonce);
											} else {
												transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
														wallet.getPrivateKeyHex(), wallet.getPublicKey(),
														tankhahWallet.getPublicKey(), sm.getContractsAddress(),
														mustReturn, EVMUtil.DefaultGasPrice,
														EVMUtil.DefaultTokenGasLimit, nonce);
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
														EVMUtil.DefaultGasPrice, EVMUtil.DefaultTokenGasLimit,
														tankhahNonce[0]);
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
						});
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			initBean.stopActionRunning("fixlAllMMTransferWalletBalance");
			logger.info("fixlAllMMTransferWalletBalance finished.");
		} else {
			logger.warn("Schedule method fixlAllMMTransferWalletBalance already running, skipping it.");
		}
	}

}
