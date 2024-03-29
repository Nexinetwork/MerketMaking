package com.plgchain.app.plingaHelper.schedule;

import com.plgchain.app.plingaHelper.bean.InitBean;
import com.plgchain.app.plingaHelper.bean.TransferBean;
import com.plgchain.app.plingaHelper.constant.TransactionParallelType;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.entity.marketMaking.MarketMakingWallet;
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
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class FundInitialMMTransferWalletSchedule {

	private static final Logger logger = LoggerFactory.getLogger(FundInitialMMTransferWalletSchedule.class);

	private final InitBean initBean;
	private final MarketMakingMicroService marketMakingMicroService;
	private final TransferBean transferBean;
	private final TankhahWalletMicroService tankhahWalletMicroService;
	private final MarketMakingWalletMicroService mmWalletMicroService;
	private final int sleepInSeconds = 4;

	@Inject
	public FundInitialMMTransferWalletSchedule(InitBean initBean, MarketMakingMicroService marketMakingMicroService,
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
	public void fundInitialMMTransferWallet() {
		if (!initBean.doesActionRunning("fundInitialMMTransferWallet")) {
			initBean.startActionRunning("fundInitialMMTransferWallet");
			logger.info("Running Schedule : fundInitialMMTransferWallet");
			try {
				marketMakingMicroService
						.findTopByInitialWalletCreationDoneAndInitialWalletFundingDoneOrderByMarketMakingId(true, false)
						.ifPresent(mm -> {
							SmartContract sm = mm.getSmartContract();
							var blockchain = sm.getBlockchain();
							var coin = sm.getCoin();
							final int[] enqueued = { 0 };
							logger.info("Try to fund for coin {}", sm.getCoin().getSymbol());
							var tankhahWallet = tankhahWalletMicroService.findByContract(sm).get(0);
							final BigInteger[] tankhahNonce = { EVMUtil.getNonceByPrivateKey(sm.getBlockchain().getRpcUrl(),
									tankhahWallet.getPrivateKeyHex()) };
							logger.info("Current nonce of tankhah wallet is: " + tankhahNonce[0]);

							int page = 0;
							int size = initBean.getSelectPageSize();
							Page<MarketMakingWallet> mmWalletPage;
							do {
								PageRequest pageable = PageRequest.of(page, size);
								mmWalletPage = mmWalletMicroService.findByContractWithPaging(sm, pageable);
								mmWalletPage.getContent().stream().filter(
										wallet -> mm.getTransactionParallelType().equals(TransactionParallelType.SYNC))
										.forEach(wallet -> {
											logger.info(String.format("Proccessing wallet %s of contract %s of coin %s of blockchain %s",wallet.getPublicKey(), sm.getContractsAddress(),coin.getSymbol(),blockchain.getName()));
											if (sm.getContractsAddress().equals(EVMUtil.mainToken)) {
												var amount = NumberUtil.generateRandomNumber(mm.getMinInitial(),
														mm.getMaxInitial(), mm.getInitialDecimal());
												if (blockchain.isAutoGas()) {
													transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
															tankhahWallet.getPrivateKeyHex(),
															tankhahWallet.getPublicKey(), wallet.getPublicKey(), amount,
															EVMUtil.DefaultGasLimit, tankhahNonce[0]);
												} else {
													transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
															tankhahWallet.getPrivateKeyHex(),
															tankhahWallet.getPublicKey(), wallet.getPublicKey(), amount,
															EVMUtil.DefaultGasPrice, EVMUtil.DefaultGasLimit,
															tankhahNonce[0]);
												}
												tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
												enqueued[0]++;
												if (enqueued[0] >= 100) {
													try {
														Thread.sleep(sleepInSeconds * 1000);
													} catch (InterruptedException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													}
													enqueued[0] = 0;
												}
											} else {
												var mainCoinAmount = NumberUtil.generateRandomNumber(
														initBean.getMinMaincoinInContractWallet(),
														initBean.getMaxMaincoinInContractWallet(),
														initBean.getDecimalMaincoinInContractWallet());
												if (blockchain.isAutoGas()) {
													transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
															tankhahWallet.getPrivateKeyHex(),
															tankhahWallet.getPublicKey(), wallet.getPublicKey(),
															mainCoinAmount, EVMUtil.DefaultGasLimit, tankhahNonce[0]);
												} else {
													transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
															tankhahWallet.getPrivateKeyHex(),
															tankhahWallet.getPublicKey(), wallet.getPublicKey(),
															mainCoinAmount, EVMUtil.DefaultGasPrice,
															EVMUtil.DefaultGasLimit, tankhahNonce[0]);
												}
												tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
												var tokenAmount = NumberUtil.generateRandomNumber(mm.getMinInitial(),
														mm.getMaxInitial(), mm.getInitialDecimal());
												if (blockchain.isAutoGas()) {
													transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
															tankhahWallet.getPrivateKeyHex(),
															tankhahWallet.getPublicKey(), wallet.getPublicKey(),
															sm.getContractsAddress(), tokenAmount,
															EVMUtil.DefaultTokenGasLimit, tankhahNonce[0]);
												} else {
													transferBean.transferBetweenToAccountSync(blockchain.getRpcUrl(),
															tankhahWallet.getPrivateKeyHex(),
															tankhahWallet.getPublicKey(), wallet.getPublicKey(),
															sm.getContractsAddress(), tokenAmount,
															EVMUtil.DefaultGasPrice, EVMUtil.DefaultTokenGasLimit,
															tankhahNonce[0]);
												}
												tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
												enqueued[0]++;
												if (enqueued[0] >= 100) {
													try {
														Thread.sleep(sleepInSeconds * 1000);
													} catch (InterruptedException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													}
													enqueued[0] = 0;
												}
											}
										});
								page++;
							} while (mmWalletPage.hasNext());

							mm.setInitialWalletFundingDone(true);
							marketMakingMicroService.save(mm);
						});
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			initBean.stopActionRunning("fundInitialMMTransferWallet");
			logger.info("fundInitialMMTransferWallet finished.");
		} else {
			logger.warn("Schedule method fundInitialMMTransferWallet already running, skipping it.");
		}
	}
}
