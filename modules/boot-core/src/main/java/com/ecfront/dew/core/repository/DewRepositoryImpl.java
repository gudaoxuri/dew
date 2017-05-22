package com.ecfront.dew.core.repository;

import com.ecfront.dew.common.BeanHelper;
import com.ecfront.dew.core.Dew;
import com.ecfront.dew.common.PageDTO;
import com.ecfront.dew.core.entity.EntityContainer;
import com.ecfront.dew.core.entity.IdEntity;
import com.ecfront.dew.core.entity.SafeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
    public EntityManager em() {
        return this.entityManager;
    }

    @Override
    @Transactional
    public <S extends E> S save(S entity) {
        Optional<EntityContainer.EntityClassInfo> entityClassInfo = EntityContainer
                .getCodeFieldNameByClazz(modelClazz);
        if (entityClassInfo.isPresent() && entityClassInfo.get().codeFieldUUID) {
            try {
                Object code = BeanHelper.getValue(entity, entityClassInfo.get().codeFieldName);
                if (code == null || ((String) code).isEmpty()) {
                    BeanHelper.setValue(entity, entityClassInfo.get().codeFieldName, Dew.Util.createUUID());
                }
            } catch (NoSuchFieldException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
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
        entityManager.flush();
        return entity;
    }

    @Override
    @Transactional
    public E updateById(long id, E entity) {
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
        entity= entityManager.merge(entity);
        entityManager.flush();
        return entity;
    }

    @Override
    @Transactional
    public E updateByCode(String code, E entity) {
        long id = getByCode(code).getId();
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
        entity= entityManager.merge(entity);
        entityManager.flush();
        return entity;
    }

    @Override
    public E getById(long id) {
        return super.findOne(id);
    }

    @Override
    public E getByCode(String code) {
        return EntityContainer
                .getCodeFieldNameByClazz(modelClazz)
                .map(s ->
                        super.findOne((root, query, cb) -> cb.equal(root.get(s.codeFieldName), code)))
                .orElse(null);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        super.delete(getById(id));
        entityManager.flush();
    }

    @Override
    @Transactional
    public void deleteByCode(String code) {
        super.delete(getByCode(code));
        entityManager.flush();
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
        return PageDTO.build(pageNumber, pageSize, result.getTotalElements(), result.getContent());
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
    @Transactional
    public void enableById(long id) {
        changeStatus(id, null, true);
    }

    @Override
    @Transactional
    public void enableByCode(String code) {
        changeStatus(-1, code, true);
    }

    @Override
    @Transactional
    public void disableById(long id) {
        changeStatus(id, null, false);
    }

    @Override
    @Transactional
    public void disableByCode(String code) {
        changeStatus(-1, code, false);
    }

    @Override
    public boolean existById(long id) {
        return super.exists(id);
    }

    @Override
    public boolean existByCode(String code) {
        return (long) entityManager
                .createQuery(String.format("SELECT count(1) FROM %s WHERE %s = ?1", modelClazz.getSimpleName(), EntityContainer.getCodeFieldNameByClazz(modelClazz).get().codeFieldName))
                .setParameter(1, code)
                .getResultList().get(0) > 0;
    }

    private void changeStatus(long id, String code, boolean status) {
        String whereField = id == -1 ? EntityContainer.getCodeFieldNameByClazz(modelClazz).get().codeFieldName : "id";
        Object whereValue = id == -1 ? code : id;
        Query q = entityManager.createQuery(String.format("UPDATE %s SET enable = ?1 WHERE %s = ?2", modelClazz.getSimpleName(), whereField));
        q.setParameter(1, status);
        q.setParameter(2, whereValue);
        q.executeUpdate();
    }


}

