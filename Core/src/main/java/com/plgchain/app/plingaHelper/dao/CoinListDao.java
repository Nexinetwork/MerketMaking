package com.plgchain.app.plingaHelper.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.plgchain.app.plingaHelper.entity.coingecko.CoinList;

public interface CoinListDao extends MongoRepository<CoinList,Long> {
	public CoinList findFirstBy();

	@Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM CoinList e")
    public boolean anyExist();
}
