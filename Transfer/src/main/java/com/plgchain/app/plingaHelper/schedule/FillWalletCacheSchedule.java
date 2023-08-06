/**
 *
 */
package com.plgchain.app.plingaHelper.schedule;

import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.plgchain.app.plingaHelper.bean.InitBean;
import com.plgchain.app.plingaHelper.constant.WalletType;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.service.MarketMakingService;
import com.plgchain.app.plingaHelper.service.MarketMakingWalletService;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 *
 */
@Component
public class FillWalletCacheSchedule implements Serializable {

	private static final long serialVersionUID = 4204493912959339744L;

	private final static Logger logger = LoggerFactory.getLogger(FillWalletCacheSchedule.class);

	@Inject
	private InitBean initBean;

	@Inject
	private MarketMakingService marketMakingService;

	@Inject
	private MarketMakingWalletService marketMakingWalletService;

	@Transactional
	@Scheduled(cron = "0 */15 * * * *", zone = "GMT")
	public void fillWalletCache() {
		if (!initBean.doesActionRunning("fillWalletCache")) {
			initBean.startActionRunning("fillWalletCache");
			logger.info("fillWalletCache started.");
			marketMakingService.findByInitialWalletCreationDoneAndInitialWalletFundingDoneOrderByRandom(true, true)
					.stream().forEach(mm -> {
						SmartContract sm = mm.getSmartContract();
						int count = initBean.getCachedContracts() - initBean.getWalletCacheCount(sm.getContractId());
						if (count > 0) {
							logger.info(
									String.format("Try to load %s of contract %s", count, sm.getContractsAddress()));
							initBean.fillWalletCache(sm.getContractId(), marketMakingWalletService
									.findNWalletsRandomByContractIdAndWalletTypeNative(sm.getContractId(),WalletType.TRANSFER, count));
						} else {
							logger.info(String.format("contract %s is full in no need to fill",
									sm.getContractsAddress()));
						}
					});
			initBean.stopActionRunning("fillWalletCache");
			logger.info("fillWalletCache finished.");
		} else {
			logger.warn("Schedule method fillWalletCache already running, skipping it.");
		}
	}

}
