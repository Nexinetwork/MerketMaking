/**
 *
 */
package com.plgchain.app.plingaHelper.service;

import java.io.Serializable;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.dao.LogDao;
import com.plgchain.app.plingaHelper.entity.Log;
import com.plgchain.app.plingaHelper.service.Base.BaseService;

/**
 *
 */
@Service
public class LogService extends BaseService<Log> implements Serializable {

	private static final long serialVersionUID = -8641271050704411082L;

	@Autowired
	private LogDao logDao;

	public Log findById(Long id) {
		return logDao.findById(id).get();
	}


	public Log save(Log log) {
		return logDao.save(log);
	}

	public Log saveAndFlush(Log log) {
		return logDao.saveAndFlush(log);
	}

	public List<Log> findAll() {
		return logDao.findAll();
	}

	public boolean anyExist() {
		return logDao.anyExist();
	}

	public void delete(Log object) {
		logDao.delete(object);
	}

	public void deleteAll() {
		logDao.deleteAll();
	}

	public List<Log> saveAll(List<Log> oList) {
		return logDao.saveAll(oList);
	}

}
