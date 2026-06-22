package org.example.sql;

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasicSqlQueryTest extends AbstractSqlTest {

    /**
     * Количество сотрудников
     */
    @Test
    void testCountEmployees() throws Exception {
        String sql = "SELECT COUNT(*) FROM employees";
        try (
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql)
        ) {
            rs.next();
            int count = rs.getInt(1);

            assertEquals(16, count);
        }
    }

    /**
     * Вторая максимальная зарплата
     */
    @Test
    void testSecondMaxSalary() throws Exception {
        String sql = "SELECT DISTINCT salary FROM employees ORDER BY salary DESC LIMIT 1 OFFSET 1";
        try (
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql)
        ) {
            rs.next();
            double salary = rs.getDouble(1);

            assertEquals(120000.00, salary, 0.001);
        }
    }

    /**
     * Сотрудники без отдела
     */
    @Test
    void testDepartmentIsNull() throws Exception {
        String sql = "SELECT COUNT(*) FROM employees WHERE department_id IS NULL";
        try (
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql)
        ) {
            rs.next();
            int count = rs.getInt(1);

            assertEquals(1, count);
        }
    }

    /**
     * Сотрудники с зарплатой больше 100_000
     */
    @Test
    void testSalaryBiggerThan() throws Exception {
        String sql = "SELECT id, salary FROM employees WHERE salary > 130000 ORDER BY salary DESC, id ASC";
        try (
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql)
        ) {
            rs.next();
            assertEquals(2, rs.getLong(1));
            assertEquals(135000L, rs.getLong(2));

            rs.next();
            assertEquals(4, rs.getLong(1));
            assertEquals(135000L, rs.getLong(2));

            rs.next();
            assertEquals(16, rs.getLong(1));
            assertEquals(135000L, rs.getLong(2));
        }
    }
}
