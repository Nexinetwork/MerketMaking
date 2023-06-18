/**
 *
 */
package com.plgchain.app.plingaHelper.bean.coingecko;

import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.netflix.servo.util.Strings;
import com.plgchain.app.plingaHelper.bean.InitBean;
import com.plgchain.app.plingaHelper.coingecko.type.AssetPlatform;
import com.plgchain.app.plingaHelper.coingecko.type.CoingeckoUtil;
import com.plgchain.app.plingaHelper.constant.BlockchainTechType;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.entity.coingecko.CoingeckoCategory;
import com.plgchain.app.plingaHelper.entity.coingecko.Currency;
import com.plgchain.app.plingaHelper.service.BlockchainService;
import com.plgchain.app.plingaHelper.service.CoingeckoCategoryService;
import com.plgchain.app.plingaHelper.service.CurrencyService;

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

}