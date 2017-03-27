package com.ecfront.dew.core.repository;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import java.io.Serializable;

public class DewRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements DewRepository<T, ID> {

    private final EntityManager entityManager;

    public DewRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    public DewRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.entityManager = em;
    }


    @Override
    public void someFun(Long id) {
        System.out.print("do some fun");
    }

}

