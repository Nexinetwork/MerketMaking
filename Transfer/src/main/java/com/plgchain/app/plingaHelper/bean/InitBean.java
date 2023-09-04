/**
 *
 */
package com.plgchain.app.plingaHelper.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.plgchain.app.plingaHelper.constant.WalletType;
import com.plgchain.app.plingaHelper.dto.EvmWalletDto;
import com.plgchain.app.plingaHelper.dto.MarketMakingWalletDto;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.entity.TempTankhahWallet;
import com.plgchain.app.plingaHelper.entity.coingecko.Coin;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.service.BlockchainService;
import com.plgchain.app.plingaHelper.service.CoinService;
import com.plgchain.app.plingaHelper.service.MarketMakingService;
import com.plgchain.app.plingaHelper.service.MarketMakingWalletService;
import com.plgchain.app.plingaHelper.service.TempTankhahWalletService;
import com.plgchain.app.plingaHelper.util.NumberUtil;
import com.plgchain.app.plingaHelper.util.blockchain.EvmWalletUtil;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
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

	private final int tmpTankhahWalletCount = 100;

	private int selectPageSize = 20000;

	public final int cachedContracts = 20000;

	private Map<Long, Set<MarketMakingWalletDto>> transferWalletMapCache = new HashMap<Long, Set<MarketMakingWalletDto>>();

	private Map<Long, List<EvmWalletDto>> tmpTankhahWallet;

	@Inject
	private MarketMakingWalletService marketMakingWalletService;

	@Inject
	private MarketMakingService marketMakingService;

	@Inject
	private BlockchainService blockchainService;

	@Inject
	private CoinService coinService;

	@Inject
	private TempTankhahWalletService tempTankhahWalletService;

	@PostConstruct
	public void init() {
		this.tmpTankhahWallet = new HashMap<Long, List<EvmWalletDto>>();
		// writeWalletDataToCache();
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

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public EvmWalletDto getRandomTmpTankhahWallet(SmartContract smartContract) {
		long contractId = smartContract.getContractId();

		return tmpTankhahWallet.computeIfAbsent(contractId, id -> {
			List<EvmWalletDto> lst;
			if (tempTankhahWalletService.existsBySmartContractAndWalletType(smartContract, WalletType.TRANSFER)) {
				lst = tempTankhahWalletService.findBySmartContractAndWalletType(smartContract, WalletType.TRANSFER)
						.stream().map(TempTankhahWallet::getAsEvmWalletDto).collect(Collectors.toList());
				logger.info(String.format("Temp tankhah for contract %s has been read from Database",
						smartContract.getContractsAddress()));
			} else {
				lst = EvmWalletUtil.generateRandomWallet(tmpTankhahWalletCount);
				var ttwLst = lst.stream().map(ewDto -> new TempTankhahWallet(ewDto)).peek(ttw -> {
					ttw.setSmartContract(smartContract);
					ttw.setWalletType(WalletType.TRANSFER);
				}).collect(Collectors.toList());

				tempTankhahWalletService.saveAll(ttwLst);
				logger.info(String.format("Temp tankhah for contract %s has been generated and saved to database.",
						smartContract.getContractsAddress()));
			}
			return lst;
		}).get(NumberUtil.generateRandomNumber(0, tmpTankhahWalletCount - 1, 0));
	}

	public List<EvmWalletDto> getTmpTankhahWallet(SmartContract smartContract) {
		return tmpTankhahWallet.get(smartContract.getContractId());
	}

	public void setNonceOfTmpTankhahWallet(SmartContract smartContract, EvmWalletDto ewd, BigInteger nonce) {
		tmpTankhahWallet.computeIfPresent(smartContract.getContractId(), (id, wallets) -> {
			for (int i = 0; i < wallets.size(); i++) {
				if (wallets.get(i).equals(ewd)) {
					if (ewd.getNonce() == null)
						ewd.setNonce(BigInteger.ZERO);
					ewd.setNonce(nonce);
					wallets.set(i, ewd);
					return wallets;
				}
			}
			return wallets;
		});
	}

	@Transactional
	public void writeWalletDataToCache() {
		marketMakingService.findByInitialWalletCreationDoneAndInitialWalletFundingDoneOrderByRandom(true, true).stream()
				.forEach(mm -> {
					SmartContract sm = mm.getSmartContract();
					transferWalletMapCache.put(sm.getContractId(),
							Set.copyOf(marketMakingWalletService.findNWalletsRandomByContractIdAndWalletTypeNative(
									sm.getContractId(), WalletType.TRANSFER, cachedContracts)));
					Coin coin = sm.getCoin();
					Blockchain blockchain = sm.getBlockchain();
					logger.info(String.format("Contract %s for coin %s and blockchain %s has been write to cache",
							sm.getContractsAddress(), coin.getSymbol(), blockchain.getName()));
				});
	}

	public int getWalletCacheCount(long contractId) {
		return transferWalletMapCache.get(contractId).size();
	}

	public void fillWalletCache(long contractId, List<MarketMakingWalletDto> lst) {
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

	public List<MarketMakingWalletDto> getNTransferWallet(long contractId, int count) {
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
