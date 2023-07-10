package com.plgchain.app.plingaHelper.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.plgchain.app.plingaHelper.dao.base.BaseLongDao;
import com.plgchain.app.plingaHelper.entity.SystemConfig;

public interface SystemConfigDao extends BaseLongDao<SystemConfig> {

	@Query("SELECT c FROM SystemConfig c WHERE c.configName  = :configName")
	public SystemConfig findByConfigName(@Param("configName") String configName);

	@Query("select case when count(c)> 0 then true else false end from SystemConfig c WHERE c.configName  = :configName")
	public boolean isByConfigNameExist(@Param("configName") String configName);

	@Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM SystemConfig e")
    public boolean anyExist();

}
