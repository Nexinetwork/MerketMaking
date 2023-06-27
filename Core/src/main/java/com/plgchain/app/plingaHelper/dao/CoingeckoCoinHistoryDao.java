/**
 *
 */
package com.plgchain.app.plingaHelper.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.plgchain.app.plingaHelper.entity.coingecko.CoingeckoCoinHistory;

/**
 *
 */
public interface CoingeckoCoinHistoryDao extends MongoRepository<CoingeckoCoinHistory,String> {

}
