package com.ecfront.dew.core.repository;

import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.dto.PageDTO;
import com.ecfront.dew.core.entity.IdEntity;
import com.ecfront.dew.core.entity.SafeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

public class DewRepositoryImpl<E extends IdEntity> extends SimpleJpaRepository<E, Long> implements DewRepository<E> {

    protected Class<E> modelClazz = null;

    protected final EntityManager entityManager;

    public DewRepositoryImpl(JpaEntityInformation<E, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    public DewRepositoryImpl(Class<E> domainClass, EntityManager em) {
        super(domainClass, em);
        modelClazz = domainClass;
        this.entityManager = em;
    }

    @Override
    public E update(long id, E entity) {
        entity.setId(id);
        if (entity instanceof SafeEntity) {
            SafeEntity e = (SafeEntity) entity;
            e.setUpdateTime(new Date());
            if (Dew.context().optInfo().isPresent()) {
                e.setUpdateUser(Dew.context().optInfo().get().getLoginId());
            } else {
                e.setUpdateUser("");
            }
        }
        return entityManager.merge(entity);
    }

    @Override
    public List<E> findEnable() {
        return super.findAll((root, query, cb) -> cb.equal(root.get("enable"), true));
    }

    @Override
    public List<E> findDisable() {
        return super.findAll((root, query, cb) -> cb.equal(root.get("enable"), false));
    }

    @Override
    public PageDTO<E> paging(int pageNumber, int pageSize, Sort sort) {
        Pageable pageRequest;
        if (sort == null) {
            pageRequest = new PageRequest(pageNumber, pageSize);
        } else {
            pageRequest = new PageRequest(pageNumber, pageSize, sort);
        }
        Page result = super.findAll(pageRequest);
        return PageDTO.build(pageNumber, pageSize, result.getTotalPages(), result.getContent());
    }

    @Override
    public PageDTO<E> pagingEnable(int pageNumber, int pageSize, Sort sort) {
        return pagingStatus(pageNumber, pageSize, sort, true);
    }

    @Override
    public PageDTO<E> pagingDisable(int pageNumber, int pageSize, Sort sort) {
        return pagingStatus(pageNumber, pageSize, sort, false);
    }

    private PageDTO<E> pagingStatus(int pageNumber, int pageSize, Sort sort, boolean enable) {
        Pageable pageRequest;
        if (sort == null) {
            pageRequest = new PageRequest(pageNumber, pageSize);
        } else {
            pageRequest = new PageRequest(pageNumber, pageSize, sort);
        }
        Page result = super.findAll((root, query, cb) -> cb.equal(root.get("enable"), enable), pageRequest);
        return PageDTO.build(pageNumber, pageSize, result.getTotalPages(), result.getContent());
    }

    @Override
    public void enable(long id) {
        changeStatus(id, true);
    }

    @Override
    public void disable(long id) {
        changeStatus(id, false);
    }

    private void changeStatus(long id, boolean status) {
        Query q = entityManager.createQuery(String.format("UPDATE %s SET enable = ?1 WHERE id= ?2", modelClazz.getSimpleName()));
        q.setParameter(1, status);
        q.setParameter(2, id);
        q.executeUpdate();
    }

    @Override
    public <S extends E> S save(S entity) {
        if (entity instanceof SafeEntity) {
            SafeEntity e = (SafeEntity) entity;
            e.setCreateTime(new Date());
            e.setUpdateTime(e.getCreateTime());
            if (Dew.context().optInfo().isPresent()) {
                e.setCreateUser(Dew.context().optInfo().get().getLoginId());
                e.setUpdateUser(e.getCreateUser());
            } else {
                e.setCreateUser("");
                e.setUpdateUser("");
            }
        }
        entityManager.persist(entity);
        return entity;
    }


}

