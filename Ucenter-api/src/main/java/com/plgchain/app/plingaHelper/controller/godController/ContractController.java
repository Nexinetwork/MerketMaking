/**
 *
 */
package com.plgchain.app.plingaHelper.controller.godController;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson2.JSON;
import com.netflix.servo.util.Strings;
import com.plgchain.app.plingaHelper.bean.BlockchainBean;
import com.plgchain.app.plingaHelper.constant.AdminCommandType;
import com.plgchain.app.plingaHelper.constant.SysConstant;
import com.plgchain.app.plingaHelper.controller.BaseController;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.exception.RestActionError;
import com.plgchain.app.plingaHelper.microService.MarketMakingWalletMicroService;
import com.plgchain.app.plingaHelper.microService.SmartContractMicroService;
import com.plgchain.app.plingaHelper.microService.TankhahWalletMicroService;
import com.plgchain.app.plingaHelper.type.CommandToRun;
import com.plgchain.app.plingaHelper.type.request.GeneralReq;
import com.plgchain.app.plingaHelper.type.response.SmartContractRes;
import com.plgchain.app.plingaHelper.util.MessageResult;

import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

/**
 *
 */
@RestController
@RequestMapping("/api/v1/godaction")
@RequiredArgsConstructor
public class ContractController extends BaseController implements Serializable {
	private static final long serialVersionUID = -457174052880551315L;

	@Inject
	private BlockchainBean blockchainBean;

	@Inject
	private TankhahWalletMicroService tankhahWalletMicroService;

	@Inject
	private MarketMakingWalletMicroService marketMakingWalletMicroService;

	@Inject
	private KafkaTemplate<String, String> kafkaTemplate;

	@Inject
	private SmartContractMicroService smartContractMicroService;

	@RequestMapping("/contract/getContractByContractAddress")
	public MessageResult getContractByContractAddress(@RequestBody GeneralReq req) {
		if (req == null || Strings.isNullOrEmpty(req.getContractAddress())) {
			error("ContractAddress is null");
		}

		var smResList = smartContractMicroService.findByContractsAddress(req.getContractAddress())
				.stream().map(SmartContract::getSmartContractRes).collect(Collectors.toList());

		return success(smResList);
	}

	@RequestMapping("/contract/updateContractAddress")
	public MessageResult updateContractAddress(@RequestBody GeneralReq req) {
		try {
			if (req == null)
				error("ContractId is null");
			var sm = blockchainBean.getContract(req);
			CommandToRun ctr = new CommandToRun();
			ctr.setAdminCommandType(AdminCommandType.UPDATECONTRACTADDRESS);
			ctr.setLong1(sm.getContractId());
			ctr.setStr1(req.getStr1());
			kafkaTemplate.send(SysConstant.KAFKA_ADMIN_COMMAND, JSON.toJSONString(ctr));
			return success("Actions Successfully put in queue please be paitent.");
		} catch (RestActionError e) {
			return error(e.getMessage());
		}
	}

}
