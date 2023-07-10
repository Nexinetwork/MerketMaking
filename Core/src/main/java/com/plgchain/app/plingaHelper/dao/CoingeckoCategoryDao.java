package com.plgchain.app.plingaHelper.dao;

import org.springframework.data.jpa.repository.Query;

import com.plgchain.app.plingaHelper.dao.base.BaseStringDao;
import com.plgchain.app.plingaHelper.entity.coingecko.CoingeckoCategory;

public interface CoingeckoCategoryDao extends BaseStringDao<CoingeckoCategory> {

	@Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM CoingeckoCategory e")
    public boolean anyExist();

}
