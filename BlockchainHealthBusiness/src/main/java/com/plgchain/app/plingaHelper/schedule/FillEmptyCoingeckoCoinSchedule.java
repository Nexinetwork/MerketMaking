package com.plgchain.app.plingaHelper.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.plgchain.app.plingaHelper.bean.InitBean;
import com.plgchain.app.plingaHelper.bean.coingecko.CoingeckoBean;
import com.plgchain.app.plingaHelper.microService.CoinMicroService;
import jakarta.inject.Inject;

@Component
public class FillEmptyCoingeckoCoinSchedule {

	private final static Logger logger = LoggerFactory.getLogger(FillEmptyCoingeckoCoinSchedule.class);

	private final CoinMicroService coinMicroService;
	private final CoingeckoBean coingeckoBean;

	@Inject
	private InitBean initBean;

	@Inject
	public FillEmptyCoingeckoCoinSchedule(CoinMicroService coinMicroService, CoingeckoBean coingeckoBean) {
		this.coinMicroService = coinMicroService;
		this.coingeckoBean = coingeckoBean;
	}

	@Scheduled(cron = "0 0 */1 * * *", zone = "GMT")
	public void fillEmptyCoingeckoCoin() {
		if (!initBean.doesActionRunning("fillEmptyCoingeckoCoin")) {
			initBean.startActionRunning("fillEmptyCoingeckoCoin");
			coinMicroService.findByCoingeckoJsonIsNullAndCoingeckoIdIsNotNull(10).forEach(coin -> {
				coingeckoBean.createOrUpdateCoingeckoCoin(coin.getCoingeckoId());
			});
			initBean.stopActionRunning("fillEmptyCoingeckoCoin");
		} else {
			logger.info("fillEmptyCoingeckoCoin already running skip it.");
		}
	}

}
