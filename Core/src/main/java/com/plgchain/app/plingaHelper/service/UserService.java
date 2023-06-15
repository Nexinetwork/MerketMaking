package com.plgchain.app.plingaHelper.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.dao.UserDao;
import com.plgchain.app.plingaHelper.entity.User;
import com.plgchain.app.plingaHelper.service.Base.BaseService;

@Service
public class UserService extends BaseService implements Serializable {

	private static final long serialVersionUID = -820540009110569732L;

	@Autowired
	public UserDao userDao;

	public User findOne(Long id) {
		return userDao.findById(id).get();
	}

	public User findById(Long id) {
		return userDao.findById(id).get();
	}

	public User findByEmailAddress(String emailAddress) {
		return userDao.findByEmailAddress(emailAddress);
	}

	public User save(User user) {
		return userDao.save(user);
	}

	public User saveAndFlush(User user) {
		return userDao.saveAndFlush(user);
	}

	public List<User> findAll() {
		return userDao.findAll();
	}

	public boolean existsUserByEmailAddress(String emailAddress) {
		return userDao.existsUserByEmailAddress(emailAddress);
	}

}
