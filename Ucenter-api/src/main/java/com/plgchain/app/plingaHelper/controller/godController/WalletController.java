/**
 *
 */
package com.plgchain.app.plingaHelper.controller.godController;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson2.JSON;
import com.plgchain.app.plingaHelper.bean.BlockchainBean;
import com.plgchain.app.plingaHelper.constant.AdminCommandType;
import com.plgchain.app.plingaHelper.constant.SysConstant;
import com.plgchain.app.plingaHelper.controller.BaseController;
import com.plgchain.app.plingaHelper.entity.TankhahWallet;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.entity.marketMaking.MarketMakingWallet;
import com.plgchain.app.plingaHelper.exception.RestActionError;
import com.plgchain.app.plingaHelper.microService.MarketMakingWalletMicroService;
import com.plgchain.app.plingaHelper.microService.SmartContractMicroService;
import com.plgchain.app.plingaHelper.microService.TankhahWalletMicroService;
import com.plgchain.app.plingaHelper.type.CommandToRun;
import com.plgchain.app.plingaHelper.type.request.GeneralReq;
import com.plgchain.app.plingaHelper.type.response.MarketMakingWalletRes;
import com.plgchain.app.plingaHelper.util.MessageResult;

import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

/**
 *
 */
@RestController
@RequestMapping("/api/v1/godaction")
@RequiredArgsConstructor
public class WalletController extends BaseController implements Serializable {

	private static final long serialVersionUID = 1L;

	private final static Logger logger = LoggerFactory.getLogger(WalletController.class);

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

	@RequestMapping("/wallet/fixWalletPrivatekeys")
	public MessageResult fixWalletPrivatekeys() {
		logger.info("fixWalletPrivatekeys fired.");
		try {
			blockchainBean.fixWalletPrivatekeys();
			return success("All wallets has been fixed.");
		} catch (Exception e) {
			e.printStackTrace();
			return error(e.getMessage());
		}
	}

	@RequestMapping("/wallet/getTankhahWalletByPublicKey")
	public MessageResult getTankhahWalletByPublicKey(@RequestBody String publicKey) {
		logger.info("getTankhahWalletByPublicKey fired.");
		List<MarketMakingWalletRes> result = tankhahWalletMicroService.findByPublicKey(publicKey).stream()
				.map(TankhahWallet::getAsMarketMakingWalletRes).collect(Collectors.toList());
		return success(result);
	}

	@RequestMapping("/wallet/getMarketMakingWalletByPublicKey")
	public MessageResult getMarketMakingWalletByPublicKey(@RequestBody String publicKey) {
		logger.info("getMarketMakingWalletByPublicKey fired.");
		List<MarketMakingWalletRes> result = marketMakingWalletMicroService.findByPublicKey(publicKey).stream()
				.map(MarketMakingWallet::getAsMarketMakingWalletRes).collect(Collectors.toList());
		return success(result);
	}

	@RequestMapping("/wallet/correctMetamaskTransWalletsFunding")
	public MessageResult correctMetamaskTransWalletsFunding(@RequestBody Long contractId) {
		if (contractId == null)
			error("ContractId is null");
		if (contractId < 0)
			error("ContractId is null");
		Optional<SmartContract> sm = smartContractMicroService.findById(contractId);
		if (sm.isEmpty())
			error("Invalid contractId.");
		CommandToRun ctr = new CommandToRun();
		ctr.setAdminCommandType(AdminCommandType.FIXTRANSFERWALLETFUNDING);
		ctr.setLong1(contractId);
		kafkaTemplate.send(SysConstant.KAFKA_ADMIN_COMMAND, JSON.toJSONString(ctr));
		return success("Actions Successfully put in queue please be paitent.");
	}

