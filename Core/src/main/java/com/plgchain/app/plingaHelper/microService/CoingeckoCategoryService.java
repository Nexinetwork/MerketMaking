/**
 *
 */
package com.plgchain.app.plingaHelper.microService;

import java.io.Serializable;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.annotation.LogMethod;
import com.plgchain.app.plingaHelper.dao.CoingeckoCategoryDao;
import com.plgchain.app.plingaHelper.entity.coingecko.CoingeckoCategory;
import com.plgchain.app.plingaHelper.microService.Base.BaseService;

/**
 *
 */
@Service
public class CoingeckoCategoryService extends BaseService<CoingeckoCategory> implements Serializable {

	private static final long serialVersionUID = -8641271050704411082L;

	@Autowired
	private CoingeckoCategoryDao coingeckoCategoryDao;

	public CoingeckoCategory findById(String id) {
		return coingeckoCategoryDao.findById(id).get();
	}

	public boolean existById(String id) {
		return coingeckoCategoryDao.existsById(id);
	}

	@LogMethod
	public CoingeckoCategory save(CoingeckoCategory cgc) {
		return coingeckoCategoryDao.save(cgc);
	}

	public CoingeckoCategory saveAndFlush(CoingeckoCategory cgc) {
		return coingeckoCategoryDao.saveAndFlush(cgc);
	}

	public List<CoingeckoCategory> findAll() {
		return coingeckoCategoryDao.findAll();
	}

	public boolean anyExist() {
		return coingeckoCategoryDao.anyExist();
	}

	public void delete(CoingeckoCategory object) {
		coingeckoCategoryDao.delete(object);
	}

	public void deleteAll() {
		coingeckoCategoryDao.deleteAll();
	}

	public List<CoingeckoCategory> saveAll(List<CoingeckoCategory> oList) {
		return coingeckoCategoryDao.saveAll(oList);
	}

}
