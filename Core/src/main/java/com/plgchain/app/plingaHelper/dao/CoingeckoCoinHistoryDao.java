/**
 *
 */
package com.plgchain.app.plingaHelper.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.plgchain.app.plingaHelper.entity.coingecko.CoingeckoCoinHistory;

/**
 *
 */
public interface CoingeckoCoinHistoryDao extends MongoRepository<CoingeckoCoinHistory,String> {

	@Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM CoingeckoCoinHistory e")
    public boolean anyExist();

}
