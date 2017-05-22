package com.ecfront.dew.core.repository;

import com.ecfront.dew.common.PageDTO;
import com.ecfront.dew.core.entity.IdEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@NoRepositoryBean
public interface DewRepository<E extends IdEntity> extends JpaRepository<E, Long> {

    EntityManager em();

    @Override
    @Transactional
    <S extends E> List<S> save(Iterable<S> entities);

    @Transactional
    E updateById(long id, E entity);

    @Transactional
    E updateByCode(String code, E entity);

    E getById(long id);

    E getByCode(String code);

    @Transactional
    void deleteById(long id);

    @Transactional
    void deleteByCode(String code);

    List<E> findEnable();

    List<E> findDisable();

    PageDTO<E> paging(int pageNumber, int pageSize, Sort sort);

    PageDTO<E> pagingEnable(int pageNumber, int pageSize, Sort sort);

    PageDTO<E> pagingDisable(int pageNumber, int pageSize, Sort sort);

    @Transactional
    void enableById(long id);

    @Transactional
    void enableByCode(String code);

    @Transactional
    void disableById(long id);

    @Transactional
    void disableByCode(String code);

    boolean existById(long id);

    boolean existByCode(String code);

    default <E> PageDTO<E> pageConvert(Page<E> page) {
        return PageDTO.build(page.getNumber(), page.getNumberOfElements(), page.getTotalElements(), page.getContent());
    }

}

