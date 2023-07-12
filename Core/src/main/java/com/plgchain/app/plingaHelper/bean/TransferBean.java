/**
 *
 */
package com.plgchain.app.plingaHelper.bean;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.plgchain.app.plingaHelper.constant.WalletType;
import com.plgchain.app.plingaHelper.dto.EvmWalletDto;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.entity.marketMaking.MarketMaking;
import com.plgchain.app.plingaHelper.entity.marketMaking.MarketMakingWallet;
import com.plgchain.app.plingaHelper.service.MarketMakingWalletService;
import com.plgchain.app.plingaHelper.util.blockchain.EvmWalletUtil;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 *
 */
@Component
public class TransferBean implements Serializable {

	private static final long serialVersionUID = -8213356057891230140L;

	private static final Logger logger = LoggerFactory.getLogger(TransferBean.class);

	@Inject
	private MarketMakingWalletService marketMakingWalletService;

	@Transactional
	public void generateWalletsForMarketmaking(MarketMaking mm, int jpaBatchCount, SmartContract contract, WalletType walletType) {
	    List<MarketMakingWallet> wList = EvmWalletUtil.generateRandomWallet(mm.getInitialWallet())
	            .stream()
	            .map(w -> MarketMakingWallet.builder()
	                    .balance(w.getBalance())
	                    .blockchain(contract.getBlockchain())
	                    .coin(contract.getCoin())
	                    .contract(contract)
	                    .contractAddress(contract.getContractsAddress())
	                    .privateKey(w.getPrivateKey())
	                    .publicKey(w.getPublicKey())
	                    .walletType(walletType)
	                    .build())
	            .collect(Collectors.toList());

	    marketMakingWalletService.batchSaveAll(wList, jpaBatchCount);
	}


}
