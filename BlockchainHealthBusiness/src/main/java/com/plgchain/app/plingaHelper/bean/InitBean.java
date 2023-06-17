/**
 *
 */
package com.plgchain.app.plingaHelper.bean;

import java.io.Serializable;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.plgchain.app.plingaHelper.constant.SysConstant;
import com.plgchain.app.plingaHelper.entity.BlockchainNode;
import com.plgchain.app.plingaHelper.service.BlockchainNodeService;
import com.plgchain.app.plingaHelper.service.BlockchainService;
import com.plgchain.app.plingaHelper.service.SystemConfigService;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
@Service
@Data
@ToString
@NoArgsConstructor
public class InitBean implements Serializable {

	private static final long serialVersionUID = 2792293419508311430L;

	private final static Logger logger = LoggerFactory.getLogger(InitBean.class);

	@Autowired
	private BlockchainService blockchainService;

	@Autowired
	private SystemConfigService systemConfigService;

	@Autowired
	private BlockchainNodeService blockchainNodeService;

	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplate;

	private String privateKey;

	private String coingeckoBaseApi;

	private int delayForCheckInSecond = 5;

	@PostConstruct
	public void init() {
		writeBlockchainToRedis();
		if (systemConfigService.isByConfigNameExist("ssh-key-path"))
			privateKey = systemConfigService.findByConfigName("ssh-key-path").getConfigStringValue();
		if (systemConfigService.isByConfigNameExist("coingeckoBaseFreeApi"))
			coingeckoBaseApi = systemConfigService.findByConfigName("coingeckoBaseFreeApi").getConfigStringValue();
	}

	@SuppressWarnings("unchecked")
	public void writeBlockchainToRedis() {
		//Predicate<Blockchain> mustCheck = blockchain -> blockchain.isEnabled() && blockchain.isMustCheck();
		HashOperations<String, String, String> blockchainDataString = redisTemplate.opsForHash();
		blockchainService.findAll().stream().filter(blockchain -> blockchain.isEnabled() && blockchain.isMustCheck()).forEach(blockchain -> {
			if (blockchainDataString.hasKey(SysConstant.REDIS_BLOCKCHAIN_DATA, blockchain.getName()))
				blockchainDataString.delete(SysConstant.REDIS_BLOCKCHAIN_DATA, blockchain.getName());
			blockchainDataString.put(SysConstant.REDIS_BLOCKCHAIN_DATA, blockchain.getName(), JSON.toJSONString(blockchain));
			List<BlockchainNode> blNodeList = blockchainNodeService.findByBlockchain(blockchain);
			if (!blNodeList.isEmpty()) {
				logger.info(String.format("There is %s node for blockchain %s", blNodeList.size(),blockchain.getName()));
				if (blockchainDataString.hasKey(SysConstant.REDIS_NODE_DATA, blockchain.getName()))
					blockchainDataString.delete(SysConstant.REDIS_NODE_DATA, blockchain.getName());
				blockchainDataString.put(SysConstant.REDIS_NODE_DATA, blockchain.getName(), JSON.toJSONString(blNodeList));
			} else {
				logger.info(String.format("There is not any node for blockchain %s", blockchain.getName()));
			}
		});
	}

}
