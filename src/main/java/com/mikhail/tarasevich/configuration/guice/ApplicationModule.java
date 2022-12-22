package com.mikhail.tarasevich.configuration.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import com.mikhail.tarasevich.dao.*;
import com.mikhail.tarasevich.dao.impl.*;
import com.mikhail.tarasevich.provider.EntityViewProvider;
import com.mikhail.tarasevich.provider.impl.EntityViewProviderImpl;
import com.mikhail.tarasevich.reader.ConsoleReader;
import com.mikhail.tarasevich.reader.impl.ConsoleReaderImpl;

public class ApplicationModule extends AbstractModule {

    private static final String DB_PROPERTIES_FILE_PATH = "database";

    @Override
    protected void configure() {

        bind(ConnectorDB.class).to(ConnectorDBImpl.class).in(Scopes.SINGLETON);
        bind(StudentDao.class).to(StudentDaoImpl.class).in(Scopes.SINGLETON);
        bind(CourseDao.class).to(CourseDaoImpl.class).in(Scopes.SINGLETON);
        bind(GroupDao.class).to(GroupDaoImpl.class).in(Scopes.SINGLETON);
        bind(EntityViewProvider.class).to(EntityViewProviderImpl.class).in(Scopes.SINGLETON);
        bind(ScriptRunner.class).to(ScriptRunnerImpl.class).in(Scopes.SINGLETON);
        bind(ConsoleReader.class).to(ConsoleReaderImpl.class).in(Scopes.SINGLETON);

        bind(String.class)
                .annotatedWith(Names.named("filePath"))
                .toInstance(DB_PROPERTIES_FILE_PATH);
    }

}
