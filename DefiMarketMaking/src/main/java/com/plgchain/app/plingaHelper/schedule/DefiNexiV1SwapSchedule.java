/**
 *
 */
package com.plgchain.app.plingaHelper.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.plgchain.app.plingaHelper.bean.InitBean;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 *
 */
@Component
public class DefiNexiV1SwapSchedule {
	private final static Logger logger = LoggerFactory.getLogger(DefiNexiV1SwapSchedule.class);

	@Inject
	private InitBean initBean;

	@PostConstruct
	@Transactional
	public void init() {
		logger.info("init for DefiNexiV1SwapSchedule has been runned.......");
	}

	@Transactional
	@Scheduled(cron = "0 */5 * * * *", zone = "GMT")
	public void defiNexiV1Swap() {
		if (!initBean.doesActionRunning("defiNexiV1Swap")) {
			initBean.startActionRunning("defiNexiV1Swap");
			logger.info("defiNexiV1Swap started.");




			initBean.stopActionRunning("defiNexiV1Swap");
			logger.info("defiNexiV1Swap finished.");
		} else {
			logger.warn("Schedule method defiNexiV1Swap already running, skipping it.");
		}
	}

}
