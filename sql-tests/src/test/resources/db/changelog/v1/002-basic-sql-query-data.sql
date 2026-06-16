INSERT INTO departments (name, location) VALUES
    ('IT', 'Moscow'),
    ('HR', 'St. Petersburg'),
    ('Sales', 'Novosibirsk'),
    ('Administration', 'Moscow');

-- changeset author:6
-- Вставка сотрудников
INSERT INTO employees (first_name, last_name, email, salary, department_id, hire_date) VALUES
    -- IT отдел (department_id = 1)
    ('Ivan', 'Petrov', 'ivan.petrov@example.com', 120000, 1, '2020-01-15'),
    ('Maria', 'Ivanova', 'maria.ivanova@example.com', 135000, 1, '2019-06-10'),
    ('Alexey', 'Sidorov', 'alexey.sidorov@example.com', 95000, 1, '2021-03-20'),
    ('Elena', 'Kuznetsova', 'elena.kuznetsova@example.com', 135000, 1, '2018-11-01'), -- дубликат зарплаты 135000
    ('Dmitry', 'Volkov', 'dmitry.volkov@example.com', 110000, 1, '2022-08-30'),

    -- HR отдел (department_id = 2)
    ('Olga', 'Smirnova', 'olga.smirnova@example.com', 85000, 2, '2019-05-25'),
    ('Pavel', 'Fedorov', 'pavel.fedorov@example.com', 78000, 2, '2020-02-10'),
    ('Natalia', 'Mikhailova', 'natalia.mikhailova@example.com', 92000, 2, '2017-12-03'),

    -- Sales отдел (department_id = 3)
    ('Sergey', 'Novikov', 'sergey.novikov@example.com', 105000, 3, '2018-07-19'),
    ('Anna', 'Zaitseva', 'anna.zaitseva@example.com', 98000, 3, '2021-01-14'),
    ('Andrey', 'Morozov', 'andrey.morozov@example.com', 112000, 3, '2019-09-27'),
    ('Ekaterina', 'Vasilyeva', 'ekaterina.vasilyeva@example.com', 98000, 3, '2022-11-05'), -- дубликат 98000

    -- Administration отдел (department_id = 4)
    ('Vladimir', 'Popov', 'vladimir.popov@example.com', 70000, 4, '2020-04-08'),
    ('Svetlana', 'Sokolova', 'svetlana.sokolova@example.com', 68000, 4, '2021-12-17'),

    -- Сотрудник без отдела (department_id = NULL)
    ('Oleg', 'Borisov', 'oleg.borisov@example.com', 60000, NULL, '2023-03-22');

INSERT INTO employees (first_name, last_name, email, salary, department_id, hire_date) VALUES
    ('Maxim', 'Grigoriev', 'maxim.grigoriev@example.com', 135000, 1, '2023-01-10');
