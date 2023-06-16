/**
 *
 */
package com.plgchain.app.plingaHelper.bean;

import java.io.Serializable;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.plgchain.app.plingaHelper.constant.SysConstant;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.service.BlockchainService;

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

	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplate;

	@PostConstruct
	public void init() {
		writeBlockchainToRedis();
	}

	@SuppressWarnings("unchecked")
	public void writeBlockchainToRedis() {
		HashOperations<String, String, String> blockchainDataString = redisTemplate.opsForHash();
		for (Blockchain blockchain : blockchainService.findAll()) {
			if (blockchainDataString.hasKey(SysConstant.REDIS_BLOCKCHAIN_DATA, blockchain.getName()))
				blockchainDataString.delete(SysConstant.REDIS_BLOCKCHAIN_DATA, blockchain.getName());
			blockchainDataString.put(SysConstant.REDIS_BLOCKCHAIN_DATA, blockchain.getName(), JSON.toJSONString(blockchain));
		}
	}

}
