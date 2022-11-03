package com.mikhail.tarasevich.dao.impl;

import com.mikhail.tarasevich.dao.ConnectorDB;
import com.mikhail.tarasevich.dao.CrudDao;
import com.mikhail.tarasevich.dao.exception.DataBaseSqlRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public abstract class AbstractCrudDaoImpl<E> implements CrudDao<E> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractCrudDaoImpl.class);
    private static final BiConsumer<PreparedStatement, Integer> INT_PARAM_SETTER = (preparedStatement, integer) -> {
        try {
            preparedStatement.setInt(1, integer);
        } catch (SQLException e) {
            LOG.error("Incorrect parameters sent to INT_PARAM_SETTER. Thrown exception: {}", e);
        }
    };
    private static final BiConsumer<PreparedStatement, String> STRING_PARAM_SETTER = (preparedStatement, str) -> {
        try {
            preparedStatement.setString(1, str);
        } catch (SQLException e) {
            LOG.error("Incorrect parameters sent to STRING_PARAM_SETTER. Thrown exception: {}", e);
        }
    };
    protected final ConnectorDB connector;
    private final String saveEntityQuery;
    private final String findByIdQuery;
    private final String findAllQuery;
    private final String updateEntityQuery;
    private final String deleteByIdQuery;

    protected AbstractCrudDaoImpl(ConnectorDB connector, String saveEntityQuery, String findByIdQuery,
                                  String findAllQuery, String updateEntityQuery, String deleteByIdQuery) {
        this.connector = connector;
        this.saveEntityQuery = saveEntityQuery;
        this.findByIdQuery = findByIdQuery;
        this.findAllQuery = findAllQuery;
        this.updateEntityQuery = updateEntityQuery;
        this.deleteByIdQuery = deleteByIdQuery;
    }

    @Override
    public E save(E entity) {
        try (final Connection connection = connector.getConnection();
             final PreparedStatement preparedStatement = connection
                     .prepareStatement(saveEntityQuery, Statement.RETURN_GENERATED_KEYS)) {
            setStatementForSave(preparedStatement, entity);
            int entityId = getGeneratedId(preparedStatement);
            LOG.info("Entity of class {} was saved in DB with id = {}. Entity parameters: {} ",
                    entity.getClass().getSimpleName(), entityId, entity);
            return makeEntityWithId(entity, entityId);
        } catch (SQLException e) {
            LOG.error("Entity of class {} wasn't saved in DB. Entity parameters: {}. Thrown exception: {}",
                    entity.getClass().getSimpleName(), entity, e);
            throw new DataBaseSqlRuntimeException("", e);
        }
    }

    @Override
    public void saveAll(List<E> entities) {
        try (final Connection connection = connector.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(saveEntityQuery)) {
            for (E entity : entities) {
                setStatementForSave(preparedStatement, entity);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            LOG.info("{} entities was saved in DB. Entities parameters: {} ",
                    entities.get(0).getClass().getSimpleName(), entities);
        } catch (SQLException e) {
            LOG.error("{} entities wasn't saved in DB. Entities parameters: {}. Thrown exception: {}",
                    entities.get(0).getClass().getSimpleName(), entities, e);
            throw new DataBaseSqlRuntimeException("", e);
        }
    }

    @Override
    public Optional<E> findById(Integer id) {
        return findByIntParam(id, findByIdQuery);
    }

    @Override
    public List<E> findAll() {
        try (final Connection connection = connector.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(findAllQuery)) {
            return collectFoundEntitiesToList(preparedStatement);
        } catch (SQLException e) {
            LOG.error("Entities wasn't found in DB by SQL query: {}. Thrown exception: {}", findAllQuery, e);
            throw new DataBaseSqlRuntimeException("", e);
        }
    }

    @Override
    public void update(E entity) {
        try (final Connection connection = connector.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(updateEntityQuery)) {
            setStatementForUpdate(preparedStatement, entity);
            preparedStatement.executeUpdate();
            LOG.info("{} entity was updated in DB. Entities parameters: {} ",
                    entity.getClass().getSimpleName(), entity);
        } catch (SQLException e) {
            LOG.error("{} entity wasn't updated in DB. Entity parameters: {}. Thrown exception: {}",
                    entity.getClass().getSimpleName(), entity, e);
            throw new DataBaseSqlRuntimeException("", e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        deleteById(id, INT_PARAM_SETTER);
    }

    protected Optional<E> findByIntParam(int param, String findByParam) {
        return findByParam(param, findByParam, INT_PARAM_SETTER);
    }

    protected Optional<E> findByStringParam(String param, String findByParam) {
        return findByParam(param, findByParam, STRING_PARAM_SETTER);
    }

    protected List<E> findManyByIntParam(int param, String findQuery) {
        return findManyByParam(param, findQuery, INT_PARAM_SETTER);
    }

    protected List<E> findManyByStringParam(String param, String findQuery) {
        return findManyByParam(param, findQuery, STRING_PARAM_SETTER);
    }

    private <P> Optional<E> findByParam(P param, String findByParam, BiConsumer<PreparedStatement, P> designatedParamSetter) {
        try (final Connection connection = connector.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(findByParam)) {
            designatedParamSetter.accept(preparedStatement, param);
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToEntity(resultSet));
                }
            }
        } catch (SQLException e) {
            LOG.error("Entity wasn't found in DB by parameter = {} (SQL query: {}). Thrown exception: {}",
                    param, findByParam, e);
            throw new DataBaseSqlRuntimeException("", e);
        }
        return Optional.empty();
    }

    private <P> List<E> findManyByParam(P param, String findByParam, BiConsumer<PreparedStatement, P> designatedParamSetter) {
        try (final Connection connection = connector.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(findByParam)) {
            designatedParamSetter.accept(preparedStatement, param);
            return collectFoundEntitiesToList(preparedStatement);
        } catch (SQLException e) {
            LOG.error("Entities wasn't found in DB by parameter = {} (SQL query: {}). Thrown exception: {}",
                    param, findByParam, e);
            throw new DataBaseSqlRuntimeException("", e);
        }
    }

    private void deleteById(Integer id, BiConsumer<PreparedStatement, Integer> designatedParamSetter) {
        try (final Connection connection = connector.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(deleteByIdQuery)) {
            designatedParamSetter.accept(preparedStatement, id);
            preparedStatement.executeUpdate();
            LOG.info("Entity was deleted from DB by id = {} (SQL query: {}).",
                    id, deleteByIdQuery);
        } catch (SQLException e) {
            LOG.error("Entity wasn't deleted from DB by id = {} (SQL query: {}). Thrown exception: {}",
                    id, deleteByIdQuery, e);
            throw new DataBaseSqlRuntimeException("", e);
        }
    }

    private List<E> collectFoundEntitiesToList(PreparedStatement preparedStatement) {
        try (final ResultSet resultSet = preparedStatement.executeQuery()) {
            List<E> entities = new ArrayList<>();
            while (resultSet.next()) {
                final E entity = mapResultSetToEntity(resultSet);
                entities.add(entity);
            }
            return entities;
        } catch (SQLException e) {
            LOG.error("Found entities wasn't collected to list. Thrown exception: {}", e);
            throw new DataBaseSqlRuntimeException("", e);
        }
    }

    private Integer getGeneratedId(PreparedStatement preparedStatement) throws SQLException {
        int affectedRows = preparedStatement.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("Creating user failed, no rows affected.");
        }
        try (final ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return (int) generatedKeys.getLong(1);
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        }
    }

    protected abstract void setStatementForSave(PreparedStatement preparedStatement, E entity);

    protected abstract void setStatementForUpdate(PreparedStatement preparedStatement, E entity);

    protected abstract E mapResultSetToEntity(ResultSet resultSet) throws SQLException;

    protected abstract E makeEntityWithId(E entity, int id);

}
