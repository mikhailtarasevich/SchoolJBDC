package com.mikhail.tarasevich.dao.impl;

import com.mikhail.tarasevich.dao.ConnectorDB;
import com.mikhail.tarasevich.dao.CrudPageableDao;
import com.mikhail.tarasevich.dao.exception.DataBaseSqlRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPageableCrudDaoImpl<E> extends AbstractCrudDaoImpl<E> implements CrudPageableDao<E> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPageableCrudDaoImpl.class);
    private final String findAllPageableQuery;
    private final String countTableRowsQuery;

    public AbstractPageableCrudDaoImpl(ConnectorDB connector, String saveEntityQuery, String findByIdQuery,
                                       String findAllQuery, String findAllPageableQuery, String updateEntityQuery,
                                       String deleteByIdQuery, String countTableRowsQuery) {
        super(connector, saveEntityQuery, findByIdQuery, findAllQuery, updateEntityQuery, deleteByIdQuery);
        this.findAllPageableQuery = findAllPageableQuery;
        this.countTableRowsQuery = countTableRowsQuery;
    }

    @Override
    public List<E> findAll(int page, int itemsPerPage) {
        int offsetToPage = page * itemsPerPage;
        try (final Connection connection = connector.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(findAllPageableQuery)) {
            preparedStatement.setInt(1, itemsPerPage);
            preparedStatement.setInt(2, offsetToPage);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<E> entities = new ArrayList<>();
            while (resultSet.next()) {
                entities.add(mapResultSetToEntity(resultSet));
            }
            return entities;
        } catch (SQLException e) {
            LOG.error("Entities wasn't found in DB by SQL query: {}. Thrown exception: {}",
                    findAllPageableQuery, e);
            throw new DataBaseSqlRuntimeException("", e);
        }
    }

    @Override
    public long count() {
        try (final Connection connection = connector.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(countTableRowsQuery)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
        } catch (SQLException e) {
            LOG.error("Rows weren't counted in DB table by SQL query: {}. Thrown exception: {}",
                    countTableRowsQuery, e);
            throw new DataBaseSqlRuntimeException("", e);
        }
        return 0;
    }

}
