/**
 *
 */
package com.plgchain.app.plingaHelper.bean;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;

import org.slf4j.LoggerFactory;

import com.plgchain.app.plingaHelper.microService.MarketMakingWalletMicroService;
import com.plgchain.app.plingaHelper.util.blockchain.EVMUtil;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;

/**
 *
 */
public class WalletBean implements Serializable {

	private static final long serialVersionUID = 4650043527337545389L;

	@Inject
	private MarketMakingWalletMicroService marketMakingWalletMicroService;

	private static final Logger logger = LoggerFactory.getLogger(WalletBean.class);

	@Transactional
	public void correctBalanceInWallets() {
		var result = marketMakingWalletMicroService.findAll().stream().peek(wallet -> {
			var blockchain = wallet.getBlockchain();
			BigDecimal balance = BigDecimal.ZERO;
			boolean mostRetry = true;
			balance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(), wallet.getPublicKey());
		});
	}

}
