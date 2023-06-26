/**
 *
 */
package com.plgchain.app.plingaHelper.bean.coingecko;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.netflix.servo.util.Strings;
import com.plgchain.app.plingaHelper.bean.InitBean;
import com.plgchain.app.plingaHelper.coingecko.request.CoinNetwork;
import com.plgchain.app.plingaHelper.coingecko.request.MustAddContractReq;
import com.plgchain.app.plingaHelper.coingecko.type.AssetPlatform;
import com.plgchain.app.plingaHelper.coingecko.type.CoingeckoUtil;
import com.plgchain.app.plingaHelper.constant.BlockchainTechType;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.entity.coingecko.Coin;
import com.plgchain.app.plingaHelper.entity.coingecko.CoinList;
import com.plgchain.app.plingaHelper.entity.coingecko.CoingeckoCategory;
import com.plgchain.app.plingaHelper.entity.coingecko.Currency;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.service.BlockchainService;
import com.plgchain.app.plingaHelper.service.CoinListHistoryService;
import com.plgchain.app.plingaHelper.service.CoinListService;
import com.plgchain.app.plingaHelper.service.CoinService;
import com.plgchain.app.plingaHelper.service.CoingeckoCategoryService;
import com.plgchain.app.plingaHelper.service.CurrencyService;
import com.plgchain.app.plingaHelper.service.SmartContractService;

import jakarta.inject.Inject;

/**
 *
 */
@Component
public class CoingeckoBean implements Serializable {

	private static final long serialVersionUID = 5927815418621440349L;
	private final static Logger logger = LoggerFactory.getLogger(CoingeckoBean.class);

	@Autowired
	private InitBean initBean;

	@Autowired
	private BlockchainService blockchainService;

	@Autowired
	private CoingeckoCategoryService coingeckoCategoryService;

	@Autowired
	private CurrencyService currencyService;

	@Autowired
	private CoinService coinService;

	@Autowired
	private SmartContractService smartContractService;

	@Inject
	private CoinListService coinListService;

	@Inject
	private CoinListHistoryService coinListHistoryService;

	public void updateCoingeckoNetworks() {
		var url = initBean.getCoingeckoBaseApi() + "/asset_platforms";
		var json = CoingeckoUtil.runGetCommand(url);
		JSON.parseArray(json, AssetPlatform.class).stream().forEach(network -> {
			if (!blockchainService.existsBlockchainByCoingeckoId(network.getId())) {
				var blockchain = Blockchain.builder().mustCheck(false).coingeckoId(network.getId())
						.isEvm(network.getChain_identifier() != null)
						.name(!Strings.isNullOrEmpty(network.getShortname()) ? network.getShortname()
								: network.getName())
						.fullName(network.getName()).enabled(false).chainId(network.getChain_identifier())
						.blockchainType((network.getChain_identifier() != null) ? BlockchainTechType.DPOS
								: BlockchainTechType.POW)
						.build();
				blockchain = blockchainService.save(blockchain);
				logger.info(String.format("Blockchain %s has been saved in System", blockchain));
			}
		});
	}

	public void updateCoingeckoCategoriesList() {
		var url = initBean.getCoingeckoBaseApi() + "/coins/categories/list";
		var json = CoingeckoUtil.runGetCommand(url);
		JSON.parseArray(json, CoingeckoCategory.class).stream().forEach(category -> {
			if (!coingeckoCategoryService.existById(category.getCategory_id())) {
				coingeckoCategoryService.save(category);
				logger.info(String.format("CoingeckoCategory %s has been added.", category.toString()));
			}
		});
	}

	public void updateCoingeckoCurrenciest() {
		var url = initBean.getCoingeckoBaseApi() + "/simple/supported_vs_currencies";
		var json = CoingeckoUtil.runGetCommand(url);
		JSON.parseArray(json, String.class).stream().forEach(currencyIso -> {
			if (!currencyService.existById(currencyIso)) {
				var currency = Currency.builder().currencyId(currencyIso).build();
				currency = currencyService.save(currency);
				logger.info(String.format("currency %s has been added.", currency.toString()));
			}
		});
	}

	public void updateCoingeckoCoinList() {
		var url = initBean.getCoingeckoBaseApi() + "/coins/list";
		var json = CoingeckoUtil.runGetCommand(url);

		JSON.parseArray(json, Coin.class).stream().forEach(coin -> {
			if (!coinService.existsCoinByCoingeckoId(coin.getCoingeckoId())) {
				coin = coinService.save(coin);
				logger.info(String.format("coin %s has been added.", coin.toString()));
			}
		});
	}

