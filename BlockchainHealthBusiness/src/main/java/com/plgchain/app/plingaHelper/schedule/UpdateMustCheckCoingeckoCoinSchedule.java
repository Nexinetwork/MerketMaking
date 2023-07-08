package com.plgchain.app.plingaHelper.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.plgchain.app.plingaHelper.bean.InitBean;
import com.plgchain.app.plingaHelper.bean.coingecko.CoingeckoBean;
import com.plgchain.app.plingaHelper.service.CoinService;
import jakarta.inject.Inject;

@Component
public class UpdateMustCheckCoingeckoCoinSchedule {

	private final static Logger logger = LoggerFactory.getLogger(UpdateMustCheckCoingeckoCoinSchedule.class);

	private final CoinService coinService;
	private final CoingeckoBean coingeckoBean;

	@Inject
	private InitBean initBean;

	@Inject
	public UpdateMustCheckCoingeckoCoinSchedule(CoinService coinService, CoingeckoBean coingeckoBean) {
		this.coinService = coinService;
		this.coingeckoBean = coingeckoBean;
	}

	@Scheduled(cron = "0 */15 * * * *", zone = "GMT")
	public void updateMustCheckCoingeckoCoinS() {
		if (!initBean.doesActionRunning("updateMustCheckCoingeckoCoinS")) {
			initBean.startActionRunning("updateMustCheckCoingeckoCoinS");
			coinService.findByMustCheck(true).forEach(coin -> {
				coingeckoBean.createOrUpdateCoingeckoCoin(coin.getCoingeckoId());
			});
			initBean.stopActionRunning("updateMustCheckCoingeckoCoinS");
		} else {
			logger.info("updateMustCheckCoingeckoCoinS already running skip it.");
		}
	}

}
