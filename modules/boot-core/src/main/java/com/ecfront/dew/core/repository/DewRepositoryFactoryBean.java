package com.ecfront.dew.core.repository;


import com.ecfront.dew.core.entity.IdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;

public class DewRepositoryFactoryBean<T extends JpaRepository<E, Long>, E extends IdEntity> extends JpaRepositoryFactoryBean<T, E, Long> {

    public DewRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new DewRepositoryFactory<E>(entityManager);
    }

    private static class DewRepositoryFactory<E extends IdEntity> extends JpaRepositoryFactory {

        public DewRepositoryFactory(EntityManager entityManager) {
            super(entityManager);
        }

        @Override
        protected SimpleJpaRepository<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
            return new DewRepositoryImpl<>((Class<E>) information.getDomainType(), entityManager);
        }

        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            return DewRepositoryImpl.class;
        }
    }
}
