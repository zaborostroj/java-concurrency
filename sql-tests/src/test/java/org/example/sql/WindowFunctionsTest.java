package org.example.sql;

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WindowFunctionsTest extends AbstractSqlTest {

    /**
     * Для каждого клиента вывести его порядковый номер по убыванию зарплаты
     */
    @Test
    void showClientNumbersBySalary() throws Exception {
        String sql = """
            SELECT
                id,
                name,
                department,
                salary,
                ROW_NUMBER() OVER (
                    ORDER BY salary DESC
                ) as num
            FROM clients
            ORDER BY num
        """;
        try (
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql)
        ) {
            rs.next(); // 6, John
            assertEquals(1, rs.getInt(5)); // num
            assertEquals(6, rs.getInt(1)); // id
            rs.next(); // 2, Petr
            assertEquals(2, rs.getInt(5));
            assertEquals(2, rs.getInt(1));
            rs.next(); // 3, Anna
            assertEquals(3, rs.getInt(5));
            assertEquals(3, rs.getInt(1));
            rs.next(); // 7, Kate
            assertEquals(4, rs.getInt(5));
            assertEquals(7, rs.getInt(1));
            rs.next(); // 5, Maria
            assertEquals(5, rs.getInt(5));
            assertEquals(5, rs.getInt(1));
            rs.next(); // 1, Ivan
            assertEquals(6, rs.getInt(5));
            assertEquals(1, rs.getInt(1));
            rs.next(); // 4, Olga
            assertEquals(7, rs.getInt(5));
            assertEquals(4, rs.getInt(1));
        }
    }

    /**
     * Для каждого клиента вывести его порядковый номер по убыванию зарплаты с разбивкой по отделам
     */
    @Test
    void showClientNumbersBySalaryAndDepartment() throws Exception {
        String sql = """
            SELECT
                id,
                name,
                department,
                salary,
                ROW_NUMBER() OVER (
                    PARTITION BY department
                    ORDER BY salary DESC
                ) as num_in_department
            FROM clients
            ORDER BY department, num_in_department
        """;
        try (
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql)
        ) {
            rs.next(); // 5, Maria
            assertEquals(1, rs.getInt(5)); // num_in_department
            assertEquals(5, rs.getInt(1)); // id
            rs.next(); // 4, Olga
            assertEquals(2, rs.getInt(5));
            assertEquals(4, rs.getInt(1));
            rs.next(); // 3, Anna
            assertEquals(1, rs.getInt(5));
            assertEquals(3, rs.getInt(1));
            rs.next(); // 2, Petr
            assertEquals(2, rs.getInt(5));
            assertEquals(2, rs.getInt(1));
            rs.next(); // 1, Ivan
            assertEquals(3, rs.getInt(5));
            assertEquals(1, rs.getInt(1));
            rs.next(); // 6, John
            assertEquals(1, rs.getInt(5));
            assertEquals(6, rs.getInt(1));
            rs.next(); // 7, Kate
            assertEquals(2, rs.getInt(5));
            assertEquals(7, rs.getInt(1));
        }
    }

    /**
     * Для каждого клиента вывести его порядковый номер по убыванию зарплаты с разбивкой по отделам
     */
    @Test
    void showClientRank() throws Exception {
        String sql = """
            SELECT
                name,
                salary,
                RANK() OVER (ORDER BY salary DESC) as rank,
                DENSE_RANK() OVER (ORDER BY salary DESC) as dense_rank
            FROM clients
        """;
        try (
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql)
        ) {
            rs.next(); // 6, John
            assertEquals(1, rs.getInt(3)); // rank
            assertEquals(1, rs.getInt(4)); // dense_rank
            rs.next(); // 2, Petr
            assertEquals(2, rs.getInt(3));
            assertEquals(2, rs.getInt(4));
            rs.next(); // 3, Anna
            assertEquals(2, rs.getInt(3));
            assertEquals(2, rs.getInt(4));
            rs.next(); // 7, Kate
            assertEquals(4, rs.getInt(3));
            assertEquals(3, rs.getInt(4));
            rs.next(); // 5, Maria
            assertEquals(5, rs.getInt(3));
            assertEquals(4, rs.getInt(4));
            rs.next(); // 1, Ivan
            assertEquals(6, rs.getInt(3));
            assertEquals(5, rs.getInt(4));
            rs.next(); // 4, Olga
            assertEquals(7, rs.getInt(3));
            assertEquals(6, rs.getInt(4));
        }
    }

    /**
     * Для каждого клиента вывести: дату заказа, сумму заказа, накопительную сумму заказов сотрудника
     */
    @Test
    void testTotalOrdersSumByClient() throws Exception {
        String sql = """
            SELECT
                c.id AS client_id,
                co.order_date,
                co.amount,
                SUM(co.amount) OVER (PARTITION BY c.id ORDER BY co.order_date)
            FROM clients c
            LEFT JOIN client_orders co ON (c.id = co.client_id)
        """;
        try (
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql)
        ) {
            rs.next();
            rs.next();
            rs.next(); // 1, Ivan, 600
            assertEquals(1, rs.getInt(1)); // id
            assertEquals(600, rs.getInt(4)); // sum
            rs.next();
            rs.next(); // 2, Petr, 500
            assertEquals(2, rs.getInt(1));
            assertEquals(500, rs.getInt(4));
            rs.next(); // 3, Anna, 250
            assertEquals(3, rs.getInt(1));
            assertEquals(250, rs.getInt(4));
            rs.next(); // 4, Olga, 100
            assertEquals(4, rs.getInt(1));
            assertEquals(100, rs.getInt(4));
            rs.next();
            rs.next(); // 5, Maria, 700
            assertEquals(5, rs.getInt(1));
            assertEquals(700, rs.getInt(4));
            rs.next();
            rs.next(); // 6, John, 1700
            assertEquals(6, rs.getInt(1));
            assertEquals(1700, rs.getInt(4));
            rs.next(); // 7, Kate, 400
            assertEquals(7, rs.getInt(1));
            assertEquals(400, rs.getInt(4));
        }
    }

    /**
     * Для каждого клиента вывести:
     * имя
     * отдел
     * сумму заказа
     * средний заказ отдела
     * Затем оставить только сотрудников с зарплатой выше средней по отделу.
     */
    @Test
    void testClientsOverAvgAmount() throws Exception {
        String sql = """
            SELECT * FROM (
                SELECT
                    c.id AS client_id,
                    c.department,
                    co.amount as co_amount,
                    AVG(co.amount) OVER (PARTITION BY c.department) as avg
                FROM clients c
                LEFT JOIN client_orders co ON (c.id = co.client_id)
            ) avg_amount
            WHERE co_amount > avg
        """;
        try (
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql)
        ) {
            rs.next(); // 5, Maria
            assertEquals(5, rs.getInt(1)); // id
            assertEquals("HR", rs.getString(2)); // department
            rs.next(); // 2, Petr
            assertEquals(2, rs.getInt(1)); // id
            assertEquals(225, rs.getInt(4)); // avg amount
            rs.next(); // 3, Anna
            assertEquals(3, rs.getInt(1));
            assertEquals(225, rs.getInt(4));
            rs.next(); // 1, Ivan
            assertEquals(1, rs.getInt(1));
            assertEquals(225, rs.getInt(4));
            rs.next(); // 6, John
            assertEquals(6, rs.getInt(1));
            assertEquals(700, rs.getInt(4));
        }
    }

    /**
     * Найти клиента с самым дорогим заказом в каждом отделе.
     */
    @Test
    void testMostExpensiveAmountPerDepartment() throws Exception {
        String sql = """
            SELECT * FROM (
                SELECT
                    c.id,
                    c.name,
                    c.department,
                    co.amount,
                    ROW_NUMBER() OVER (PARTITION BY c.department ORDER BY co.amount DESC) as row_number
                FROM clients c
                LEFT JOIN client_orders co ON (c.id = co.client_id)
            ) clients_rating
            WHERE row_number = 1
            ORDER BY department
        """;
        try (
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql)
        ) {
            rs.next(); // 5, Maria
            assertEquals(5, rs.getInt(1)); // id
            assertEquals(500, rs.getInt(4)); // amount
            rs.next(); // 2, Petr
            assertEquals(2, rs.getInt(1));
            assertEquals(350, rs.getInt(4));
            rs.next(); // 6, John
            assertEquals(6, rs.getInt(1));
            assertEquals(1000, rs.getInt(4));
        }
    }
}