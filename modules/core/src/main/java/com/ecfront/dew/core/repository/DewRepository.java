package com.ecfront.dew.core.repository;

import com.ecfront.dew.core.dto.PageDTO;
import com.ecfront.dew.core.entity.IdEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import java.util.List;

@NoRepositoryBean
public interface DewRepository<E extends IdEntity> extends JpaRepository<E, Long> {

    EntityManager em();

    E updateById(long id, E entity);

    E updateByCode(String code, E entity);

    E getById(long id);

    E getByCode(String code);

    void deleteById(long id);

    void deleteByCode(String code);

    List<E> findEnable();

    List<E> findDisable();

    PageDTO<E> paging(int pageNumber, int pageSize, Sort sort);

    PageDTO<E> pagingEnable(int pageNumber, int pageSize, Sort sort);

    PageDTO<E> pagingDisable(int pageNumber, int pageSize, Sort sort);

    void enableById(long id);

    void enableByCode(String code);

    void disableById(long id);

    void disableByCode(String code);

    boolean existById(long id);

    boolean existByCode(String code);

    default <E> PageDTO<E> pageConvert(Page<E> page) {
        return PageDTO.build(page.getNumber(), page.getNumberOfElements(), page.getTotalElements(), page.getContent());
    }

}

