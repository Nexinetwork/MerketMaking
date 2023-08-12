/**
 *
 */
package com.plgchain.app.plingaHelper.service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.dao.SystemConfigDao;
import com.plgchain.app.plingaHelper.entity.SystemConfig;
import com.plgchain.app.plingaHelper.entity.TankhahWallet;
import com.plgchain.app.plingaHelper.service.Base.BaseService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author eae966
 *
 */
@SuppressWarnings({"unused"})
@Slf4j
@Service
public class SystemConfigService extends BaseService<SystemConfig> implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -1350815432334726157L;

	@Autowired
	private SystemConfigDao systemConfigDao;

	public boolean isByConfigNameExist(String configName) {
		return systemConfigDao.isByConfigNameExist(configName);
	}

	public Optional<SystemConfig> findByConfigName(String configName) {
		return systemConfigDao.findByConfigName(configName);
	}

	public boolean existsByConfigName(String configName) {
		return systemConfigDao.existsByConfigName(configName);
	}

	public SystemConfig save(SystemConfig sc) {
		return systemConfigDao.save(sc);
	}

	public SystemConfig saveAndFlush(SystemConfig sc) {
		return systemConfigDao.saveAndFlush(sc);
	}

	public boolean anyExist() {
		return systemConfigDao.anyExist();
	}

	public void delete(SystemConfig object) {
		systemConfigDao.delete(object);
	}

	public void deleteAll() {
		systemConfigDao.deleteAll();
	}

	public List<SystemConfig> saveAll(List<SystemConfig> oList) {
		return systemConfigDao.saveAll(oList);
	}

}
