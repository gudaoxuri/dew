package com.ecfront.dew.core.repository;

import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.dto.PageDTO;
import com.ecfront.dew.core.entity.SafeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Date;

public class DewRepositoryImpl<E, ID extends Serializable> extends SimpleJpaRepository<E, ID> implements DewRepository<E, ID> {

    private final EntityManager entityManager;

    public DewRepositoryImpl(JpaEntityInformation<E, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    public DewRepositoryImpl(Class<E> domainClass, EntityManager em) {
        super(domainClass, em);
        this.entityManager = em;
    }

    @Transactional
    @Override
    public E update(ID id, E entity) {
        if (entity instanceof SafeEntity) {
            SafeEntity e = (SafeEntity) entity;
            e.setUpdateTime(new Date());
            if (Dew.context().optInfo().isPresent()) {
                e.setUpdateUser(Dew.context().optInfo().get().getLoginId());
            }
        }
        return entityManager.merge(entity);
    }

    @Override
    public PageDTO<E> paging(int pageNumber, int pageSize, Sort sort) {
        Page result;
        if (sort == null) {
            result = super.findAll(new PageRequest(pageNumber, pageSize));
        } else {
            result = super.findAll(new PageRequest(pageNumber, pageSize, sort));
        }
        return PageDTO.build(pageNumber, pageSize, result.getTotalPages(), result.getContent());
    }

    @Transactional
    @Override
    public <S extends E> S save(S entity) {
        if (entity instanceof SafeEntity) {
            SafeEntity e = (SafeEntity) entity;
            e.setCreateTime(new Date());
            if (Dew.context().optInfo().isPresent()) {
                e.setCreateUser(Dew.context().optInfo().get().getLoginId());
            }
        }
        entityManager.persist(entity);
        return entity;
    }


}

