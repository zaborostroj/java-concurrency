CREATE TABLE employees (
    id INT PRIMARY KEY,
    name VARCHAR(50),
    department VARCHAR(50),
    salary INT
);

CREATE TABLE orders (
    id INT PRIMARY KEY,
    employee_id INT,
    order_date DATE,
    amount DECIMAL(10,2),
    FOREIGN KEY (employee_id) REFERENCES employees(id)
);
