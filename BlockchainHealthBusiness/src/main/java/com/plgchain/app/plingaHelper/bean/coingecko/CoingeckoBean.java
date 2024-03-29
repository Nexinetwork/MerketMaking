/**
 *
 */
package com.plgchain.app.plingaHelper.bean.coingecko;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.netflix.servo.util.Strings;
import com.plgchain.app.plingaHelper.bean.InitBean;
import com.plgchain.app.plingaHelper.coingecko.request.CoinNetwork;
import com.plgchain.app.plingaHelper.coingecko.request.MustAddContractReq;
import com.plgchain.app.plingaHelper.coingecko.type.AssetPlatform;
import com.plgchain.app.plingaHelper.coingecko.util.CoingeckoUtil;
import com.plgchain.app.plingaHelper.constant.BlockchainTechType;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.entity.coingecko.Coin;
import com.plgchain.app.plingaHelper.entity.coingecko.CoinList;
import com.plgchain.app.plingaHelper.entity.coingecko.CoingeckoCategory;
import com.plgchain.app.plingaHelper.entity.coingecko.CoingeckoCoin;
import com.plgchain.app.plingaHelper.entity.coingecko.CoingeckoCoinHistory;
import com.plgchain.app.plingaHelper.entity.coingecko.Currency;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.microService.BlockchainMicroService;
import com.plgchain.app.plingaHelper.microService.CoinListHistoryMicroService;
import com.plgchain.app.plingaHelper.microService.CoinListMicroService;
import com.plgchain.app.plingaHelper.microService.CoinMicroService;
import com.plgchain.app.plingaHelper.microService.CoingeckoCategoryMicroService;
import com.plgchain.app.plingaHelper.microService.CoingeckoCoinMicroService;
import com.plgchain.app.plingaHelper.microService.CoingeckoCoinHistoryMicroService;
import com.plgchain.app.plingaHelper.microService.CurrencyMicroService;
import com.plgchain.app.plingaHelper.microService.SmartContractMicroService;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 *
 */
@Component
public class CoingeckoBean implements Serializable {

	private static final long serialVersionUID = 5927815418621440349L;
	private final static Logger logger = LoggerFactory.getLogger(CoingeckoBean.class);

	@Inject
	private InitBean initBean;

	@Inject
	private BlockchainMicroService blockchainMicroService;

	@Inject
	private CoingeckoCategoryMicroService coingeckoCategoryMicroService;

	@Inject
	private CurrencyMicroService currencyMicroService;

	@Inject
	private CoinMicroService coinMicroService;

	@Inject
	private SmartContractMicroService smartContractMicroService;

	@Inject
	private CoinListMicroService coinListMicroService;

	@Inject
	private CoinListHistoryMicroService coinListHistoryMicroService;

	@Inject
	private CoingeckoCoinMicroService coingeckoCoinMicroService;

	@Inject
	private CoingeckoCoinHistoryMicroService coingeckoCoinHistoryMicroService;

	public void updateCoingeckoNetworks() {
		try {
			var url = initBean.getCoingeckoBaseApi() + "/asset_platforms";
			var json = CoingeckoUtil.runGetCommand(initBean.getHttpClient(),url);
			JSON.parseArray(json, AssetPlatform.class).stream().forEach(network -> {
				if (!blockchainMicroService.existsBlockchainByCoingeckoId(network.getId())) {
					var blockchain = Blockchain.builder().mustCheck(false).coingeckoId(network.getId())
							.isEvm(network.getChain_identifier() != null)
							.name(!Strings.isNullOrEmpty(network.getShortname()) ? network.getShortname()
									: network.getName())
							.fullName(network.getName()).enabled(false).chainId(network.getChain_identifier())
							.blockchainType((network.getChain_identifier() != null) ? BlockchainTechType.DPOS
									: BlockchainTechType.POW)
							.build();
					blockchain = blockchainMicroService.save(blockchain);
					logger.info(String.format("Blockchain %s has been saved in System", blockchain));
				}
			});
		} catch (Exception e) {
		}
	}

	public void updateCoingeckoCategoriesList() {
		try {
			var url = initBean.getCoingeckoBaseApi() + "/coins/categories/list";
			var json = CoingeckoUtil.runGetCommand(initBean.getHttpClient(),url);
			JSON.parseArray(json, CoingeckoCategory.class).stream().forEach(category -> {
				if (!coingeckoCategoryMicroService.existById(category.getCategory_id())) {
					coingeckoCategoryMicroService.save(category);
					logger.info(String.format("CoingeckoCategory %s has been added.", category.toString()));
				}
			});
		} catch (Exception e) {
		}
	}

