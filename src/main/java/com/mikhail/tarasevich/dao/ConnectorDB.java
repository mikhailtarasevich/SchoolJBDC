package com.mikhail.tarasevich.dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectorDB {
    Connection getConnection () throws SQLException;
}
