/**
 *
 */
package com.plgchain.app.plingaHelper.microService;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.dao.CoingeckoCoinDao;
import com.plgchain.app.plingaHelper.entity.coingecko.CoingeckoCoin;
import com.plgchain.app.plingaHelper.microService.Base.BaseMicroService;

import jakarta.inject.Inject;

/**
 *
 */
@Service
public class CoingeckoCoinMicroService extends BaseMicroService<CoingeckoCoin> implements Serializable {

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

	public boolean anyExist() {
		return coingeckoCoinDao.anyExist();
	}

	public void delete(CoingeckoCoin object) {
		coingeckoCoinDao.delete(object);
	}

	public void deleteAll() {
		coingeckoCoinDao.deleteAll();
	}

	public List<CoingeckoCoin> saveAll(List<CoingeckoCoin> oList) {
		return coingeckoCoinDao.saveAll(oList);
	}

}
