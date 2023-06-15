package com.plgchain.app.plingaHelper.dao;

import org.springframework.stereotype.Repository;

import com.plgchain.app.plingaHelper.dao.base.BaseLongDao;
import com.plgchain.app.plingaHelper.entity.User;

@Repository
public interface UserDao extends BaseLongDao<User> {

	public User findByEmailAddress(String emailAddress);

	public boolean existsUserByEmailAddress(String emailAddress);

}
