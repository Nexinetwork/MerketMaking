package com.plgchain.app.plingaHelper.dao;

import org.springframework.data.jpa.repository.Query;

import com.plgchain.app.plingaHelper.dao.base.BaseLongDao;
import com.plgchain.app.plingaHelper.entity.marketMaking.MarketMaking;

public interface MarketMakingDao extends BaseLongDao<MarketMaking> {

	@Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM MarketMaking e")
    public boolean anyExist();

}
