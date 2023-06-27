package com.plgchain.app.plingaHelper.schedule;

import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.plgchain.app.plingaHelper.bean.coingecko.CoingeckoBean;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Component
public class UpdateCoingeckoCoinListSchedule implements Serializable {

	private static final long serialVersionUID = 7504602259969637312L;

	private final static Logger logger = LoggerFactory.getLogger(UpdateCoingeckoCoinListSchedule.class);

	@Autowired
	private CoingeckoBean coingeckoBean;


	@Scheduled(cron = "0 30 1 * * *", zone = "GMT")
    @SchedulerLock(name = "TaskScheduler_scheduledTask", lockAtMostFor = "90m", lockAtLeastFor = "60m" )
	public void updateCoingeckoCoinList() {
		logger.info("Run Method updateCoingeckoCoinList...........");
		coingeckoBean.checkAndUpdateCoingeckoCoinListFull();
	}

}
