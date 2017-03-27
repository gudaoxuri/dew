package com.ecfront.dew.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface DewRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    void someFun(Long id);

}

