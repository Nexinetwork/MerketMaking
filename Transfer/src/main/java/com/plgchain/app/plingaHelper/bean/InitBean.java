/**
 *
 */
package com.plgchain.app.plingaHelper.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.entity.coingecko.Coin;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.service.BlockchainService;
import com.plgchain.app.plingaHelper.service.CoinService;
import com.plgchain.app.plingaHelper.service.MarketMakingService;
import com.plgchain.app.plingaHelper.service.MarketMakingWalletService;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 */
@Component
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InitBean implements Serializable {

	private static final long serialVersionUID = 2792293419508311430L;

	private final static Logger logger = LoggerFactory.getLogger(InitBean.class);

	private Set<String> lockedMethod = new HashSet<String>();

	private HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

	private int jpaBatchCount = 2000;

	private int fixTransferWalletBalancePerRound = 500;

	private int transferPerRound = 1000;

	private BigDecimal minMaincoinInContractWallet = new BigDecimal(2);

	private BigDecimal maxMaincoinInContractWallet = new BigDecimal(10);

	private int decimalMaincoinInContractWallet = 2;

	private int selectPageSize = 20000;

	@Inject
	private MarketMakingWalletService marketMakingWalletService;

	@Inject
	private MarketMakingService marketMakingService;

	@Inject
	private BlockchainService blockchainService;

	@Inject
	private CoinService coinService;

	@PostConstruct
	public void init() {
		writeWalletDataToCache();
	}

	public boolean doesActionRunning(String action) {
		return lockedMethod.contains(action);
	}

	public void startActionRunning(String action) {
		lockedMethod.add(action);
	}

	public void stopActionRunning(String action) {
		lockedMethod.remove(action);
	}

	@Transactional
	public void writeWalletDataToCache() {
		marketMakingService.findByInitialWalletCreationDoneAndInitialWalletFundingDoneOrderByRandom(true, true)
		.stream().forEach(mm -> {
			SmartContract sm = mm.getSmartContract();
			Coin coin = sm.getCoin();
			Blockchain blockchain = sm.getBlockchain();
			marketMakingWalletService.findAllWalletsByContractIdNativeAsCache(sm.getContractId());
			logger.info(String.format("Contract %s for coin %s and blockchain %s has been write to cache", sm.getContractsAddress(),coin.getSymbol(),blockchain.getName()));
		});
	}

}
