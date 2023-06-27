/**
 *
 */
package com.plgchain.app.plingaHelper.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.plgchain.app.plingaHelper.entity.coingecko.CoingeckoCoin;

/**
 *
 */
public interface CoingeckoCoinDao extends MongoRepository<CoingeckoCoin,String> {

}
