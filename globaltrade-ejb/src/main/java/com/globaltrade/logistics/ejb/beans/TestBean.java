package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.annotation.Test;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
@Test
public class TestBean {
//    @PersistenceContext(unitName = "GlobalTradeLogisticsPU")
//    private EntityManager em;
//
//    @PostConstruct
//    public void init() {
//        System.out.println("=================================");
//        System.out.println("GlobalTrade JPA initialized");
//        System.out.println("EntityManager: " + em);
//        System.out.println("=================================");
//    }

    public void m(){
        System.out.println("TestBean m()");
    }


}
