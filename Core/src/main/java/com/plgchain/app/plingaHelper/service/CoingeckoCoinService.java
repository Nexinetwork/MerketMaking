/**
 *
 */
package com.plgchain.app.plingaHelper.service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import com.plgchain.app.plingaHelper.dao.CoingeckoCoinDao;
import com.plgchain.app.plingaHelper.entity.coingecko.CoingeckoCoin;
import com.plgchain.app.plingaHelper.service.Base.BaseService;

import jakarta.inject.Inject;

/**
 *
 */
public class CoingeckoCoinService extends BaseService<CoingeckoCoin> implements Serializable {

	private static final long serialVersionUID = 93357945667081346L;

	@Inject
	private CoingeckoCoinDao coingeckoCoinDao;

	public Optional<CoingeckoCoin> findById(String id) {
		return coingeckoCoinDao.findById(id);

	}

	public boolean existById(String id) {
		return coingeckoCoinDao.existsById(id);
	}

	public List<CoingeckoCoin> findAll() {
		return coingeckoCoinDao.findAll();
	}

	public CoingeckoCoin save(CoingeckoCoin cc) {
		return coingeckoCoinDao.save(cc);
	}

}
