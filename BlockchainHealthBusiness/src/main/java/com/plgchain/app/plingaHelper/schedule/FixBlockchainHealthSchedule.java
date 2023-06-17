package com.plgchain.app.plingaHelper.schedule;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.JSchException;
import com.plgchain.app.plingaHelper.bean.InitBean;
import com.plgchain.app.plingaHelper.constant.BlockchainNodeType;
import com.plgchain.app.plingaHelper.constant.SysConstant;
import com.plgchain.app.plingaHelper.entity.BlockchainNode;
import com.plgchain.app.plingaHelper.util.ArrayListHelper;
import com.plgchain.app.plingaHelper.util.BlockscoutUtil;
import com.plgchain.app.plingaHelper.util.ServiceUtil;
import com.plgchain.app.plingaHelper.util.blockchain.BlockchainUtil;

@Component
public class FixBlockchainHealthSchedule implements Serializable {

	private static final long serialVersionUID = 7504602259969637312L;

	private final static Logger logger = LoggerFactory.getLogger(FixBlockchainHealthSchedule.class);

	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private InitBean initBean;

	@Scheduled(cron = "0 */15 * * * *")
	public void fixBlockchainHealth() {
		HashOperations<String, String, String> blockchainDataString = redisTemplate.opsForHash();
		Map<String, String> entries = blockchainDataString.entries(SysConstant.REDIS_NODE_DATA);
		entries.forEach((key, value) -> {
			List<BlockchainNode> blnLst = ArrayListHelper.parseJsonToArrayList(value, BlockchainNode.class);
			// logger.info(String.format("Blockchain %s has %s node and node lists are %s",
			// key,blnLst.size(),blnLst));
			ArrayListHelper.parseJsonToArrayList(value, BlockchainNode.class).parallelStream()
					.filter(blchNode -> blchNode.isEnabled() && blchNode.isMustCheck()).forEach(blockchainNode -> {
						if (blockchainNode.getNodeType().equals(BlockchainNodeType.BLOCKCHAINNODE)) {
							BigInteger currentBlock = BlockchainUtil.getLatestBlockNumber(blockchainNode.getRpcUrl());
							if (currentBlock == null) {
								logger.info(String.format(
										"Server %s with service %s return block null try to restart service.",
										blockchainNode.getServerIp(), blockchainNode.getServiceNeme()));
								try {
									ServiceUtil.restartService(blockchainNode.getServerIp(),
											blockchainNode.getSshPort(), initBean.getPrivateKey(),
											blockchainNode.getServiceNeme());
									logger.info(String.format("Server %s with service %s has been restarted.",
											blockchainNode.getServerIp(), blockchainNode.getServiceNeme()));
								} catch (JSchException e) {
									// TODO Auto-generated catch block
									logger.error(e.getMessage());
									// e.printStackTrace();
								}
							} else if (currentBlock.equals(BigInteger.ZERO)) {
								logger.info(String.format(
										"Server %s with service %s return block null try to restart service.",
										blockchainNode.getServerIp(), blockchainNode.getServiceNeme()));
								try {
									ServiceUtil.restartService(blockchainNode.getServerIp(),
											blockchainNode.getSshPort(), initBean.getPrivateKey(),
											blockchainNode.getServiceNeme());
									logger.info(String.format("Server %s with service %s has been restarted.",
											blockchainNode.getServerIp(), blockchainNode.getServiceNeme()));
								} catch (JSchException e) {
									// TODO Auto-generated catch block
									logger.error(e.getMessage());
									// e.printStackTrace();
								}
							} else {
								try {
									Thread.sleep(initBean.getDelayForCheckInSecond() * 1000);
									BigInteger newBlock = BlockchainUtil
											.getLatestBlockNumber(blockchainNode.getRpcUrl());
									if (newBlock.equals(currentBlock)) {
										logger.info(String.format(
												"Server %s with service %s has been has been same block %s after %s try to restart it",
												blockchainNode.getServerIp(), blockchainNode.getServiceNeme(),
												newBlock.toString(), initBean.getDelayForCheckInSecond()));
									} else {
										logger.info(String.format(
												"Server %s with service %s are healthy block changed from %s to %s after %s seconds",
												blockchainNode.getServerIp(), blockchainNode.getServiceNeme(),
												currentBlock.toString(), newBlock.toString(),
												initBean.getDelayForCheckInSecond()));
									}
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						} else {
							while (true) {
								BigInteger currentBlock = BlockscoutUtil.getLatestBlock(blockchainNode.getRpcUrl());
								if (currentBlock == null) {
									logger.info(String.format(
											"Blockscout Server %s with service %s return block null try to restart service.",
											blockchainNode.getServerIp(), blockchainNode.getServiceNeme()));
									try {
										ServiceUtil.restartService(blockchainNode.getServerIp(),
												blockchainNode.getSshPort(), initBean.getPrivateKey(),
												blockchainNode.getServiceNeme());
										logger.info(String.format(
												"Blockscout Server %s with service %s has been restarted.",
												blockchainNode.getServerIp(), blockchainNode.getServiceNeme()));
									} catch (JSchException e) {
										// TODO Auto-generated catch block
										logger.error(e.getMessage());
										// e.printStackTrace();
									}
								} else if (currentBlock.equals(BigInteger.ZERO)) {
									logger.info(String.format(
											"Blockscout Server %s with service %s return block null try to restart service.",
											blockchainNode.getServerIp(), blockchainNode.getServiceNeme()));
									try {
										ServiceUtil.restartService(blockchainNode.getServerIp(),
												blockchainNode.getSshPort(), initBean.getPrivateKey(),
												blockchainNode.getServiceNeme());
										logger.info(String.format(
												"Blockscout Server %s with service %s has been restarted.",
												blockchainNode.getServerIp(), blockchainNode.getServiceNeme()));
									} catch (JSchException e) {
										// TODO Auto-generated catch block
										logger.error(e.getMessage());
										// e.printStackTrace();
									}
								} else {
									try {
										Thread.sleep(initBean.getDelayForCheckInSecond() * 1000);
										BigInteger newBlock = BlockscoutUtil.getLatestBlock(blockchainNode.getRpcUrl());
										if (newBlock.equals(currentBlock)) {
											logger.info(String.format(
													"SBlockscout erver %s with service %s has been has been same block %s after %s try to restart it",
													blockchainNode.getServerIp(), blockchainNode.getServiceNeme(),
													newBlock.toString(), initBean.getDelayForCheckInSecond()));
										} else {
											logger.info(String.format(
													"Blockscout Server %s with service %s are healthy block changed from %s to %s after %s seconds",
													blockchainNode.getServerIp(), blockchainNode.getServiceNeme(),
													currentBlock.toString(), newBlock.toString(),
													initBean.getDelayForCheckInSecond()));
											break;
										}
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
						}
					});
		});
	}

}
