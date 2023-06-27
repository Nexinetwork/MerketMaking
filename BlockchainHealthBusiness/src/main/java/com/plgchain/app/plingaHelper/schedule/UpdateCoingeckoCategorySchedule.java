package com.plgchain.app.plingaHelper.schedule;

import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.plgchain.app.plingaHelper.bean.coingecko.CoingeckoBean;

import jakarta.inject.Inject;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Component
public class UpdateCoingeckoCategorySchedule implements Serializable {

	private static final long serialVersionUID = 7504602259969637312L;

	private final static Logger logger = LoggerFactory.getLogger(UpdateCoingeckoCategorySchedule.class);

	@Inject
	private CoingeckoBean coingeckoBean;


	@Scheduled(cron = "0 30 0 * * *", zone = "GMT")
    @SchedulerLock(name = "TaskScheduler_scheduledTask", lockAtMostFor = "60m", lockAtLeastFor = "30m" )
	public void updateCoingeckoCategory() {
		logger.info("Run Method updateCoingeckoCategory...........");
		coingeckoBean.updateCoingeckoCategoriesList();
	}

}
