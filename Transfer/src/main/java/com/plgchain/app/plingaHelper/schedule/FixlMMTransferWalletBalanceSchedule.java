package com.plgchain.app.plingaHelper.schedule;

import com.plgchain.app.plingaHelper.bean.InitBean;
import com.plgchain.app.plingaHelper.bean.TransferBean;
import com.plgchain.app.plingaHelper.constant.TransactionParallelType;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.service.MarketMakingService;
import com.plgchain.app.plingaHelper.service.MarketMakingWalletService;
import com.plgchain.app.plingaHelper.service.TankhahWalletService;
import com.plgchain.app.plingaHelper.util.NumberUtil;
import com.plgchain.app.plingaHelper.util.blockchain.EVMUtil;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;

@Component
public class FixlMMTransferWalletBalanceSchedule {

	private static final Logger logger = LoggerFactory.getLogger(FixlMMTransferWalletBalanceSchedule.class);

	private final InitBean initBean;
	private final MarketMakingService marketMakingService;
	private final TransferBean transferBean;
	private final TankhahWalletService tankhahWalletService;
	private final MarketMakingWalletService mmWalletService;

	@Inject
	public FixlMMTransferWalletBalanceSchedule(InitBean initBean, MarketMakingService marketMakingService,
			TransferBean transferBean, TankhahWalletService tankhahWalletService,
			MarketMakingWalletService mmWalletService) {
		this.initBean = initBean;
		this.marketMakingService = marketMakingService;
		this.transferBean = transferBean;
		this.tankhahWalletService = tankhahWalletService;
		this.mmWalletService = mmWalletService;
	}