	public void updateCoingeckoCoinListNetwork() {
		var url = initBean.getCoingeckoBaseApi() + "/coins/list?include_platform=true";
		var json = CoingeckoUtil.runGetCommand(url);

		JSON.parseArray(json, CoinNetwork.class).stream().forEach(coinNetwork -> {
			logger.info(String.format("Coin %s has Platforms :", coinNetwork.getId()));
			try {
				Coin coin = coinService.findByCoingeckoId(coinNetwork.getId()).get();
				for (Map.Entry<String, Object> entry : coinNetwork.getPlatforms().entrySet()) {
					String key = entry.getKey();
					Object value = entry.getValue();
					Blockchain blockchain = blockchainService.findByCoingeckoId(key).get();
					if (!smartContractService.existsSmartContractByBlockchainAndCoinAndContractsAddress(blockchain,
							coin, value.toString())) {
						var sc = SmartContract.builder().blockchain(blockchain).coin(coin)
								.contractsAddress(value.toString()).isMain(false).mustCheck(false).build();
						sc = smartContractService.save(sc);
						logger.info(String.format("SmartContract %s has been added", sc));
					}
				}
			} catch (Exception e) {
				logger.error(String.format("Error in coinnetwork %s", coinNetwork.toString()));
			}
		});
	}

	public List<MustAddContractReq> getContractMustAddToCoinNetwork(String coin, List<MustAddContractReq> lst) {
		return lst.stream().filter(mac -> mac.getCoin().equalsIgnoreCase(coin)).collect(Collectors.toList());
	}

	public boolean checkAndUpdateCoingeckoCoinListFull() {
		// var url = initBean.getCoingeckoBaseApi() + "/coins/list";
		if (initBean.doesActionRunning("checkAndUpdateCoingeckoCoinListFull")) {
			logger.info("**********************\" checkAndUpdateCoingeckoCoinListFull Method Already running skip**********************");
			return false;
		}
		logger.info("**********************\" Run checkAndUpdateCoingeckoCoinListFull Method **********************");
		initBean.startActionRunning("checkAndUpdateCoingeckoCoinListFull");
		try {
			var coinList = CoingeckoUtil.runGetCommand(initBean.getCoingeckoBaseApi() + "/coins/list");
			logger.info("000000000000000000000000000000000000000");
			var coinListWithNetwork = CoingeckoUtil
					.runGetCommand(initBean.getCoingeckoBaseApi() + "/coins/list?include_platform=true");
			logger.info("111111111111111111111111111111111111111111");
			var mustAddContracts = smartContractService.findByMustAddAsMustAddContractReq();
			logger.info("222222222222222222222222222222222222222222222222");
			JSONArray jsonArray = JSON.parseArray(coinListWithNetwork);

			List<JSONObject> resultList = jsonArray.stream().map(obj -> {
				JSONObject jsonObject = (JSONObject) obj;
				String id = jsonObject.getString("id");
				List<MustAddContractReq> mustAddContractsForCoin = getContractMustAddToCoinNetwork(id,
						mustAddContracts);
				return new AbstractMap.SimpleEntry<>(jsonObject, mustAddContractsForCoin);
			}).filter(entry -> !entry.getValue().isEmpty()).peek(entry -> {
				JSONObject obj = entry.getKey();
				entry.getValue().forEach(mac -> {
					obj.getJSONObject("platforms").put(mac.getBlockchain(), mac.getContractAddress());
				});
			}).map(AbstractMap.SimpleEntry::getKey).collect(Collectors.toList());
			String modifiedJCoinListWithNetwork = JSON.toJSONString(resultList);
			logger.info("333333333333333333333333333333333333333333");
			var coinListObject = CoinList.builder().currenOriginaltCoinList(coinList)
					.currenOriginaltCoinListWithPlatform(coinListWithNetwork).currentCoinList(coinList)
					.currentCoinListWithPlatform(modifiedJCoinListWithNetwork).build();
			if (coinListService.isEmptyDocument()) {
				coinListService.save(coinListObject);
			} else {
				var currentCoinListObject = coinListService.findFirst();
				if (!currentCoinListObject.equals(coinListObject)) {
					coinListService.save(coinListObject);
					coinListHistoryService.save(currentCoinListObject);
				}
			}
			logger.info("44444444444444444444444444444444444444444444444444");
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		initBean.stopActionRunning("checkAndUpdateCoingeckoCoinListFull");
		return false;
	}

}
