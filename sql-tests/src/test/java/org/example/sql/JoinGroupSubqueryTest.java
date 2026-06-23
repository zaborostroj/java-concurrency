package org.example.sql;

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JoinGroupSubqueryTest extends AbstractSqlTest {

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
