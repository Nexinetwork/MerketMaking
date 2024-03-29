package com.plgchain.app.plingaHelper.controller.godController;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson2.JSON;
import com.google.common.base.Strings;
import com.plgchain.app.plingaHelper.bean.BlockchainBean;
import com.plgchain.app.plingaHelper.constant.AdminCommandType;
import com.plgchain.app.plingaHelper.constant.SysConstant;
import com.plgchain.app.plingaHelper.controller.BaseController;
import com.plgchain.app.plingaHelper.dto.BlockchainNodeDto;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.exception.RestActionError;
import com.plgchain.app.plingaHelper.microService.CoinMicroService;
import com.plgchain.app.plingaHelper.type.CommandToRun;
import com.plgchain.app.plingaHelper.util.MessageResult;

import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/godaction")
@RequiredArgsConstructor
public class BlockchainController extends BaseController implements Serializable {

	private static final long serialVersionUID = -4038029722467775718L;

	private final static Logger logger = LoggerFactory.getLogger(BlockchainController.class);

	@Inject
	private BlockchainBean blockchainBean;

	@Inject
	private KafkaTemplate<String, String> kafkaTemplate;

	@Inject
	private CoinMicroService coinMicroService;

	@RequestMapping("/ping")
	public MessageResult ping() {
		try {
			return success("Pong God");
		} catch (Exception e) {
			logger.info("Error is : " + e.getMessage());
			return error(e.getMessage());
		}
	}

	@PostMapping("/blockchain/createNewBlockchain")
	public MessageResult createNewBlockchain(@RequestBody Blockchain blockchain) {
		try {
			blockchain = blockchainBean.createBlockchain(blockchain);
			return success(String.format("Blockchain %s has been created.", blockchain));
		} catch (RestActionError e) {
			// TODO Auto-generated catch block
			error(e.getMessage());
			return error(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return error(e.getMessage());
		}
	}

	@PostMapping("/blockchain/createNewNode")
	public MessageResult createNewBlockchainNode(@RequestBody BlockchainNodeDto node) {
		try {
			var result = blockchainBean.createBlockchainNode(node);
			CommandToRun ctr = new CommandToRun();
			ctr.setAdminCommandType(AdminCommandType.UPDATEBLOCKCHAIN);
			kafkaTemplate.send(SysConstant.KAFKA_ADMIN_COMMAND, JSON.toJSONString(ctr));
			return success(String.format("Blockchain node %s has been created.", result.getAsDto()));
		} catch (RestActionError e) {
			// TODO Auto-generated catch block
			error(e.getMessage());
			return error(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return error(e.getMessage());
		}
	}

	@PostMapping("/blockchain/deleteAllBlockchainNode")
	public MessageResult deleteAllBlockchainNode(@RequestBody String blockchain) {
		logger.info(String.format("Delete all node of blockchain %s", blockchain));
		if (Strings.isNullOrEmpty(blockchain))
			return error("Blockchain name is empty");
		try {
			Long count = blockchainBean.deleteAllNodesBlockchainNodes(blockchain.trim());
			CommandToRun ctr = new CommandToRun();
			ctr.setAdminCommandType(AdminCommandType.UPDATEBLOCKCHAIN);
			kafkaTemplate.send(SysConstant.KAFKA_ADMIN_COMMAND, JSON.toJSONString(ctr));
			return success(String.format("%s nodes of blockchain %s has been deleted..", count,blockchain));
		} catch (RestActionError e) {
			// TODO Auto-generated catch block
			return error(e.getMessage());
		} catch (Exception e) {
			return error(e.getMessage());
		}
	}

	@PostMapping("/coin/setCoinAsMustCheckByCoingeckoId")
	public MessageResult setCoinAsMustCheckByCoingeckoId(@RequestBody String coingeckoId) {
		try {
			if (Strings.isNullOrEmpty(coingeckoId))
				error("Coin is empty");
			if (!coinMicroService.existsCoinByCoingeckoId(coingeckoId))
				error("Invalid Coin");
			var result = coinMicroService.findByCoingeckoId(coingeckoId).get();
			result.setMustCheck(true);
			result = coinMicroService.save(result);
			CommandToRun ctr = new CommandToRun();
			ctr.setAdminCommandType(AdminCommandType.UPDATECOINS);
			kafkaTemplate.send(SysConstant.KAFKA_ADMIN_COMMAND, JSON.toJSONString(ctr));
			return success(result);
		} catch (Exception e) {
			e.printStackTrace();
			return error(e.getMessage());
		}
	}

	@PostMapping("/coin/setCoinAsMustNotCheckByCoingeckoId")
	public MessageResult setCoinAsMustNotCheckByCoingeckoId(@RequestBody String coingeckoId) {
		try {
			if (Strings.isNullOrEmpty(coingeckoId))
				error("Coin is empty");
			if (!coinMicroService.existsCoinByCoingeckoId(coingeckoId))
				error("Invalid Coin");
			var result = coinMicroService.findByCoingeckoId(coingeckoId).get();
			result.setMustCheck(false);
			result = coinMicroService.save(result);
			CommandToRun ctr = new CommandToRun();
			ctr.setAdminCommandType(AdminCommandType.UPDATECOINS);
			kafkaTemplate.send(SysConstant.KAFKA_ADMIN_COMMAND, JSON.toJSONString(ctr));
			return success(result);
		} catch (Exception e) {
			e.printStackTrace();
			return error(e.getMessage());
		}
	}

	@PostMapping("/blockchain/stopAllNodesOfBlockchain")
	public MessageResult stopAllNodesOfBlockchain(@RequestBody String blockchain) {
		try {
			if (Strings.isNullOrEmpty(blockchain))
				error("blockchain is empty");
			CommandToRun ctr = new CommandToRun();
			ctr.setAdminCommandType(AdminCommandType.STOPALLNODESOFBLOCKCHAIN);
			ctr.setStr1(blockchain);
			kafkaTemplate.send(SysConstant.KAFKA_ADMIN_COMMAND, JSON.toJSONString(ctr));
			return success("Blockchain stop command has been sent");
		} catch (Exception e) {
			e.printStackTrace();
			return error(e.getMessage());
		}
	}

	@PostMapping("/blockchain/startAllNodesOfBlockchain")
	public MessageResult startAllNodesOfBlockchain(@RequestBody String blockchain) {
		try {
			if (Strings.isNullOrEmpty(blockchain))
				error("blockchain is empty");
			CommandToRun ctr = new CommandToRun();
			ctr.setAdminCommandType(AdminCommandType.STARTALLNODESOFBLOCKCHAIN);
			ctr.setStr1(blockchain);
			kafkaTemplate.send(SysConstant.KAFKA_ADMIN_COMMAND, JSON.toJSONString(ctr));
			return success("Blockchain start command has been sent");
		} catch (Exception e) {
			e.printStackTrace();
			return error(e.getMessage());
		}
	}

	@PostMapping("/blockchain/restartAllNodesOfBlockchain")
	public MessageResult restartAllNodesOfBlockchain(@RequestBody String blockchain) {
		try {
			if (Strings.isNullOrEmpty(blockchain))
				error("blockchain is empty");
			CommandToRun ctr = new CommandToRun();
			ctr.setAdminCommandType(AdminCommandType.RESTARTALLNODESOFBLOCKCHAIN);
			ctr.setStr1(blockchain);
			kafkaTemplate.send(SysConstant.KAFKA_ADMIN_COMMAND, JSON.toJSONString(ctr));
			return success("Blockchain restart command has been sent");
		} catch (Exception e) {
			e.printStackTrace();
			return error(e.getMessage());
		}
	}

}
