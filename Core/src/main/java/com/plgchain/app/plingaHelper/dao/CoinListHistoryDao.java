package com.plgchain.app.plingaHelper.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.plgchain.app.plingaHelper.entity.CoinListHistory;

public interface CoinListHistoryDao extends MongoRepository<CoinListHistory,Long> {

	public CoinListHistory findFirstBy();

	@Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM CoinListHistory e")
    public boolean anyExist();

}
