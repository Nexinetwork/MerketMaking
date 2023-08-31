/**
 *
 */
package com.plgchain.app.plingaHelper.schedule;

import java.io.Serializable;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.plgchain.app.plingaHelper.bean.InitBean;
import com.plgchain.app.plingaHelper.constant.WalletType;
import com.plgchain.app.plingaHelper.entity.MMWallet;
import com.plgchain.app.plingaHelper.service.MarketMakingService;
import com.plgchain.app.plingaHelper.service.MarketMakingWalletService;
import com.plgchain.app.plingaHelper.service.MMWalletService;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 *
 */
@Component
public class SyncTransferWalletToMongoSchedule implements Serializable {

	private static final long serialVersionUID = -682176671308893371L;
	private final static Logger logger = LoggerFactory.getLogger(SyncTransferWalletToMongoSchedule.class);

	@Inject
	private MarketMakingService marketMakingService;

	@Inject
	private MMWalletService mmWalletService;

	@Inject
	private MarketMakingWalletService marketMakingWalletService;

	@Inject
	private InitBean initBean;

	@Transactional
	@Scheduled(cron = "0 */15 * * * *", zone = "GMT")
	public void syncTransferWalletToMongo() {
		if (!initBean.doesActionRunning("syncTransferWalletToMongo")) {
			initBean.startActionRunning("syncTransferWalletToMongo");
			logger.info("fillWalletCache started.");
			marketMakingService.findByMustUpdateMongoTransfer(true).stream()
					.filter(mm -> mm.isInitialWalletCreationDone() && !Strings.isNullOrEmpty(mm.getTrPid()))
					.forEach(mm -> {
						mmWalletService.findById(mm.getMarketMakingId()).ifPresentOrElse(www -> {
							www.setTransferWalletList(
									marketMakingWalletService.findAllWalletsByContractIdAndWalletTypeNative(
											mm.getSmartContract().getContractId(), WalletType.TRANSFER));
							www = mmWalletService.save(www);
							mm.setMustUpdateMongoTransfer(false);
							marketMakingService.save(mm);
							logger.info(
									String.format("Contract %s of coin %s of blockchain %s has been updated in mongodb",
											mm.getSmartContract().getContractsAddress(),
											mm.getSmartContract().getCoin().getSymbol(),
											mm.getSmartContract().getBlockchain().getName()));
						}, () -> {
							var mmv = MMWallet.builder().marketMakingId(mm.getMarketMakingId())
									.blockchain(mm.getSmartContract().getBlockchain().getName())
									.blockchainId(mm.getSmartContract().getBlockchain().getBlockchainId())
									.coinId(mm.getSmartContract().getCoin().getCoinId())
									.coinSymbol(mm.getSmartContract().getCoin().getSymbol())
									.coin(mm.getSmartContract().getCoin().getName())
									.contractAddress(mm.getSmartContract().getContractsAddress())
									.contractId(mm.getSmartContract().getContractId())
									.tankhahDefiWalletList(new ArrayList<>())
									.tankhahTransferWalletList(new ArrayList<>()).defiWalletList(new ArrayList<>())
									.transferWalletList(new ArrayList<>()).build();

							mmv = mmWalletService.save(mmv);
							logger.info(
									String.format("Contract %s of coin %s of blockchain %s has been created in mongodb",
											mm.getSmartContract().getContractsAddress(),
											mm.getSmartContract().getCoin().getSymbol(),
											mm.getSmartContract().getBlockchain().getName()));
						});
					});

		} else {
			logger.warn("Schedule method syncTransferWalletToMongo already running, skipping it.");
		}
	}

}
