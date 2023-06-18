/**
 *
 */
package com.plgchain.app.plingaHelper.service;

import java.io.Serializable;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.annotation.LogMethod;
import com.plgchain.app.plingaHelper.dao.CurrencyDao;
import com.plgchain.app.plingaHelper.entity.coingecko.Currency;
import com.plgchain.app.plingaHelper.service.Base.BaseService;

/**
 *
 */
@SuppressWarnings("rawtypes")
@Service
public class CurrencyService extends BaseService implements Serializable {

	private static final long serialVersionUID = -8641271050704411082L;

	@Autowired
	private CurrencyDao currencyDao;

	public Currency findById(String id) {
		return currencyDao.findById(id).get();
	}

	public boolean existById(String id) {
		return currencyDao.existsById(id);
	}

	@LogMethod
	public Currency save(Currency cgc) {
		return currencyDao.save(cgc);
	}

	public Currency saveAndFlush(Currency cgc) {
		return currencyDao.saveAndFlush(cgc);
	}

	public List<Currency> findAll() {
		return currencyDao.findAll();
	}

}
