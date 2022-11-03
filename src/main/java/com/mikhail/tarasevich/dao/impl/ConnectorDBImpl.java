package com.mikhail.tarasevich.dao.impl;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mikhail.tarasevich.dao.ConnectorDB;
import com.mikhail.tarasevich.dao.exception.DataBaseSqlRuntimeException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ConnectorDBImpl implements ConnectorDB {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectorDBImpl.class);
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    @Inject
    public ConnectorDBImpl(@Named("filePath") String filePath) {
        ResourceBundle resource = ResourceBundle.getBundle(filePath);
        config.setJdbcUrl(resource.getString("db.url"));
        config.setUsername(resource.getString("db.user"));
        config.setPassword(resource.getString("db.password"));
        ds = new HikariDataSource(config);
        LOG.debug("Object of ConnectorDBImpl.class has been created");
    }

    public Connection getConnection() {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            LOG.error("Troubles with connection. Thrown exception: {}", e);
            throw new DataBaseSqlRuntimeException("", e);
        }
    }

}
