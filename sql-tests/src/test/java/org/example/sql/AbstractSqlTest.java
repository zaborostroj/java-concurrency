package org.example.sql;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;

@Testcontainers
public class AbstractSqlTest {

    @Container
    static PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    static Connection connection;

    @BeforeAll
    static void setUp() throws Exception {
        connection = DriverManager.getConnection(
                database.getJdbcUrl(),
                database.getUsername(),
                database.getPassword()
        );

        JdbcConnection jdbcConnection = new JdbcConnection(connection);
        var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbcConnection);
        var liquibase = new Liquibase(
                "db/changelog/db.changelog-master.xml",
                new ClassLoaderResourceAccessor(),
                database
        );
        liquibase.update(new Contexts(), new LabelExpression());
    }
}
