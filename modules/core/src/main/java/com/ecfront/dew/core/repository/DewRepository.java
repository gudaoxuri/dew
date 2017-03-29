package com.ecfront.dew.core.repository;

import com.ecfront.dew.core.dto.PageDTO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@NoRepositoryBean
public interface DewRepository<E, ID extends Serializable> extends JpaRepository<E, ID> {

    @Transactional
    E update(ID id, E entity);

    @Transactional
    PageDTO<E> paging(int pageNumber, int pageSize, Sort sort);

}