	@RequestMapping("/wallet/correctMetamaskTransWalletsFundingReverse")
	public MessageResult correctMetamaskTransWalletsFundingReverse(@RequestBody Long contractId) {
		if (contractId == null)
			error("ContractId is null");
		if (contractId < 0)
			error("ContractId is null");
		Optional<SmartContract> sm = smartContractMicroService.findById(contractId);
		if (sm.isEmpty())
			error("Invalid contractId.");
		CommandToRun ctr = new CommandToRun();
		ctr.setAdminCommandType(AdminCommandType.FIXTRANSFERWALLETFUNDINGREVERSE);
		ctr.setLong1(contractId);
		kafkaTemplate.send(SysConstant.KAFKA_ADMIN_COMMAND, JSON.toJSONString(ctr));
		return success("Actions Successfully put in queue please be paitent.");
	}

	@RequestMapping("/wallet/backAllTokensToTankhah")
	public MessageResult backAllTokensToTankhah(@RequestBody GeneralReq req) {
		try {
			if (req == null)
				error("ContractId is null");
			var sm = blockchainBean.getContract(req);
			CommandToRun ctr = new CommandToRun();
			ctr.setAdminCommandType(AdminCommandType.BACKALLTOKENTOTANKHAH);
			ctr.setLong1(sm.getContractId());
			ctr.setInt1(req.getInt1());
			kafkaTemplate.send(SysConstant.KAFKA_ADMIN_COMMAND, JSON.toJSONString(ctr));
			return success("Actions Successfully put in queue please be paitent.");
		} catch (RestActionError e) {
			return error(e.getMessage());
		}
	}

	@RequestMapping("/wallet/backAllTokensToTankhahParallel")
	public MessageResult backAllTokensToTankhahParallel(@RequestBody GeneralReq req) {
		try {
			if (req == null)
				error("ContractId is null");
			var sm = blockchainBean.getContract(req);
			CommandToRun ctr = new CommandToRun();
			ctr.setAdminCommandType(AdminCommandType.BACKALLTOKENTOTANKHAHPPARALLEL);
			ctr.setLong1(sm.getContractId());
			ctr.setInt1(req.getInt1());
			kafkaTemplate.send(SysConstant.KAFKA_ADMIN_COMMAND, JSON.toJSONString(ctr));
			return success("Actions Successfully put in queue please be paitent.");
		} catch (RestActionError e) {
			return error(e.getMessage());
		}
	}

	@RequestMapping("/wallet/updateAllwalletsBalancesByContractId")
	public MessageResult updateAllwalletsBalancesByContractId(@RequestBody GeneralReq req) {
		try {
			if (req == null)
				error("ContractId is null");
			var sm = blockchainBean.getContract(req);
			CommandToRun ctr = new CommandToRun();
			ctr.setAdminCommandType(AdminCommandType.UPDATEALLWALLETSBALANCESBYCONTRACTID);
			ctr.setLong1(sm.getContractId());
			ctr.setInt1(req.getInt1());
			kafkaTemplate.send(SysConstant.KAFKA_ADMIN_COMMAND, JSON.toJSONString(ctr));
			return success("Actions Successfully put in queue please be paitent.");
		} catch (RestActionError e) {
			return error(e.getMessage());
		}
	}

	@RequestMapping("/wallet/updateAllwalletsBalancesByContractIdParallel")
	public MessageResult updateAllwalletsBalancesByContractIdParallel(@RequestBody GeneralReq req) {
		try {
			if (req == null)
				error("ContractId is null");
			var sm = blockchainBean.getContract(req);
			CommandToRun ctr = new CommandToRun();
			ctr.setAdminCommandType(AdminCommandType.UPDATEALLWALLETSBALANCESBYCONTRACTIDPARALLEL);
			ctr.setLong1(sm.getContractId());
			ctr.setInt1(req.getInt1());
			kafkaTemplate.send(SysConstant.KAFKA_ADMIN_COMMAND, JSON.toJSONString(ctr));
			return success("Actions Successfully put in queue please be paitent.");
		} catch (RestActionError e) {
			return error(e.getMessage());
		}
	}

