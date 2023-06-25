package com.plgchain.app.plingaHelper.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.plgchain.app.plingaHelper.entity.coingecko.CoinList;

public interface CoinListDao extends MongoRepository<CoinList,Long> {
	public CoinList findFirstBy();
}
