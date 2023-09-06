package com.plgchain.app.plingaHelper.schedule;

import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.plgchain.app.plingaHelper.bean.InitBean;
import com.plgchain.app.plingaHelper.bean.coingecko.CoingeckoBean;
import com.plgchain.app.plingaHelper.entity.SystemConfig;
import com.plgchain.app.plingaHelper.microService.SystemConfigMicroService;

@Component
public class InitCoingeckoSchedule implements Serializable {

	private static final long serialVersionUID = 7504602259969637312L;

	@SuppressWarnings("unused")
	private final static Logger logger = LoggerFactory.getLogger(InitCoingeckoSchedule.class);

	@Autowired
	private InitBean initBean;

	@Autowired
	private CoingeckoBean coingeckoBean;

	@Autowired
	private SystemConfigMicroService systemConfigMicroService;

	@Scheduled(cron = "0 */10 * * * *", zone = "GMT")
	public void initCoingecko() {
		if (!initBean.isInitCoingecko()) {
			if (!systemConfigMicroService.isByConfigNameExist("coingeckoNetworksInit")) {
				coingeckoBean.updateCoingeckoNetworks();
				var sc = SystemConfig.builder().configName("coingeckoNetworksInit").configBooleanValue(true).build();
				sc = systemConfigMicroService.save(sc);
			} else if (!systemConfigMicroService.isByConfigNameExist("coingeckocategoriesListInit")) {
				coingeckoBean.updateCoingeckoCategoriesList();
				var sc = SystemConfig.builder().configName("coingeckocategoriesListInit").configBooleanValue(true)
						.build();
				sc = systemConfigMicroService.save(sc);
			} else if (!systemConfigMicroService.isByConfigNameExist("coingeckoCurrencyListInit")) {
				coingeckoBean.updateCoingeckoCurrencyList();
				var sc = SystemConfig.builder().configName("coingeckoCurrencyListInit").configBooleanValue(true)
						.build();
				sc = systemConfigMicroService.save(sc);
			} else if (!systemConfigMicroService.isByConfigNameExist("coingeckoCoinListInit")) {
				coingeckoBean.updateCoingeckoCoinList();
				var sc = SystemConfig.builder().configName("coingeckoCoinListInit").configBooleanValue(true).build();
				sc = systemConfigMicroService.save(sc);
			} else if (!systemConfigMicroService.isByConfigNameExist("coingeckoSmartcontractInit")) {
				coingeckoBean.updateCoingeckoCoinListNetwork();
				var sc = SystemConfig.builder().configName("coingeckoSmartcontractInit").configBooleanValue(true)
						.build();
				sc = systemConfigMicroService.save(sc);
			} else if (!systemConfigMicroService.isByConfigNameExist("coingeckoCoinListMongoInit")) {
				coingeckoBean.checkAndUpdateCoingeckoCoinListFull();
				var sc = SystemConfig.builder().configName("coingeckoCoinListMongoInit").configBooleanValue(true)
						.build();
				sc = systemConfigMicroService.save(sc);
			}
		}
	}

}
