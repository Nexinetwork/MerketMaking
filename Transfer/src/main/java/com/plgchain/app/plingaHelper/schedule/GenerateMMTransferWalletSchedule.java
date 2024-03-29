package com.plgchain.app.plingaHelper.schedule;

import com.plgchain.app.plingaHelper.bean.InitBean;
import com.plgchain.app.plingaHelper.bean.TransferBean;
import com.plgchain.app.plingaHelper.constant.WalletType;
import com.plgchain.app.plingaHelper.microService.MarketMakingMicroService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class GenerateMMTransferWalletSchedule implements Serializable {

	private static final long serialVersionUID = 7504602259969637312L;
	private static final Logger logger = LoggerFactory.getLogger(GenerateMMTransferWalletSchedule.class);

	private final InitBean initBean;
	private final MarketMakingMicroService marketMakingMicroService;
	private final TransferBean transferBean;

	@Inject
	public GenerateMMTransferWalletSchedule(InitBean initBean, MarketMakingMicroService marketMakingMicroService,
			TransferBean transferBean) {
		this.initBean = initBean;
		this.marketMakingMicroService = marketMakingMicroService;
		this.transferBean = transferBean;
	}

	@Scheduled(cron = "0 */10 * * * *", zone = "GMT")
	@Transactional
	public void generateMMTransferWallet() {
		if (!initBean.doesActionRunning("generateMMTransferWallet")) {
			initBean.startActionRunning("generateMMTransferWallet");
			logger.info("Running Schedule : generateMMTransferWallet");
			try {
				marketMakingMicroService.findByInitialWalletCreationDone(false).forEach(mm -> {
					transferBean.generateWalletsForMarketmaking(mm, initBean.getJpaBatchCount(), mm.getSmartContract(),
							mm.getSmartContract().getBlockchain(), mm.getSmartContract().getCoin(),
							WalletType.TRANSFER);
					mm.setInitialWalletCreationDone(true);
					marketMakingMicroService.save(mm);
					logger.info(String.format("wallets for marketmaking %s has been created", mm));
				});
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			initBean.stopActionRunning("generateMMTransferWallet");
			logger.info("generateMMTransferWallet finished.");
		} else {
			logger.warn("Schedule method generateMMTransferWallet already running, skipping it.");
		}
	}
}