	@Scheduled(cron = "0 */10 * * * *", zone = "GMT")
	@Transactional
	public void fixlMMTransferWalletBalance() {
		if (!initBean.doesActionRunning("fixlMMTransferWalletBalance")) {
			initBean.startActionRunning("fixlMMTransferWalletBalance");
			logger.info("fixlMMTransferWalletBalance started.");
			try {
				marketMakingService.findByInitialWalletCreationDoneAndInitialWalletFundingDone(true, true)
						.parallelStream().forEach(mm -> {
							SmartContract sm = mm.getSmartContract();
							var blockchain = sm.getBlockchain();
							var coin = sm.getCoin();
							logger.info("Try to fix for coin {}", coin.getSymbol());
							var tankhahWallet = tankhahWalletService.findByContract(sm).get(0);
							final BigInteger[] tankhahNonce = {
									EVMUtil.getNonce(blockchain.getRpcUrl(), tankhahWallet.getPrivateKeyHex()) };
							logger.info("Current nonce of tankhah wallet is: " + tankhahNonce[0]);
							logger.info(String.format(
									"Nonce for wallet %s of Contract address %s and coin %s and blockchain %s is %s",
									tankhahWallet.getPublicKey(), sm.getContractsAddress(), coin.getSymbol(),
									blockchain.getName(), tankhahNonce[0]));
							mmWalletService
									.findNByContractOrderByRandom(sm, initBean.getFixTransferWalletBalancePerRound())
									.stream()
									.filter(wallet -> mm.getTransactionParallelType()
											.equals(TransactionParallelType.SYNC))
									.filter(wallet -> sm.getContractsAddress().equals(EVMUtil.mainToken))
									.forEach(wallet -> {
										BigDecimal balance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(),
												wallet.getPublicKey());
										if (balance.compareTo(mm.getMaxInitial()) > 0) {
											var amount = NumberUtil.generateRandomNumber(mm.getMinInitial(),
													mm.getMaxInitial(), mm.getInitialDecimal());
											var mustReturn = balance.subtract(amount);
											BigInteger nonce = EVMUtil.getNonce(blockchain.getRpcUrl(),
													wallet.getPrivateKeyHex());
											transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
													wallet.getPrivateKeyHex(), wallet.getPublicKey(),
													tankhahWallet.getPublicKey(), mustReturn, EVMUtil.DefaultGasPrice,
													EVMUtil.DefaultGasLimit, nonce);
										} else if (balance.equals(BigDecimal.ZERO)) {
											var amount = NumberUtil.generateRandomNumber(mm.getMinInitial(),
													mm.getMaxInitial(), mm.getInitialDecimal());
											transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
													tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
													wallet.getPublicKey(), amount, EVMUtil.DefaultGasPrice,
													EVMUtil.DefaultGasLimit, tankhahNonce[0]);
											tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
										}
									});
							mmWalletService
									.findNByContractOrderByRandom(sm, initBean.getFixTransferWalletBalancePerRound())
									.stream()
									.filter(wallet -> mm.getTransactionParallelType()
											.equals(TransactionParallelType.SYNC))
									.filter(wallet -> (!sm.getContractsAddress().equals(EVMUtil.mainToken)))
									.forEach(wallet -> {
										logger.info("000000000000000000000000000000000000000000000");
										logger.info("0101010101010101010101010101 wallet is" + wallet.getPrivateKeyHex() + " : " + wallet.getPublicKey());
										BigDecimal balance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(),
												wallet.getPublicKey());
										logger.info("020202020202020202020202020202020202020202020202 balance is" + balance.toString());
										BigDecimal tokenBalance = EVMUtil.getTokenBalancSync(blockchain.getRpcUrl(),
												wallet.getPrivateKeyHex(), sm.getContractsAddress());
										logger.info("11111111111111111111111111111111111111111 Token Balance is " + tokenBalance.toString());
										if (balance.compareTo(initBean.getMaxMaincoinInContractWallet()) > 0) {
											logger.info("222222222222222222222222222222222222222222222222222222222");
											var amount = NumberUtil.generateRandomNumber(
													initBean.getMinMaincoinInContractWallet(),
													initBean.getMaxMaincoinInContractWallet(),
													initBean.getDecimalMaincoinInContractWallet());
											var mustReturn = balance.subtract(amount);
											BigInteger nonce = EVMUtil.getNonce(blockchain.getRpcUrl(),
													wallet.getPrivateKeyHex());
											transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
													wallet.getPrivateKeyHex(), wallet.getPublicKey(),
													tankhahWallet.getPublicKey(), mustReturn, EVMUtil.DefaultGasPrice,
													EVMUtil.DefaultGasLimit, nonce);
											logger.info("3333333333333333333333333333333333333333333333333333333");
										} else if (balance.equals(BigDecimal.ZERO)) {
											logger.info("44444444444444444444444444444444444");
											var amount = NumberUtil.generateRandomNumber(mm.getMinInitial(),
													mm.getMaxInitial(), mm.getInitialDecimal());
											transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
													tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
													wallet.getPublicKey(), amount, EVMUtil.DefaultGasPrice,
													EVMUtil.DefaultGasLimit, tankhahNonce[0]);
											tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
											logger.info("555555555555555555555555555555555555555555");
										}
										logger.info("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
										var amount = NumberUtil.generateRandomNumber(mm.getMinInitial(),
												mm.getMaxInitial(), mm.getInitialDecimal());
										logger.info("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
										if (tokenBalance.compareTo(mm.getMaxInitial()) > 0) {
											logger.info("666666666666666666666666666666666666");
											var mustReturn = tokenBalance.subtract(amount);
											BigInteger nonce = EVMUtil.getNonce(blockchain.getRpcUrl(),
													wallet.getPrivateKeyHex());
											transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
													wallet.getPrivateKeyHex(), wallet.getPublicKey(),
													tankhahWallet.getPublicKey(), sm.getContractsAddress(), mustReturn,
													EVMUtil.DefaultGasPrice, EVMUtil.DefaultTokenGasLimit, nonce);
											logger.info("77777777777777777777777777777777777777777777777777777777");
										} else if (tokenBalance.equals(BigDecimal.ZERO)) {
											logger.info("888888888888888888888888888888888888888888888888888");
											transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
													tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
													wallet.getPublicKey(), sm.getContractsAddress(), amount,
													EVMUtil.DefaultGasPrice, EVMUtil.DefaultTokenGasLimit,
													tankhahNonce[0]);
											tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
											logger.info("99999999999999999999999999999999999999999999999999999999");
										}
										logger.info("ccccccccccccccccccccccccccccccccccccccccccccccccc");
									});
						});
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			initBean.stopActionRunning("fixlMMTransferWalletBalance");
			logger.info("fixlMMTransferWalletBalance finished.");
		} else {
			logger.warn("Schedule method fixlMMTransferWalletBalance already running, skipping it.");
		}
	}

}
