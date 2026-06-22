CREATE TABLE clients (
    id INT PRIMARY KEY,
    name VARCHAR(50),
    department VARCHAR(50),
    salary INT
);

CREATE TABLE client_orders (
    id INT PRIMARY KEY,
    client_id INT,
    order_date DATE,
    amount DECIMAL(10,2),
    FOREIGN KEY (client_id) REFERENCES clients(id)
);
