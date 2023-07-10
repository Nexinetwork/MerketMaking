package com.plgchain.app.plingaHelper.dao;

import org.springframework.data.jpa.repository.Query;

import com.plgchain.app.plingaHelper.dao.base.BaseLongDao;
import com.plgchain.app.plingaHelper.entity.Log;

public interface LogDao extends BaseLongDao<Log> {

	@Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Log e")
    public boolean anyExist();

}
