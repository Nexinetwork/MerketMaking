/**
 *
 */
package com.plgchain.app.plingaHelper.service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.dao.MarketMakingDao;
import com.plgchain.app.plingaHelper.entity.marketMaking.MarketMaking;
import com.plgchain.app.plingaHelper.service.Base.BaseService;

import jakarta.inject.Inject;

/**
 *
 */
@Service
public class MarketMakingService extends BaseService<MarketMaking> implements Serializable {

	private static final long serialVersionUID = -3217338693572386600L;

	@Inject
	private MarketMakingDao marketMakingDao;

	public Optional<MarketMaking> findById(Long id) {
		return marketMakingDao.findById(id);
	}

	public boolean existById(Long id) {
		return marketMakingDao.existsById(id);
	}

	public MarketMaking save(MarketMaking mm) {
		return marketMakingDao.save(mm);
	}

	public MarketMaking saveAndFlush(MarketMaking mm) {
		return marketMakingDao.saveAndFlush(mm);
	}

	public List<MarketMaking> findAll() {
		return marketMakingDao.findAll();
	}

	public boolean anyExist() {
		return marketMakingDao.anyExist();
	}

	public void delete(MarketMaking object) {
		marketMakingDao.delete(object);
	}

	public void deleteAll() {
		marketMakingDao.deleteAll();
	}

	public List<MarketMaking> saveAll(List<MarketMaking> oList) {
		return marketMakingDao.saveAll(oList);
	}

}
