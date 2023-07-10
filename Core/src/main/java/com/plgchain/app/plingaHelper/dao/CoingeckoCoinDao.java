/**
 *
 */
package com.plgchain.app.plingaHelper.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.plgchain.app.plingaHelper.entity.coingecko.CoingeckoCoin;

/**
 *
 */
public interface CoingeckoCoinDao extends MongoRepository<CoingeckoCoin,String> {

	@Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM CoingeckoCoin e")
    public boolean anyExist();

}
