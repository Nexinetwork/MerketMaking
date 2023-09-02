package com.plgchain.app.plingaHelper.controller.godController;

import java.io.Serializable;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.plgchain.app.plingaHelper.bean.BlockchainBean;
import com.plgchain.app.plingaHelper.controller.BaseController;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.exception.RestActionError;
import com.plgchain.app.plingaHelper.service.CoinService;
import com.plgchain.app.plingaHelper.service.MarketMakingService;
import com.plgchain.app.plingaHelper.service.SmartContractService;
import com.plgchain.app.plingaHelper.type.request.CoinReq;
import com.plgchain.app.plingaHelper.type.request.ContractReq;
import com.plgchain.app.plingaHelper.type.request.MarketMakingReq;
import com.plgchain.app.plingaHelper.type.request.SmartContractReq;
import com.plgchain.app.plingaHelper.util.MessageResult;

import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/godaction")
@RequiredArgsConstructor
public class CoinController extends BaseController implements Serializable {

	private static final long serialVersionUID = -4038029722467775718L;

	private final static Logger logger = LoggerFactory.getLogger(CoinController.class);

	@Inject
	private BlockchainBean blockchainBean;

	@Inject
	private KafkaTemplate<String, String> kafkaTemplate;

	@Inject
	private CoinService coinService;

	@Inject
	private SmartContractService smartContractService;

	@Inject
	private MarketMakingService marketMakingService;

	@PostMapping("/coin/createNewCoin")
	public MessageResult createNewCoin(@RequestBody CoinReq coinReq) {
		try {
			var coin = blockchainBean.createNewCoin(coinReq);
			return success(String.format("Coin %s has been created.", coin));
		} catch (RestActionError e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			return error(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			return error(e.getMessage());
		}
	}

	@PostMapping("/contract/createNewSmartContract")
	public MessageResult createNewSmartContract(@RequestBody SmartContractReq scReq) {
		try {
			var contract = blockchainBean.createOrUpdateSmartContract(scReq);
			return success(String.format("Contract %s has been created.", contract));
		} catch (RestActionError e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			return error(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			return error(e.getMessage());
		}
	}

	@RequestMapping("/contract/getTankhahWalletListAsResult")
	public MessageResult getTankhahWalletListAsResult() {
		try {
			return success(blockchainBean.getTankhahWalletListAsResult());
		} catch (Exception e) {
			logger.error(e.getMessage());
			return error(e.getMessage());
		}
	}

	@PostMapping("/marketMaking/createOrUpdateMarketMaking")
	public MessageResult createOrUpdateMarketMaking(@RequestBody MarketMakingReq mmReq) {
		try {
			var mm = blockchainBean.createOrUpdateMarketMaking(mmReq);
			return success(String.format("Contract %s has been created.", mm));
		} catch (RestActionError e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			return error(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			return error(e.getMessage());
		}
	}

	@PostMapping("/marketMaking/findMarketmakingByBlockchainAndContractAddress")
	public MessageResult findMarketmakingByBlockchainAndContractAddress(@RequestBody ContractReq cReq) {
		if (cReq == null)
			return error("Input is null");
		if (Strings.isNullOrEmpty(cReq.getBlockchainName()))
			return error("Blockchain is null");
		if (Strings.isNullOrEmpty(cReq.getContract()))
			return error("Contract is null");
		return success(
				marketMakingService.findByBlockchainAndContractAddress(cReq.getBlockchainName(), cReq.getContract()));
	}

	@RequestMapping("/contract/findContractsByContractAddress")
	public MessageResult findContractsByContractAddress(@RequestBody String contractAddress) {
		logger.info("findContractsByContractAdress fired.");
		var result = smartContractService.findByContractsAddress(contractAddress)
		        .stream()
		        .map(SmartContract::getSmartContractRes)
		        .collect(Collectors.toList());
		    return success(result);
	}

}
