/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package com.mysema.query.hql;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysema.query.QueryModifiers;
import com.mysema.query.SearchResults;
import com.mysema.query.grammar.HqlGrammar;
import com.mysema.query.grammar.HqlOps;
import com.mysema.query.grammar.HqlQueryBase;
import com.mysema.query.grammar.types.Expr;

/**
 * AbstractJpaqlQuery provides
 *
 * @author tiwe
 * @version $Id$
 */
public class AbstractJpaqlQuery<SubType extends AbstractJpaqlQuery<SubType>> extends HqlQueryBase<SubType>{
    
    private static final Logger logger = LoggerFactory.getLogger(JpaqlQuery.class);
    
    private final EntityManager em;

    public AbstractJpaqlQuery(EntityManager em) {
        this(em, HqlOps.DEFAULT);
    }

    public AbstractJpaqlQuery(EntityManager em, HqlOps ops) {
        super(ops);
        this.em = em;
    }
    
    private Query createQuery(String queryString, QueryModifiers modifiers) {
        Query query = em.createQuery(queryString);
        setConstants(query, getConstants());        
        if (modifiers != null){
        	if (modifiers.getLimit() != null){
        		query.setMaxResults(modifiers.getLimit().intValue());
        	}
            if (modifiers.getOffset() != null){
            	query.setFirstResult(modifiers.getOffset().intValue());	
            }
        }  
        return query;
    }
        
    public static void setConstants(Query query, List<Object> constants) {
        for (int i=0; i < constants.size(); i++){
            String key = "a"+(i+1);
            Object val = constants.get(i);            
            query.setParameter(key,val);
        }        
    }

    @SuppressWarnings("unchecked")
    public <RT> List<RT> list(Expr<RT> expr){
        addToProjection(expr);
        String queryString = toString();
        logger.debug("query : {}", queryString);
        Query query = createQuery(queryString, getMetadata().getModifiers());
        return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<Object[]> list(Expr<?> expr1, Expr<?> expr2, Expr<?>...rest){
        addToProjection(expr1, expr2);
        addToProjection(rest);
        String queryString = toString();
        logger.debug("query : {}", queryString);
        Query query = createQuery(queryString, getMetadata().getModifiers());
        return query.getResultList();
    }

    public <RT> SearchResults<RT> listResults(Expr<RT> expr) {
        addToProjection(expr);
        Query query = createQuery(toCountRowsString(),null);
        long total = (Long) query.getSingleResult();
        if (total > 0) {
        	QueryModifiers modifiers = getMetadata().getModifiers();
            String queryString = toString();
            logger.debug("query : {}", queryString);
            query = createQuery(queryString, modifiers);
            @SuppressWarnings("unchecked")
            List<RT> list = query.getResultList();
            return new SearchResults<RT>(list, modifiers, total);
        } else {
            return SearchResults.emptyResults();
        }
    }
    
    public long count() {
        return uniqueResult(HqlGrammar.count());
    }

    public <RT> Iterator<RT> iterate(Expr<RT> projection) {
        // TODO Auto-generated method stub
        return list(projection).iterator();
    }

    public Iterator<Object[]> iterate(Expr<?> first, Expr<?> second,
            Expr<?>... rest) {
        // TODO Auto-generated method stub
        return list(first, second, rest).iterator();
    }

}
