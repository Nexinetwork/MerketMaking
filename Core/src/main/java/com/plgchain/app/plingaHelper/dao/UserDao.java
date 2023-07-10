package com.plgchain.app.plingaHelper.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.plgchain.app.plingaHelper.dao.base.BaseLongDao;
import com.plgchain.app.plingaHelper.entity.User;

public interface UserDao extends BaseLongDao<User> {

	public Optional<User> findByEmailAddress(String emailAddress);

	public boolean existsUserByEmailAddress(String emailAddress);

	@Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM User e")
    public boolean anyExist();

}
