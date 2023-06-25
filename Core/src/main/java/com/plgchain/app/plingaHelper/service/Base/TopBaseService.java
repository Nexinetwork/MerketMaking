package com.plgchain.app.plingaHelper.service.Base;

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

public class TopBaseService<E, D extends BaseLongDao> {

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
    * @param createAbility Create ability
    * @return
    */
    public E save(CreateAbility<E> createAbility) {
        return (E) dao.save(createAbility.transformation());
    }

    /**
     * @param updateAbility Update ability
     * @param e             Object to be updated
     * @return
     */
    public E update(UpdateAbility<E> updateAbility, E e) {
        return (E) dao.save(updateAbility.transformation(e));
    }

    /**
    * @param predicate Filter predicate
    * @param pageModel Pagination object
    * @return
    */
    @Transactional(readOnly = true)
    public Page<E> findAll(Predicate predicate, PageModel pageModel) {
        return dao.findAll(predicate, pageModel.getPageable());
    }

    /**
    * @param screenAbility Filter ability
    * @param pageModel Pagination object
    * @return
    */
    @Transactional(readOnly = true)
    public Page<E> findAllScreen(ScreenAbility screenAbility, PageModel pageModel) {
        return dao.findAll(screenAbility.getPredicate(), pageModel.getPageable());
    }

    /**
    * Pagination and sorting query using Querydsl
    *
    * @param pagination
    * @return
    */
    public Pagenation<E> pageQuery(Pagenation pagenation, Predicate predicate) {
        Sort sort = Sort.by(pagenation.getPageParam().getDirection());
        Pageable pageable = PageRequest.of(pagenation.getPageParam().getPageNo() - 1, pagenation.getPageParam().getPageSize(), sort);
        Page<E> page = dao.findAll(predicate, pageable);
        return pagenation.setData(page.getContent(), page.getTotalElements(), page.getTotalPages());
    }

    /**
    * Native SQL multiple table join pagination query, mapping to Map or Class
    * @param countSql
    * @param sql
    * @param pageModel
    * @param result The mapped object (Map or Class)
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
