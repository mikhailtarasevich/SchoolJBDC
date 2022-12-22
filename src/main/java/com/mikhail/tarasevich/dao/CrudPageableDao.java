package com.mikhail.tarasevich.dao;

import java.util.List;

public interface CrudPageableDao<E> extends CrudDao<E> {

    List<E> findAll(int page, int itemsPerPage);

    long count();

}
