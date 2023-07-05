package com.plgchain.app.plingaHelper.controller.godController;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.plgchain.app.plingaHelper.bean.BlockchainBean;
import com.plgchain.app.plingaHelper.controller.BaseController;
import com.plgchain.app.plingaHelper.dto.BlockchainNodeDto;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.entity.BlockchainNode;
import com.plgchain.app.plingaHelper.exception.RestActionError;
import com.plgchain.app.plingaHelper.util.MessageResult;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/godaction")
@RequiredArgsConstructor
public class BlockchainControler extends BaseController implements Serializable {

	private static final long serialVersionUID = -4038029722467775718L;

	private final static Logger logger = LoggerFactory.getLogger(BlockchainControler.class);

	@Autowired
	private BlockchainBean blockchainBean;

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

}
