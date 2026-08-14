package com.globaltrade.logistics.ejb.beans;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class TestBean {
    @PersistenceContext(unitName = "GlobalTradeLogisticsPU")
    private EntityManager em;

    @PostConstruct
    public void init() {
        System.out.println("=================================");
        System.out.println("GlobalTrade JPA initialized");
        System.out.println("EntityManager: " + em);
        System.out.println("=================================");
    }

}
