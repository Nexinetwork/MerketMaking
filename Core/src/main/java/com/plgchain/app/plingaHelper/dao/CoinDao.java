package com.plgchain.app.plingaHelper.dao;

import java.util.List;
import java.util.Optional;

import com.plgchain.app.plingaHelper.dao.base.BaseLongDao;
import com.plgchain.app.plingaHelper.entity.coingecko.Coin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CoinDao extends BaseLongDao<Coin> {
	public Optional<Coin> findByName(String name);
	public boolean existsCoinByName(String name);
	public Optional<Coin> findBySymbol(String symbol);
	public boolean existsCoinBySymbol(String symbol);
	public Optional<Coin> findByCoingeckoId(String coingeckoId);
	public boolean existsCoinByCoingeckoId(String coingeckoId);
	public List<Coin> findByCoingeckoJsonIsNull();
    public Page<Coin> findByCoingeckoJsonIsNull(Pageable pageable);
    public Page<Coin> findByCoingeckoJsonIsNullAndCoingeckoIdIsNotNull(Pageable pageable);
    public List<Coin> findByMustCheck(boolean mustCheck);


}
