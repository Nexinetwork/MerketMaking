/**
 *
 */
package com.plgchain.app.plingaHelper.controller.godController;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.plgchain.app.plingaHelper.bean.BlockchainBean;
import com.plgchain.app.plingaHelper.controller.BaseController;
import com.plgchain.app.plingaHelper.entity.TankhahWallet;
import com.plgchain.app.plingaHelper.entity.marketMaking.MarketMakingWallet;
import com.plgchain.app.plingaHelper.service.MarketMakingWalletService;
import com.plgchain.app.plingaHelper.service.TankhahWalletService;
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
public class WalletControler extends BaseController implements Serializable {

	private static final long serialVersionUID = 1L;

	private final static Logger logger = LoggerFactory.getLogger(WalletControler.class);

	@Inject
	private BlockchainBean blockchainBean;

	@Inject
	private TankhahWalletService tankhahWalletService;

	@Inject
	private MarketMakingWalletService marketMakingWalletService;

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
		List<MarketMakingWalletRes> result = tankhahWalletService.findByPublicKey(publicKey)
		        .stream()
		        .map(TankhahWallet::getAsMarketMakingWalletRes)
		        .collect(Collectors.toList());
		    return success(result);
	}

	@RequestMapping("/wallet/getMarketMakingWalletByPublicKey")
	public MessageResult getMarketMakingWalletByPublicKey(@RequestBody String publicKey) {
	    logger.info("getMarketMakingWalletByPublicKey fired.");
	    List<MarketMakingWalletRes> result = marketMakingWalletService.findByPublicKey(publicKey)
	        .stream()
	        .map(MarketMakingWallet::getAsMarketMakingWalletRes)
	        .collect(Collectors.toList());
	    return success(result);
	}


}
