package com.plgchain.app.plingaHelper.dao;

import org.springframework.stereotype.Repository;

import com.plgchain.app.plingaHelper.dao.base.BaseStringDao;
import com.plgchain.app.plingaHelper.entity.Log;
import com.plgchain.app.plingaHelper.entity.coingecko.CoingeckoCategory;
import com.plgchain.app.plingaHelper.entity.coingecko.Currency;

@Repository
public interface CurrencyDao extends BaseStringDao<Currency> {

}