	public void updateCoingeckoCurrencyList() {
		try {
			var url = initBean.getCoingeckoBaseApi() + "/simple/supported_vs_currencies";
			var json = CoingeckoUtil.runGetCommand(initBean.getHttpClient(),url);
			JSON.parseArray(json, String.class).stream().forEach(currencyIso -> {
				if (!currencyMicroService.existById(currencyIso)) {
					var currency = Currency.builder().currencyId(currencyIso).build();
					currency = currencyMicroService.save(currency);
					logger.info(String.format("currency %s has been added.", currency.toString()));
				}
			});
		} catch (Exception e) {
		}
	}

	public void updateCoingeckoCoinList() {
		try {
			var url = initBean.getCoingeckoBaseApi() + "/coins/list";
			var json = CoingeckoUtil.runGetCommand(initBean.getHttpClient(),url);

			JSON.parseArray(json, Coin.class).stream().forEach(coin -> {
				if (!coinMicroService.existsCoinByCoingeckoId(coin.getCoingeckoId())) {
					coin = coinMicroService.save(coin);
					logger.info(String.format("coin %s has been added.", coin.toString()));
				}
			});
		} catch (Exception e) {
		}
	}

	public void updateCoingeckoCoinListNetwork() {
		try {
			var url = initBean.getCoingeckoBaseApi() + "/coins/list?include_platform=true";
			var json = CoingeckoUtil.runGetCommand(initBean.getHttpClient(),url);

			JSON.parseArray(json, CoinNetwork.class).stream().forEach(coinNetwork -> {
				logger.info(String.format("Coin %s has Platforms :", coinNetwork.getId()));
				try {
					Coin coin = coinMicroService.findByCoingeckoId(coinNetwork.getId()).get();
					for (Map.Entry<String, Object> entry : coinNetwork.getPlatforms().entrySet()) {
						String key = entry.getKey();
						Object value = entry.getValue();
						Blockchain blockchain = blockchainMicroService.findByCoingeckoId(key).get();
						if (!smartContractMicroService.existsSmartContractByBlockchainAndCoinAndContractsAddress(blockchain,
								coin, value.toString())) {
							var sc = SmartContract.builder().blockchain(blockchain).coin(coin)
									.contractsAddress(value.toString()).isMain(false).mustCheck(false).build();
							sc = smartContractMicroService.save(sc);
							logger.info(String.format("SmartContract %s has been added", sc));
						}
					}
				} catch (Exception e) {
					logger.error(String.format("Error in coinnetwork %s", coinNetwork.toString()));
				}
			});
		} catch (Exception e) {
		}
	}

	public List<MustAddContractReq> getContractMustAddToCoinNetwork(String coin, List<MustAddContractReq> lst) {
		return lst.stream().filter(mac -> mac.getCoin().equalsIgnoreCase(coin)).collect(Collectors.toList());
	}

