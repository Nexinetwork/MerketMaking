package com.plgchain.app.plingaHelper.dao;

import org.springframework.stereotype.Repository;

import com.plgchain.app.plingaHelper.dao.base.BaseStringDao;
import com.plgchain.app.plingaHelper.entity.Log;
import com.plgchain.app.plingaHelper.entity.category.CoingeckoCategory;

@Repository
public interface CoingeckoCategoryDao extends BaseStringDao<CoingeckoCategory> {

}
