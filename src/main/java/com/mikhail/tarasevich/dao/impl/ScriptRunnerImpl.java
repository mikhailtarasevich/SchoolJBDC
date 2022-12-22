package com.mikhail.tarasevich.dao.impl;

import com.google.inject.Inject;
import com.mikhail.tarasevich.dao.ConnectorDB;
import com.mikhail.tarasevich.dao.ScriptRunner;
import com.mikhail.tarasevich.dao.exception.DataBaseSqlRuntimeException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ScriptRunnerImpl implements ScriptRunner {

    private final ConnectorDB connectorDB;

    @Inject
    public ScriptRunnerImpl(ConnectorDB connectorDB){
        this.connectorDB = connectorDB;
    }

    @Override
    public void runScript(String scriptFilePath) {
        try (final Connection connection = connectorDB.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(sqlToString(scriptFilePath))) {
            preparedStatement.executeUpdate();
        } catch (SQLException | IOException e) {
            throw new DataBaseSqlRuntimeException("", e);
        }
    }

    private String sqlToString(String filePath) throws IOException {
        List<String> sqlInArray = readInfoFromFile(filePath);
        return sqlInArray.stream().collect(Collectors.joining("\n"));
    }

    private List<String> readInfoFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllLines(path);
    }

}
