package com.mikhail.tarasevich.dao;

import java.util.List;
import java.util.Optional;

public interface CrudDao<E> {

    //create
    E save(E entity);
    void saveAll(List<E> entity);

    //read
    Optional<E> findById(Integer id);
    List<E> findAll();

    //update
    void update(E entity);

    //delete
    void deleteById(Integer param);

}
