package com.plgchain.app.plingaHelper.dao.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author GS
 * @description
 * @date 2018/1/18 10:38
 */
@NoRepositoryBean
public interface BaseLongDao<T> extends JpaRepository<T,Long>,JpaSpecificationExecutor<T>,QuerydslPredicateExecutor<T> {
}
