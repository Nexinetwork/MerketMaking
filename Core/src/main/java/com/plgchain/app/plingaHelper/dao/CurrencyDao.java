package com.plgchain.app.plingaHelper.dao;

import org.springframework.data.jpa.repository.Query;

import com.plgchain.app.plingaHelper.dao.base.BaseStringDao;
import com.plgchain.app.plingaHelper.entity.coingecko.Currency;

public interface CurrencyDao extends BaseStringDao<Currency> {

	@Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Currency e")
    public boolean anyExist();

}
