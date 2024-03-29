package com.plgchain.app.plingaHelper.microService.Base;

import com.plgchain.app.plingaHelper.ability.CreateAbility;
import com.plgchain.app.plingaHelper.ability.ScreenAbility;
import com.plgchain.app.plingaHelper.ability.UpdateAbility;
import com.plgchain.app.plingaHelper.constant.PageModel;
import com.plgchain.app.plingaHelper.dao.base.BaseLongDao;
import com.plgchain.app.plingaHelper.dto.Pagenation;
import com.querydsl.core.types.Predicate;

import lombok.Setter;

import org.hibernate.query.NativeQuery;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TopBaseMicroService<E, D extends BaseLongDao> {

    @Autowired
    protected EntityManager entityManager ;

    @Setter
    protected D dao;


    public E findById(Serializable id) {
        return (E) dao.findById(id);
    }

    public List<E> findAll() {
        return dao.findAll();
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(long id) {
        dao.delete(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deletes(Long[] ids) {
        for (long id : ids) {
            delete(id);
        }
    }

    public E save(E e) {
        return (E) dao.save(e);
    }

    /**
     * @param createAbility 创建能力
     * @return
     */
    public E save(CreateAbility<E> createAbility) {
        return (E) dao.save(createAbility.transformation());
    }

    //更新能力

    /**
     * @param updateAbility 更新能力
     * @param e             更新的对象
     * @return
     */
    public E update(UpdateAbility<E> updateAbility, E e) {
        return (E) dao.save(updateAbility.transformation(e));
    }

    /**
     * @param predicate 筛选条件
     * @param pageModel 分页对象
     * @return
     */
    @Transactional(readOnly = true)
    public Page<E> findAll(Predicate predicate, PageModel pageModel) {
        return dao.findAll(predicate, pageModel.getPageable());
    }

    /**
     * @param screenAbility 筛选能力
     * @param pageModel     分页对象
     * @return
     */
    @Transactional(readOnly = true)
    public Page<E> findAllScreen(ScreenAbility screenAbility, PageModel pageModel) {
        return dao.findAll(screenAbility.getPredicate(), pageModel.getPageable());
    }

    /**
     * 分页排序查询 querydsl
     *
     * @param pagenation
     * @return
     */
    public Pagenation<E> pageQuery(Pagenation pagenation, Predicate predicate) {
        Sort sort = Sort.by(pagenation.getPageParam().getDirection());
        Pageable pageable = PageRequest.of(pagenation.getPageParam().getPageNo() - 1, pagenation.getPageParam().getPageSize(), sort);
        Page<E> page = dao.findAll(predicate, pageable);
        return pagenation.setData(page.getContent(), page.getTotalElements(), page.getTotalPages());
    }

    /**
     * 原生sql 多表关联分页查询 映射Map 或者 Class
     * @param countSql
     * @param sql
     * @param pageModel
     * @param result  映射的对象 （Map 或者 Class）
     * @return
     */
    public Page createNativePageQuery(StringBuilder countSql , StringBuilder sql , PageModel pageModel,ResultTransformer result){
        Query query1 = entityManager.createNativeQuery(countSql.toString());
        long count =((BigInteger) query1.getSingleResult()).longValue() ;
        if(pageModel.getProperty()!=null && pageModel.getProperty().size()>0 && pageModel.getDirection().size() == pageModel.getProperty().size()){
            sql.append(" order by") ;
            for(int i = 0 ; i < pageModel.getProperty().size() ; i++){
                sql.append(" "+pageModel.getProperty().get(i)+" "+pageModel.getDirection().get(i)+" ");
                if(i < pageModel.getProperty().size()-1){
                    sql.append(",");
                }
            }
        }
        sql.append(" limit "+pageModel.getPageSize()*(pageModel.getPageNo()-1)+" , "+pageModel.getPageSize());
        jakarta.persistence.Query query2 = entityManager.createNativeQuery(sql.toString());
        query2.unwrap(NativeQuery.class).setResultTransformer(result);
        List list = query2.getResultList() ;
        return new PageImpl<>(list,pageModel.getPageable(),count);
    }

}
