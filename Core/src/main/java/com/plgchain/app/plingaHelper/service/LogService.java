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
@SuppressWarnings("rawtypes")
@Service
public class LogService extends BaseService implements Serializable {

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

}
