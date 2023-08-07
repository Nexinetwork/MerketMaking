/**
 *
 */
package com.plgchain.app.plingaHelper.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.plgchain.app.plingaHelper.constant.WalletType;
import com.plgchain.app.plingaHelper.dto.MarketMakingWalletDto;
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

	private final int sleepInSeconds = 5;

	private int selectPageSize = 20000;

	public final int cachedContracts = 20000;

	private Map<Long, Set<MarketMakingWalletDto>> transferWalletMapCache = new HashMap<Long, Set<MarketMakingWalletDto>>();

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
		logger.info("111111111111111111111111111111111111111111111111111111111111111111111111111");
		//writeWalletDataToCache();
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
			transferWalletMapCache.put(sm.getContractId(), Set.copyOf(marketMakingWalletService.findNWalletsRandomByContractIdAndWalletTypeNative(sm.getContractId(),WalletType.TRANSFER,cachedContracts)));
			Coin coin = sm.getCoin();
			Blockchain blockchain = sm.getBlockchain();
			logger.info(String.format("Contract %s for coin %s and blockchain %s has been write to cache", sm.getContractsAddress(),coin.getSymbol(),blockchain.getName()));
		});
	}

	public int getWalletCacheCount(long contractId) {
		return transferWalletMapCache.get(contractId).size();
	}

	public void fillWalletCache(long contractId,List<MarketMakingWalletDto> lst) {
		transferWalletMapCache.get(contractId).addAll(Set.copyOf(lst));
	}

	public List<MarketMakingWalletDto> getAllWalletsByContractId(long contractId) {
		return List.copyOf(transferWalletMapCache.get(contractId));
	}

	public Page<MarketMakingWalletDto> getMMWalletList(long contractId, Pageable pageable) {
	    List<MarketMakingWalletDto> walletDtos = List.copyOf(transferWalletMapCache.get(contractId));

	    int start = (int) pageable.getOffset();
	    int end = Math.min((start + pageable.getPageSize()), walletDtos.size());
	    List<MarketMakingWalletDto> sublist = walletDtos.subList(start, end);

	    return new PageImpl<>(sublist, pageable, walletDtos.size());
	}

	public List<MarketMakingWalletDto> getNTransferWallet(long contractId,int count) {
		List<MarketMakingWalletDto> walletDtos = List.copyOf(transferWalletMapCache.get(contractId));
		if (walletDtos.size() <= count) {
			transferWalletMapCache.put(contractId, new HashSet<MarketMakingWalletDto>());
			return walletDtos;
		} else {
			var newList = walletDtos.subList(0, count);
			walletDtos.removeAll(newList);
			transferWalletMapCache.put(contractId, Set.copyOf(walletDtos));
			return newList;
		}
	}


}