	@RequestMapping("/wallet/creditMinimumMainCoinForTokenWallets")
	public MessageResult creditMinimumMainCoinForTokenWallets(@RequestBody GeneralReq req) {
		try {
			if (req == null)
				error("ContractId is null");
			var sm = blockchainBean.getContract(req);
			CommandToRun ctr = new CommandToRun();
			ctr.setAdminCommandType(AdminCommandType.CREDITMAINCOINBALANCEINTOKENWALLETS);
			ctr.setLong1(sm.getContractId());
			ctr.setInt1(req.getInt1());
			kafkaTemplate.send(SysConstant.KAFKA_ADMIN_COMMAND, JSON.toJSONString(ctr));
			return success("Actions Successfully put in queue please be paitent.");
		} catch (RestActionError e) {
			return error(e.getMessage());
		}
	}

	@RequestMapping("/wallet/creditMinimumMainCoinForTokenWalletsReverse")
	public MessageResult creditMinimumMainCoinForTokenWalletsReverse(@RequestBody GeneralReq req) {
		try {
			if (req == null)
				error("ContractId is null");
			var sm = blockchainBean.getContract(req);
			CommandToRun ctr = new CommandToRun();
			ctr.setAdminCommandType(AdminCommandType.CREDITMAINCOINBALANCEINTOKENWALLETSREVERSE);
			ctr.setLong1(sm.getContractId());
			ctr.setInt1(req.getInt1());
			kafkaTemplate.send(SysConstant.KAFKA_ADMIN_COMMAND, JSON.toJSONString(ctr));
			return success("Actions Successfully put in queue please be paitent.");
		} catch (RestActionError e) {
			return error(e.getMessage());
		}
	}

	@RequestMapping("/wallet/backAllTokensToTankhahReverse")
	public MessageResult backAllTokensToTankhahReverse(@RequestBody GeneralReq req) {
		try {
			if (req == null)
				error("ContractId is null");
			var sm = blockchainBean.getContract(req);
			CommandToRun ctr = new CommandToRun();
			ctr.setAdminCommandType(AdminCommandType.BACKALLTOKENTOTANKHAHREVERSE);
			ctr.setLong1(sm.getContractId());
			ctr.setInt1(req.getInt1());
			kafkaTemplate.send(SysConstant.KAFKA_ADMIN_COMMAND, JSON.toJSONString(ctr));
			return success("Actions Successfully put in queue please be paitent.");
		} catch (RestActionError e) {
			return error(e.getMessage());
		}
	}

	@RequestMapping("/wallet/backAllTokensFromTempTankhahToTankhah")
	public MessageResult backAllTokensFromTempTankhahToTankhah(@RequestBody GeneralReq req) {
		try {
			if (req == null)
				error("ContractId is null");
			var sm = blockchainBean.getContract(req);
			CommandToRun ctr = new CommandToRun();
			ctr.setAdminCommandType(AdminCommandType.BACKALLFROMTMPTANKHAHTOTANKHAH);
			ctr.setLong1(sm.getContractId());
			ctr.setInt1(req.getInt1());
			kafkaTemplate.send(SysConstant.KAFKA_ADMIN_COMMAND, JSON.toJSONString(ctr));
			return success("Actions Successfully put in queue please be paitent.");
		} catch (RestActionError e) {
			return error(e.getMessage());
		}
	}

	@RequestMapping("/wallet/deleteTempTankhahWallet")
	public MessageResult deleteTempTankhahWallet(@RequestBody GeneralReq req) {
		try {
			if (req == null)
				error("ContractId is null");
			var sm = blockchainBean.getContract(req);
			CommandToRun ctr = new CommandToRun();
			ctr.setAdminCommandType(AdminCommandType.DELETETEMPTANKHAHWALLET);
			ctr.setLong1(sm.getContractId());
			ctr.setInt1(req.getInt1());
			kafkaTemplate.send(SysConstant.KAFKA_ADMIN_COMMAND, JSON.toJSONString(ctr));
			return success("Actions Successfully put in queue please be paitent.");
		} catch (RestActionError e) {
			return error(e.getMessage());
		}
	}

}
