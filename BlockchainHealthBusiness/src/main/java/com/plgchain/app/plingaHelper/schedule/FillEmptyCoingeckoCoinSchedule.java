package com.plgchain.app.plingaHelper.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.plgchain.app.plingaHelper.bean.coingecko.CoingeckoBean;
import com.plgchain.app.plingaHelper.service.CoinService;
import jakarta.inject.Inject;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Component
public class FillEmptyCoingeckoCoinSchedule {

	private final static Logger logger = LoggerFactory.getLogger(FillEmptyCoingeckoCoinSchedule.class);

	private final CoinService coinService;
	private final CoingeckoBean coingeckoBean;

	@Inject
	public FillEmptyCoingeckoCoinSchedule(CoinService coinService, CoingeckoBean coingeckoBean) {
		this.coinService = coinService;
		this.coingeckoBean = coingeckoBean;
	}

	@Scheduled(cron = "0 */10 * * * *", zone = "GMT")
	@SchedulerLock(name = "TaskScheduler_scheduledTask", lockAtMostFor = "60m", lockAtLeastFor = "30m" )
	public void fillEmptyCoingeckoCoin() {
		coinService.findByCoingeckoJsonIsNull(10).forEach(coin -> {
			coingeckoBean.createOrUpdateCoingeckoCoin(coin.getCoingeckoId());
		});
	}

}
