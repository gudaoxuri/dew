package com.ecfront.dew.core.repository;

import com.ecfront.dew.core.dto.PageDTO;
import com.ecfront.dew.core.entity.IdEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface DewRepository<E extends IdEntity> extends JpaRepository<E, Long> {

    E update(long id, E entity);

    List<E> findEnable();

    List<E> findDisable();

    PageDTO<E> paging(int pageNumber, int pageSize, Sort sort);

    PageDTO<E> pagingEnable(int pageNumber, int pageSize, Sort sort);

    PageDTO<E> pagingDisable(int pageNumber, int pageSize, Sort sort);

    void enable(long id);

    void disable(long id);

}

