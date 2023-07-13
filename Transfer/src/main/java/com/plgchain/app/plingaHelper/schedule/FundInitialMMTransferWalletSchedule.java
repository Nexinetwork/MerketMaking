package com.plgchain.app.plingaHelper.schedule;

import com.plgchain.app.plingaHelper.bean.InitBean;
import com.plgchain.app.plingaHelper.bean.TransferBean;
import com.plgchain.app.plingaHelper.constant.TransactionParallelType;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.entity.TankhahWallet;
import com.plgchain.app.plingaHelper.entity.coingecko.Coin;
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

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

@Component
public class FundInitialMMTransferWalletSchedule implements Serializable {

	private static final long serialVersionUID = 7504602259969637312L;
	private static final Logger logger = LoggerFactory.getLogger(FundInitialMMTransferWalletSchedule.class);

	private final InitBean initBean;
	private final MarketMakingService marketMakingService;
	private final TransferBean transferBean;
	private final TankhahWalletService tankhahWalletService;
	private final MarketMakingWalletService mmWalletService;

	@Inject
	public FundInitialMMTransferWalletSchedule(InitBean initBean, MarketMakingService marketMakingService,
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
	public void fundInitialMMTransferWallet() {
	    if (!initBean.doesActionRunning("fundInitialMMTransferWallet")) {
	        initBean.startActionRunning("fundInitialMMTransferWallet");
	        try {
	            marketMakingService
	                    .findTopByInitialWalletCreationDoneAndInitialWalletFundingDoneOrderByMarketMakingId(true, false)
	                    .ifPresent(mm -> {
	                        SmartContract sm = mm.getSmartContract();
	                        var blockchain = sm.getBlockchain();
	                        var coin = sm.getCoin();
	                        logger.info("Try to fund for coin {}", coin.getSymbol());
	                        var tankhahWallet = tankhahWalletService.findByContract(sm).get(0);
	                        var gasPrice = new BigInteger("1100000000");
	                        var gasLimit = EVMUtil.getGasLimit(blockchain.getRpcUrl()).divide(new BigInteger("10000"));
	                        final BigInteger[] tankhahNonce = { BigInteger.ZERO };
	                        try {
	                            tankhahNonce[0] = EVMUtil.getNonce(blockchain.getRpcUrl(), tankhahWallet.getPrivateKey());
	                        } catch (IOException e) {
	                            logger.error(e.getMessage());
	                        }
	                        logger.info("Current nonce of tankhah wallet is : " + tankhahNonce[0].toString());
	                        mmWalletService.findByContract(sm)
	                                .stream()
	                                .filter(wallet -> mm.getTransactionParallelType().equals(TransactionParallelType.SYNC))
	                                .filter(wallet -> sm.getContractsAddress().equals(EVMUtil.mainToken))
	                                .forEach(wallet -> {
	                                    var amount = NumberUtil.generateRandomNumber(mm.getMinInitial(),
	                                            mm.getMaxInitial(), mm.getInitialDecimal());

										  transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
										  tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
										  wallet.getPublicKey(), amount, EVMUtil.DefaultGasPrice,EVMUtil.DefaultGasLimit, tankhahNonce[0]);

	                                    tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
	                                });

	                        mm.setInitialWalletFundingDone(true);
	                        marketMakingService.save(mm);
	                    });
	        } catch (Exception e) {
	            logger.error(e.getMessage());
	        }
	        initBean.stopActionRunning("fundInitialMMTransferWallet");
	    } else {
	        logger.warn("Schedule method fundInitialMMTransferWallet already running, skipping it.");
	    }
	}


}
