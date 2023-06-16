/**
 *
 */
package com.plgchain.app.plingaHelper.bean;

import java.io.Serializable;
import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.google.common.base.Strings;
import com.plgchain.app.plingaHelper.annotation.LogMethod;
import com.plgchain.app.plingaHelper.constant.AdminCommandType;
import com.plgchain.app.plingaHelper.constant.SysConstant;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.exception.RestActionError;
import com.plgchain.app.plingaHelper.service.BlockchainService;
import com.plgchain.app.plingaHelper.type.CommandToRun;

/**
 *
 */
@Component
public class BlockchainBean implements Serializable {

	private static final long serialVersionUID = -2749816508506842832L;

	@Autowired
	private BlockchainService blockchainService;

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@LogMethod
	public Blockchain createBlockchain(Blockchain blockchain) throws RestActionError {
		if (blockchain == null)
			throw new RestActionError("Blockchain is Null");
		if (Strings.isNullOrEmpty(blockchain.getName()))
			throw new RestActionError("Blockchain name is empty");
		if (Strings.isNullOrEmpty(blockchain.getMainCoin()))
			throw new RestActionError("Blockchain main coin is empty");
		if (Strings.isNullOrEmpty(blockchain.getBlockExplorer()))
			throw new RestActionError("Blockchain Blockexplorer is empty");
		if (blockchain.getBlockchainType() == null)
			throw new RestActionError("Blockchain explorer is empty");
		if (blockchain.isEvm()) {
			if (blockchain.getChainId() == null)
				throw new RestActionError("ChainId for evm blockchain should not be empty");
			if (blockchain.getChainId().compareTo(BigInteger.ZERO) <= 0)
				throw new RestActionError("ChainId for evm blockchain should be bigger than 0");
			if (blockchainService.existsBlockchainByChainId(blockchain.getChainId())) {
				throw new RestActionError("ChainId for evm blockchain Already exist");
			}
		}
		if (blockchainService.existsBlockchainByName(blockchain.getName().trim())) {
			throw new RestActionError("Blockchain name already exist");
		}
		if (blockchainService.existsBlockchainByMainCoin(blockchain.getMainCoin().trim())) {
			throw new RestActionError("Blockchain main coin already exist");
		}
		blockchain.setName(blockchain.getName().trim());
		blockchain.setMainCoin(blockchain.getMainCoin().trim());
		blockchain.setBlockDuration(2);
		blockchain.setEnabled(true);
		blockchain.setHealthy(true);
		blockchain.setNodeCount(0);
		blockchain.setHeight(BigInteger.ZERO);
		blockchain = blockchainService.save(blockchain);
		CommandToRun ctr = new CommandToRun();
		ctr.setAdminCommandType(AdminCommandType.UPDATEBLOCKCHAIN);
		kafkaTemplate.send(SysConstant.KAFKA_ADMIN_COMMAND, JSON.toJSONString(ctr));
		return blockchain;
	}

}
