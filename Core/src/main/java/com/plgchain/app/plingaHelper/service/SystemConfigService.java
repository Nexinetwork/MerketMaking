/**
 *
 */
package com.plgchain.app.plingaHelper.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.dao.SystemConfigDao;
import com.plgchain.app.plingaHelper.entity.SystemConfig;
import com.plgchain.app.plingaHelper.service.Base.BaseService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author eae966
 *
 */
@SuppressWarnings({"rawtypes","unused"})
@Slf4j
@Service
public class SystemConfigService extends BaseService implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -1350815432334726157L;

	@Autowired
	private SystemConfigDao systemConfigDao;

	public boolean isByConfigNameExist(String configName) {
		return systemConfigDao.isByConfigNameExist(configName);
	}

	public SystemConfig findByConfigName(String configName) {
		return systemConfigDao.findByConfigName(configName);
	}

	public SystemConfig save(SystemConfig sc) {
		return systemConfigDao.save(sc);
	}

	public SystemConfig saveAndFlush(SystemConfig sc) {
		return systemConfigDao.saveAndFlush(sc);
	}

}
