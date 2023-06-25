package com.plgchain.app.plingaHelper.dao;

import java.util.Optional;

import com.plgchain.app.plingaHelper.dao.base.BaseLongDao;
import com.plgchain.app.plingaHelper.entity.User;

public interface UserDao extends BaseLongDao<User> {

	public Optional<User> findByEmailAddress(String emailAddress);

	public boolean existsUserByEmailAddress(String emailAddress);

}
