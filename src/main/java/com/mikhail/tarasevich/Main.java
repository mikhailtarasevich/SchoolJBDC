package com.mikhail.tarasevich;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mikhail.tarasevich.configuration.guice.ApplicationModule;
import com.mikhail.tarasevich.dao.ScriptRunner;
import com.mikhail.tarasevich.dao.impl.*;
import com.mikhail.tarasevich.uploader.DataSourceUploader;

public class Main {

    //Constants
    private static final String SCRIPT_FILE_PATH = "src/main/resources/sql/schema.SQL";
    private static final int GENERATE_GROUP_QUANTITY = 3;
    private static final int GENERATE_STUDENT_QUANTITY = 30;
    private static final int ITEMS_PER_PAGE = 10;

    //Guice injectors
    private static final Injector guice = Guice.createInjector(new ApplicationModule());

    //DAO classes
    private static final ScriptRunner scriptRunner = guice.getInstance(ScriptRunnerImpl.class);

    //Factories
    private static final DataSourceUploader dataSourceUploader = guice.getInstance(DataSourceUploader.class);

    //Controllers
    private static final FrontController frontController = guice.getInstance(FrontController.class);

    public static void main(String[] args) {

        //ScriptRunner - create empty structure of database
        scriptRunner.runScript(SCRIPT_FILE_PATH);

        //DataSourceUploader - fill database tables with random data. (Courses quantity is ten and it is constanta)
        dataSourceUploader.uploadRandomDataToDB(GENERATE_GROUP_QUANTITY, GENERATE_STUDENT_QUANTITY);

        //FromController - entry point to the application
        frontController.startMenu(ITEMS_PER_PAGE);
    }

}
