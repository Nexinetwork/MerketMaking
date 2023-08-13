/**
 *
 */
package com.plgchain.app.plingaHelper.bean;

import java.io.Serializable;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSON;
import com.google.common.base.Strings;
import com.plgchain.app.plingaHelper.constant.SysConstant;
import com.plgchain.app.plingaHelper.entity.BlockchainNode;
import com.plgchain.app.plingaHelper.entity.SystemConfig;
import com.plgchain.app.plingaHelper.service.BlockchainNodeService;
import com.plgchain.app.plingaHelper.service.BlockchainService;
import com.plgchain.app.plingaHelper.service.SmartContractService;
import com.plgchain.app.plingaHelper.service.SystemConfigService;
import com.plgchain.app.plingaHelper.type.response.ContractMustAddResponse;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
@Component
@Data
@ToString
@NoArgsConstructor
public class InitBean implements Serializable {

	private static final long serialVersionUID = 2792293419508311430L;

	private final static Logger logger = LoggerFactory.getLogger(InitBean.class);

	@Inject
	private BlockchainService blockchainService;

	@Inject
	private SystemConfigService systemConfigService;

	@Inject
	private BlockchainNodeService blockchainNodeService;

	@SuppressWarnings("rawtypes")
	@Inject
	private RedisTemplate redisTemplate;

	@Inject
	private SmartContractService smartContractService;

	private String privateKey;

	private String coingeckoBaseApi;

	private int delayForCheckInSecond = 10;

	private boolean initCoingecko = false;

	private boolean checkNodeHealth = true;

	private List<String> lockedMethod = new ArrayList<String>();

	private HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

	@PostConstruct
	public void init() {
		writeBlockchainToRedis();
		loadConfigs();
	}

	public void loadConfigs() {
		Optional<SystemConfig> coingeckoBaseApiConfig = systemConfigService.findByConfigName("coingeckoBaseFreeApi");
	    Optional<SystemConfig> initCoingeckoConfig = systemConfigService.findByConfigName("initCoingecko");
	    Optional<SystemConfig> checkNodeHealthConfig = systemConfigService.findByConfigName("checkNodeHealth");
	    Optional<SystemConfig> privateKeyConfig = systemConfigService.findByConfigName("ssh-key-path");

	    coingeckoBaseApiConfig.ifPresent(config -> coingeckoBaseApi = config.getConfigStringValue());
	    initCoingeckoConfig.ifPresent(config -> initCoingecko = config.getConfigBooleanValue());
	    checkNodeHealthConfig.ifPresent(config -> checkNodeHealth = config.getConfigBooleanValue());
	    privateKeyConfig.ifPresent(config -> privateKey = config.getConfigStringValue());
	}

	public boolean doesActionRunning(String action) {
		return lockedMethod.contains(action);
	}

	public void startActionRunning(String action) {
		lockedMethod.add(action);
	}

	public void stopActionRunning(String action) {
		lockedMethod.remove(action);
	}

	@Transactional
	@SuppressWarnings("unchecked")
	public void writeBlockchainToRedis() {
		// Predicate<Blockchain> mustCheck = blockchain -> blockchain.isEnabled() &&
		// blockchain.isMustCheck();
		HashOperations<String, String, String> blockchainDataString = redisTemplate.opsForHash();
		blockchainService.findAll().stream().filter(blockchain -> blockchain.isEnabled() && blockchain.isMustCheck())
				.forEach(blockchain -> {
					if (blockchainDataString.hasKey(SysConstant.REDIS_BLOCKCHAIN_DATA, blockchain.getName()))
						blockchainDataString.delete(SysConstant.REDIS_BLOCKCHAIN_DATA, blockchain.getName());
					if (blockchainDataString.hasKey(SysConstant.REDIS_NODE_DATA, blockchain.getName()))
						blockchainDataString.delete(SysConstant.REDIS_NODE_DATA, blockchain.getName());
					blockchainDataString.put(SysConstant.REDIS_BLOCKCHAIN_DATA, blockchain.getName(),
							JSON.toJSONString(blockchain));
					List<BlockchainNode> blNodeList = blockchainNodeService.findByBlockchain(blockchain);
					if (!blNodeList.isEmpty()) {
						logger.info(String.format("There is %s node for blockchain %s", blNodeList.size(),
								blockchain.getName()));
						blockchainDataString.put(SysConstant.REDIS_NODE_DATA, blockchain.getName(),
								JSON.toJSONString(blNodeList));
					} else {
						logger.info(String.format("There is not any node for blockchain %s", blockchain.getName()));
					}
				});
		smartContractService.findByMustAdd(true).stream().filter(smartContract -> smartContract != null && !Strings.isNullOrEmpty(smartContract.getCoin().getCoingeckoId())).forEach(smartContract -> {
			if (blockchainDataString.hasKey(SysConstant.REDIS_CONTRACTS_MUSTADD_DATA,
					smartContract.getCoin().getCoingeckoId()))
				blockchainDataString.delete(SysConstant.REDIS_CONTRACTS_MUSTADD_DATA,
						smartContract.getCoin().getCoingeckoId());
			var smc = ContractMustAddResponse.builder()
					.blockchainCoingeckoId(smartContract.getBlockchain().getCoingeckoId())
					.contract(smartContract.getContractsAddress())
					.coinCoingeckoId(smartContract.getCoin().getCoingeckoId()).decimal(smartContract.getDecimal())
					.build();
			blockchainDataString.put(SysConstant.REDIS_CONTRACTS_MUSTADD_DATA, smartContract.getCoin().getCoingeckoId(),
					JSON.toJSONString(smc));
		});
	}

}
