/**
 *
 */
package com.plgchain.app.plingaHelper.bean;

import java.io.Serializable;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.microService.MMWalletMicroService;
import com.plgchain.app.plingaHelper.microService.MarketMakingMicroService;
import com.plgchain.app.plingaHelper.microService.MarketMakingWalletMicroService;
import com.plgchain.app.plingaHelper.microService.TankhahWalletMicroService;
import com.plgchain.app.plingaHelper.microService.TempTankhahWalletMicroService;
import com.plgchain.app.plingaHelper.service.MMWalletService;
import com.plgchain.app.plingaHelper.service.SmartContractService;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 *
 */
@Service
public class ContractActionBean implements Serializable {

	private static final long serialVersionUID = -6233120304915944668L;

	private static final Logger logger = LoggerFactory.getLogger(ContractActionBean.class);

	private final InitBean initBean;
	private final MarketMakingMicroService marketMakingMicroService;
	private final TransferBean transferBean;
	private final TankhahWalletMicroService tankhahWalletMicroService;
	private final MarketMakingWalletMicroService marketMakingWalletMicroService;
	private final SmartContractService smartContractService;
	private final BlockchainBean blockchainBean;
	private final TempTankhahWalletMicroService tempTankhahWalletMicroService;
	private final MMWalletService mmWalletService;

	@Inject
	public ContractActionBean(InitBean initBean, MarketMakingMicroService marketMakingMicroService,
			TransferBean transferBean, TankhahWalletMicroService tankhahWalletMicroService,
			MarketMakingWalletMicroService marketMakingWalletMicroService,
			SmartContractService smartContractService, BlockchainBean blockchainBean,
			MMWalletMicroService mmWalletMicroService, TempTankhahWalletMicroService tempTankhahWalletMicroService,
			MMWalletService mmWalletService) {
		this.initBean = initBean;
		this.marketMakingMicroService = marketMakingMicroService;
		this.transferBean = transferBean;
		this.tankhahWalletMicroService = tankhahWalletMicroService;
		this.marketMakingWalletMicroService = marketMakingWalletMicroService;
		this.smartContractService = smartContractService;
		this.blockchainBean = blockchainBean;
		this.tempTankhahWalletMicroService = tempTankhahWalletMicroService;
		this.mmWalletService = mmWalletService;
	}

	@Async
	@Transactional
	public void changeContractAddressOfContract(long contractId,String newAddress) {
		smartContractService.findById(contractId).ifPresent(sm -> {
			String oldAddress = sm.getContractsAddress();
			if (!smartContractService.existsSmartContractByBlockchainAndContractsAddress(sm.getBlockchain(), newAddress)) {
				var mm = marketMakingMicroService.findBySmartContract(sm).get();
				IntStream.range(0, mm.getChunkCount()).forEach(chunk -> {
					mmWalletService.findByContractIdAndChunk(contractId, chunk).ifPresent(mmw -> {
						mmw.setContractAddress(newAddress);
						mmw = mmWalletService.save(mmw);
						logger.info(String.format("(%s/%s) contract address for blockchain %s and coin %s has been updated from %s to %s", chunk + 1 ,mm.getChunkCount(),sm.getBlockchain().getName(),sm.getCoin().getSymbol(),oldAddress,newAddress));
					});
				});
				smartContractService.changeContractAddress(sm,newAddress);
				logger.info(String.format("Address of contractId %s on blockchain %s has been changed from %s to %s.", contractId,sm.getBlockchain().getName(),oldAddress,newAddress));
			}

		});
	}

}
