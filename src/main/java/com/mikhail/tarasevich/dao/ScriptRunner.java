package com.mikhail.tarasevich.dao;

import java.sql.Connection;

public interface ScriptRunner {
    void runScript (String scriptFilePath);
}
