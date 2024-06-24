package com.solo83.weatherapp.repository;

import com.solo83.weatherapp.utils.exception.RepositoryException;

import java.util.List;
import java.util.Optional;

public interface Repository <K,T> {

    Optional<T> findById(K id) throws RepositoryException;
    List<T> findAll() throws RepositoryException;
    Optional<T> save(T entity) throws RepositoryException;
    boolean delete(K id) throws RepositoryException;
    Optional<T> update(T entity) throws RepositoryException;

}
