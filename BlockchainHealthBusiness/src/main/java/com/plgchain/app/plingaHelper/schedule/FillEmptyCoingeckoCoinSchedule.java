package com.plgchain.app.plingaHelper.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.plgchain.app.plingaHelper.bean.InitBean;
import com.plgchain.app.plingaHelper.bean.coingecko.CoingeckoBean;
import com.plgchain.app.plingaHelper.microService.CoinService;
import jakarta.inject.Inject;

@Component
public class FillEmptyCoingeckoCoinSchedule {

	private final static Logger logger = LoggerFactory.getLogger(FillEmptyCoingeckoCoinSchedule.class);

	private final CoinService coinService;
	private final CoingeckoBean coingeckoBean;

	@Inject
	private InitBean initBean;

	@Inject
	public FillEmptyCoingeckoCoinSchedule(CoinService coinService, CoingeckoBean coingeckoBean) {
		this.coinService = coinService;
		this.coingeckoBean = coingeckoBean;
	}

	@Scheduled(cron = "0 0 */1 * * *", zone = "GMT")
	public void fillEmptyCoingeckoCoin() {
		if (!initBean.doesActionRunning("fillEmptyCoingeckoCoin")) {
			initBean.startActionRunning("fillEmptyCoingeckoCoin");
			coinService.findByCoingeckoJsonIsNullAndCoingeckoIdIsNotNull(10).forEach(coin -> {
				coingeckoBean.createOrUpdateCoingeckoCoin(coin.getCoingeckoId());
			});
			initBean.stopActionRunning("fillEmptyCoingeckoCoin");
		} else {
			logger.info("fillEmptyCoingeckoCoin already running skip it.");
		}
	}

}
