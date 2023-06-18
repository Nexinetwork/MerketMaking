/**
 *
 */
package com.plgchain.app.plingaHelper.service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.annotation.LogMethod;
import com.plgchain.app.plingaHelper.dao.CoinDao;
import com.plgchain.app.plingaHelper.entity.coingecko.Coin;
import com.plgchain.app.plingaHelper.service.Base.BaseService;

/**
 *
 */
@SuppressWarnings("rawtypes")
@Service
public class CoinService extends BaseService implements Serializable {

	private static final long serialVersionUID = -8641271050704411082L;

	@Autowired
	private CoinDao coinDao;

	public Coin findById(Long id) {
		return coinDao.findById(id).get();
	}

	@LogMethod
	public Coin save(Coin coin) {
		return coinDao.save(coin);
	}

	public Coin saveAndFlush(Coin coin) {
		return coinDao.saveAndFlush(coin);
	}

	public List<Coin> findAll() {
		return coinDao.findAll();
	}

	public Optional<Coin> findByName(String name) {
		return coinDao.findByName(name);
	}

	public boolean existsCoinByName(String name) {
		return coinDao.existsCoinByName(name);
	}

	public Optional<Coin> findBySymbol(String symbol) {
		return coinDao.findBySymbol(symbol);
	}

	public boolean existsCoinBySymbol(String symbol) {
		return coinDao.existsCoinBySymbol(symbol);
	}

	public Optional<Coin> findByCoingeckoId(String coingeckoId) {
		return coinDao.findByCoingeckoId(coingeckoId);
	}

	public boolean existsCoinByCoingeckoId(String coingeckoId) {
		return coinDao.existsCoinByCoingeckoId(coingeckoId);
	}

}
