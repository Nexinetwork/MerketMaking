package com.plgchain.app.plingaHelper.dao;

import java.util.List;
import java.util.Optional;

import com.plgchain.app.plingaHelper.dao.base.BaseLongDao;
import com.plgchain.app.plingaHelper.entity.CoinPrice;
import com.plgchain.app.plingaHelper.entity.coingecko.Coin;
import com.plgchain.app.plingaHelper.entity.coingecko.Currency;

public interface CoinPriceDao extends BaseLongDao<CoinPrice> {
	public List<CoinPrice> findByCoin(Coin coin);
	public boolean existsCoinPriceByCoin(Coin coin);
	public Optional<CoinPrice> findByCoinAndCurrency(Coin coin,Currency currency);
	public boolean existsCoinPriceByCoinAndCurrency(Coin coin,Currency currency);

}
