package com.plgchain.app.plingaHelper.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.plgchain.app.plingaHelper.entity.CoinListHistory;

public interface CoinListHistoryDao extends MongoRepository<CoinListHistory,Long> {

	public CoinListHistory findFirstBy();

}
