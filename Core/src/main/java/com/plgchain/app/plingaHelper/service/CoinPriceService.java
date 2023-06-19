/**
 *
 */
package com.plgchain.app.plingaHelper.service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.dao.CoinPriceDao;
import com.plgchain.app.plingaHelper.dao.LogDao;
import com.plgchain.app.plingaHelper.entity.CoinPrice;
import com.plgchain.app.plingaHelper.entity.Log;
import com.plgchain.app.plingaHelper.entity.coingecko.Coin;
import com.plgchain.app.plingaHelper.entity.coingecko.Currency;
import com.plgchain.app.plingaHelper.service.Base.BaseService;

/**
 *
 */
@SuppressWarnings("rawtypes")
@Service
public class CoinPriceService extends BaseService implements Serializable {

	private static final long serialVersionUID = -8641271050704411082L;

	@Autowired
	private CoinPriceDao coinPriceDao;

	public CoinPrice findById(Long id) {
		return coinPriceDao.findById(id).get();
	}


	public CoinPrice save(CoinPrice coinPrice) {
		return coinPriceDao.save(coinPrice);
	}

	public CoinPrice saveAndFlush(CoinPrice coinPrice) {
		return coinPriceDao.saveAndFlush(coinPrice);
	}

	public List<CoinPrice> findAll() {
		return coinPriceDao.findAll();
	}

	public List<CoinPrice> findByCoin(Coin coin) {
		return coinPriceDao.findByCoin(coin);
	}

	public boolean existsCoinPriceByCoin(Coin coin) {
		return coinPriceDao.existsCoinPriceByCoin(coin);
	}

	public Optional<CoinPrice> findByCoinAndCurrency(Coin coin,Currency currency) {
		return coinPriceDao.findByCoinAndCurrency(coin,currency);
	}

	public boolean existsCoinPriceByCoinAndCurrency(Coin coin,Currency currency) {
		return coinPriceDao.existsCoinPriceByCoinAndCurrency(coin,currency);
	}

}
