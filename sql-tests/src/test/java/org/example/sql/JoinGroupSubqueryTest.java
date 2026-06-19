package org.example.sql;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class JoinGroupSubqueryTest {

    @Container
    static PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    private static Connection connection;

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

    @Test
    void testTablesExist() throws Exception {
        try (Statement statement = connection.createStatement()) {
            statement.executeQuery("SELECT * FROM customers LIMIT 1");
            statement.executeQuery("SELECT * FROM orders LIMIT 1");
            statement.executeQuery("SELECT * FROM employees_practice LIMIT 1");
        }
    }

    /**
     * пользователи без заказов
     */
    @Test
    void testCustomersWithoutOrders() throws Exception {
        String sql = """
                SELECT c.name
                FROM customers c
                LEFT JOIN orders o ON c.id = o.customer_id
                WHERE o.id IS NULL
                """;
        try (
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql)
        ) {
            int count = 0;
            while (rs.next()) {
                count++;
            }

            // Charlie, Eve, Heidi, Mallory, Oscar
            assertEquals(5, count);
        }
    }

    /**
     * количество заказов по пользователю
     */
    @Test
    void testOrderCountByCustomer() throws Exception {
        String sql = """
                SELECT c.name, COUNT(o.id) as order_count
                FROM customers c
                LEFT JOIN orders o ON c.id = o.customer_id
                GROUP BY c.name
                ORDER BY order_count DESC
                """;
        try (
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql)
        ) {
            assertTrue(rs.next());
            assertEquals("Bob", rs.getString("name"));
            assertEquals(6, rs.getInt("order_count"));
        }
    }

    /**
     * пользователи с >5 заказов
     */
    @Test
    void testCustomersWithManyOrders() throws Exception {
        String sql = """
                SELECT c.name
                FROM customers c
                JOIN orders o ON c.id = o.customer_id
                GROUP BY c.id, c.name
                HAVING COUNT(o.id) > 5
                """;
        try (
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql)
        ) {
            assertTrue(rs.next());
            assertEquals("Bob", rs.getString("name"));
        }
    }

    /**
     * второй максимальный salary
     */
    @Test
    void testSecondMaxSalary() throws Exception {
        String sql = """
                SELECT DISTINCT salary
                FROM employees_practice
                ORDER BY salary DESC
                OFFSET 1 LIMIT 1
                """;
        try (
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql)
        ) {
            assertTrue(rs.next());
            assertEquals(4500.00, rs.getDouble(1), 0.001);
        }
    }

    /**
     * найти дубликаты email
     */
    @Test
    void testDuplicateEmails() throws Exception {
        String sql = """
                SELECT email, COUNT(*)
                FROM customers
                GROUP BY email
                HAVING COUNT(*) > 1
                """;
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            int count = 0;
            while (rs.next()) {
                count++;
            }
            // alice@example.com, bob@example.com
            assertEquals(2, count);
        }
    }

    /**
     * топ-3 зарплаты
     */
    @Test
    void testTop3Salaries() throws Exception {
        String sql = """
                SELECT DISTINCT salary
                FROM employees_practice
                ORDER BY salary DESC
                LIMIT 3
                """;
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            rs.next();
            assertEquals(5000.00, rs.getDouble(1), 0.001);
            rs.next();
            assertEquals(4500.00, rs.getDouble(1), 0.001);
            rs.next();
            assertEquals(4000.00, rs.getDouble(1), 0.001);
        }
    }

    /**
     * пользователи без записей в другой таблице (через NOT EXISTS)
     */
    @Test
    void testCustomersWithoutOrdersSubquery() throws Exception {
        String sql = """
                SELECT c.name
                FROM customers c
                WHERE NOT EXISTS (
                    SELECT 1 FROM orders o WHERE o.customer_id = c.id
                )
                """;
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            int count = 0;
            while (rs.next()) {
                count++;
            }
            assertEquals(5, count);
        }
    }
}
