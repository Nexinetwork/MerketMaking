/**
 *
 */
package com.plgchain.app.plingaHelper.schedule;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.plgchain.app.plingaHelper.bean.InitBean;
import com.plgchain.app.plingaHelper.microService.BlockchainNodeMicroService;
import com.plgchain.app.plingaHelper.microService.BlockchainMicroService;
import com.plgchain.app.plingaHelper.util.blockchain.BlockchainUtil;

/**
 *
 */
@Component
public class BlockchainUpdateDetailsSchedule implements Serializable {

	private static final long serialVersionUID = 8715904411431836590L;

	private final static Logger logger = LoggerFactory.getLogger(BlockchainUpdateDetailsSchedule.class);

	@Autowired
	private InitBean initBean;

	@Autowired
	private BlockchainMicroService blockchainMicroService;

	@Autowired
	private BlockchainNodeMicroService blockchainNodeMicroService;

	@Scheduled(cron = "0 1 */2 * * *", zone = "GMT")
	public void blockchainUpdateDetails() {
		blockchainMicroService.findAll().stream().filter(blockchain -> blockchain.isEvm()).forEach(blockchain -> {
			try {
				if (!Strings.isNullOrEmpty(blockchain.getRpcUrl())) {
					BigInteger currentBlock = BlockchainUtil.getLatestBlockNumber(initBean.getHttpClient(),blockchain.getRpcUrl());
					long nodeCounts = blockchainNodeMicroService.countByBlockchain(blockchain);
					blockchain.setLastCheck(LocalDateTime.now());
					blockchain.setHealthy(true);
					blockchain.setNodeCount(Math.toIntExact(nodeCounts));
					blockchain.setHeight(currentBlock);
					blockchain = blockchainMicroService.save(blockchain);
					logger.info(String.format("Blockchain %s has been updated to %s", blockchain.getName(),
							blockchain.toString()));
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}

		});
		initBean.writeBlockchainToRedis();
	}

}
