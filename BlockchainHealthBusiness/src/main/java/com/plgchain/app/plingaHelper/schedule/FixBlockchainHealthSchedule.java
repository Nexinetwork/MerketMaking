package com.plgchain.app.plingaHelper.schedule;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.plgchain.app.plingaHelper.constant.SysConstant;
import com.plgchain.app.plingaHelper.entity.BlockchainNode;
import com.plgchain.app.plingaHelper.util.ArrayListHelper;

@Component
public class FixBlockchainHealthSchedule implements Serializable {

	private static final long serialVersionUID = 7504602259969637312L;

	private final static Logger logger = LoggerFactory.getLogger(FixBlockchainHealthSchedule.class);

	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplate;

	@Scheduled(cron = "0 */15 * * * *")
	public void fixBlockchainHealth() {
		HashOperations<String, String, String> blockchainDataString = redisTemplate.opsForHash();
		Map<String, String> entries = blockchainDataString.entries(SysConstant.REDIS_NODE_DATA);
		entries.forEach((key, value) -> {
			List<BlockchainNode> blnLst = ArrayListHelper.parseJsonToArrayList(value, BlockchainNode.class);
			logger.info(String.format("Blockchain %s has %s node and node lists are %s", key,blnLst.size(),blnLst));
		});
	}

}