	public void checkAndUpdateCoingeckoCoinListFull() {
		// var url = initBean.getCoingeckoBaseApi() + "/coins/list";
		initBean.startActionRunning("checkAndUpdateCoingeckoCoinListFull");
		try {
			var coinList = CoingeckoUtil.runGetCommand(initBean.getHttpClient(),initBean.getCoingeckoBaseApi() + "/coins/list");
			var coinListWithNetwork = CoingeckoUtil
					.runGetCommand(initBean.getHttpClient(),initBean.getCoingeckoBaseApi() + "/coins/list?include_platform=true");
			var mustAddContracts = smartContractMicroService.findByMustAddAsMustAddContractReq();
			JSON.parseArray(coinList, Coin.class).stream().forEach(coin -> {
				if (!coinMicroService.existsCoinByCoingeckoId(coin.getCoingeckoId())) {
					coin = coinMicroService.save(coin);
					logger.info(String.format("coin %s has been added.", coin.toString()));
				}
			});

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
			var coinListObject = CoinList.builder().currentOriginalCoinList(coinList)
					.currentOriginalCoinListWithPlatform(coinListWithNetwork).currentCoinList(coinList)
					.currentCoinListWithPlatform(modifiedJCoinListWithNetwork).build();
			if (coinListMicroService.isEmptyDocument()) {
				coinListMicroService.save(coinListObject);
			} else {
				var currentCoinListObject = coinListMicroService.findFirst();
				if (!currentCoinListObject.equals(coinListObject)) {
					coinListMicroService.save(coinListObject);
					coinListHistoryMicroService.save(currentCoinListObject);
				}
			}
			JSON.parseArray(coinListWithNetwork, CoinNetwork.class).stream().forEach(coinNetwork -> {
				logger.info(String.format("Coin %s has Platforms :", coinNetwork.getId()));
				try {
					Coin coin = coinMicroService.findByCoingeckoId(coinNetwork.getId()).get();
					for (Map.Entry<String, Object> entry : coinNetwork.getPlatforms().entrySet()) {
						String key = entry.getKey();
						Object value = entry.getValue();
						Blockchain blockchain = blockchainMicroService.findByCoingeckoId(key).get();
						if (!smartContractMicroService.existsSmartContractByBlockchainAndCoinAndContractsAddress(blockchain,
								coin, value.toString())) {
							var sc = SmartContract.builder().blockchain(blockchain).coin(coin)
									.contractsAddress(value.toString()).isMain(false).mustCheck(false).build();
							sc = smartContractMicroService.save(sc);
							logger.info(String.format("SmartContract %s has been added", sc));
						}
					}
				} catch (Exception e) {
					logger.error(String.format("Error in coinnetwork %s", coinNetwork.toString()));
				}
			});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Transactional
	public CoingeckoCoin createOrUpdateCoingeckoCoin(String coinId) {
		try {
			var url = initBean.getCoingeckoBaseApi() + "/coins/" + coinId;
			var json = CoingeckoUtil.runGetCommand(initBean.getHttpClient(),url);
			JSONObject jo = JSON.parseObject(json);
			Coin coin = coinMicroService.findByCoingeckoId(coinId).orElseThrow();
			List<SmartContract> contractList = coin.getContractList();
			contractList.forEach(contract -> {
				if (jo.getJSONObject("platforms") != null) {
					jo.getJSONObject("platforms").put(contract.getBlockchain().getName(),
							contract.getContractsAddress());
				}
				if (jo.getJSONObject("detail_platforms") != null) {
					JSONObject blockchainObject = new JSONObject();
					if (contract.getDecimal() != null)
						blockchainObject.put("decimal_place", contract.getDecimal());
					if (contract.getContractsAddress() != null)
						blockchainObject.put("contract_address", contract.getContractsAddress());
					if (blockchainObject != null)
						jo.getJSONObject("detail_platforms").put(contract.getBlockchain().getName(), blockchainObject);
				}
			});
			var editedJson = JSON.toJSONString(jo);
			Optional<CoingeckoCoin> coinGeckoCoinOp = coingeckoCoinMicroService.findById(coinId);
			CoingeckoCoin coingeckoCoin = null;
			if (coinGeckoCoinOp.isPresent()) {
				var currentObject = coinGeckoCoinOp.get();
				if (!(currentObject.getOriginalJson().equals(json)
						&& currentObject.getEditedJson().equals(editedJson))) {
					coingeckoCoin = new CoingeckoCoin(jo);
					coingeckoCoin.setOriginalJson(json);
					coingeckoCoin.setEditedJson(editedJson);
					coin.setCoingeckoJson(editedJson);
					coin.setLastCheck(LocalDateTime.now());
					coin.setPriceInUsd(coingeckoCoin.getPriceInUsd());
					coin = coinMicroService.save(coin);
					coingeckoCoin = coingeckoCoinMicroService.save(coingeckoCoin);
					logger.info(String.format("coingeckoCoin %s has been updated.", coinId));
					CoingeckoCoinHistory cgh = coingeckoCoinHistoryMicroService.findById(coinId).orElseThrow();
					cgh.addCoingeckoCoin(coingeckoCoin);
					coingeckoCoinHistoryMicroService.save(cgh);
				} else {
					coin.setLastCheck(LocalDateTime.now());
					coin = coinMicroService.save(coin);
				}
			} else {
				coingeckoCoin = new CoingeckoCoin(jo);
				coingeckoCoin.setOriginalJson(json);
				coingeckoCoin.setEditedJson(editedJson);
				coin.setCoingeckoJson(editedJson);
				coin.setPriceInUsd(coingeckoCoin.getPriceInUsd());
				coin.setLastCheck(LocalDateTime.now());
				coin = coinMicroService.save(coin);
				coingeckoCoin = coingeckoCoinMicroService.save(coingeckoCoin);
				CoingeckoCoinHistory cgh = new CoingeckoCoinHistory(coingeckoCoin.getId(), coingeckoCoin.getSymbol(),
						coingeckoCoin.getName());
				cgh.addCoingeckoCoin(coingeckoCoin);
				coingeckoCoinHistoryMicroService.save(cgh);
				logger.info(String.format("coingeckoCoin %s has been added", coinId));
			}
			return coingeckoCoin;
		} catch (Exception e) {
			return null;
		}
	}

}
