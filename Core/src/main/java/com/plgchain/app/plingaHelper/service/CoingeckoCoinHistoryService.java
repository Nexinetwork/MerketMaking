/**
 *
 */
package com.plgchain.app.plingaHelper.service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.dao.CoingeckoCoinHistoryDao;
import com.plgchain.app.plingaHelper.entity.coingecko.CoingeckoCoinHistory;
import com.plgchain.app.plingaHelper.service.Base.BaseService;

import jakarta.inject.Inject;

/**
 *
 */
@Service
public class CoingeckoCoinHistoryService extends BaseService<CoingeckoCoinHistory> implements Serializable {

	private static final long serialVersionUID = 1994814416456311489L;

	@Inject
	private CoingeckoCoinHistoryDao coingeckoCoinHistoryDao;

	public Optional<CoingeckoCoinHistory> findById(String id) {
		return coingeckoCoinHistoryDao.findById(id);

	}

	public boolean existById(String id) {
		return coingeckoCoinHistoryDao.existsById(id);
	}

	public List<CoingeckoCoinHistory> findAll() {
		return coingeckoCoinHistoryDao.findAll();
	}

	public CoingeckoCoinHistory save(CoingeckoCoinHistory cc) {
		return coingeckoCoinHistoryDao.save(cc);
	}

}
