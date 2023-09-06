/**
 *
 */
package com.plgchain.app.plingaHelper.schedule;

import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.plgchain.app.plingaHelper.bean.InitBean;
import com.plgchain.app.plingaHelper.constant.WalletType;
import com.plgchain.app.plingaHelper.entity.MMWallet;
import com.plgchain.app.plingaHelper.microService.MarketMakingMicroService;
import com.plgchain.app.plingaHelper.microService.MarketMakingWalletMicroService;
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
	private MarketMakingMicroService marketMakingMicroService;

	@Inject
	private MMWalletService mmWalletService;

	@Inject
	private MarketMakingWalletMicroService marketMakingWalletMicroService;

	@Inject
	private InitBean initBean;

	@Transactional
	@Scheduled(cron = "0 */5 * * * *", zone = "GMT")
	public void syncTransferWalletToMongo() {
	    if (!initBean.doesActionRunning("syncTransferWalletToMongo")) {
	        initBean.startActionRunning("syncTransferWalletToMongo");
	        logger.info("syncTransferWalletToMongo started.");

	        marketMakingMicroService.findByMustUpdateMongoTransfer(true)
	                .stream()
	                .filter(mm -> mm.isInitialWalletCreationDone() && !Strings.isNullOrEmpty(mm.getTrPid()))
	                .findFirst()
	                .ifPresent(mm -> {
	                    logger.info("Syncing data for marketmaking {}", mm.getMarketMakingId());
	                    mmWalletService.deleteByMarketMakingId(mm.getMarketMakingId());

	                    logger.info("All objects has been deleted for marketmaking {}", mm.getMarketMakingId());
	                    var walletList = marketMakingWalletMicroService
	                            .findAllWalletsByContractIdAndWalletTypeNative(mm.getSmartContract().getContractId(),
	                                    WalletType.TRANSFER);

	                    logger.info("{} wallets has been loaded for marketmaking {}", walletList.size(),
	                            mm.getMarketMakingId());

	                    var mmWallet = MMWallet.builder()
	                            .blockchain(mm.getSmartContract().getBlockchain().getName())
	                            .blockchainId(mm.getSmartContract().getBlockchain().getBlockchainId())
	                            .coin(mm.getSmartContract().getCoin().getName())
	                            .coinId(mm.getSmartContract().getCoin().getCoinId())
	                            .coinSymbol(mm.getSmartContract().getCoin().getSymbol())
	                            .contractAddress(mm.getSmartContract().getContractsAddress())
	                            .contractId(mm.getSmartContract().getContractId())
	                            .marketMakingId(mm.getMarketMakingId())
	                            .build();

	                    mmWalletService.saveWithChunk(mmWallet, walletList, initBean.getChunkSize(),
	                            initBean.getDefiWalletCount(), mm.getTrPid());

	                    logger.info("{} wallets saved to mongodb for marketmaking {}", walletList.size(),
	                            mm.getMarketMakingId());

	                    int chunkCount = (int) Math.ceil((double) walletList.size() / initBean.getChunkSize());
	                    mm.setChunkCount(chunkCount);
	                    mm.setMustUpdateMongoTransfer(false);
	                    mm.setMustUpdateMongoDefi(false);

	                    marketMakingMicroService.save(mm);

	                    logger.info("Sync completed for marketmaking {}", mm.getMarketMakingId());
	                });

	        initBean.stopActionRunning("syncTransferWalletToMongo");
	        logger.info("syncTransferWalletToMongo finished.");
	    } else {
	        logger.warn("Schedule method syncTransferWalletToMongo already running, skipping it.");
	    }
	}


}
