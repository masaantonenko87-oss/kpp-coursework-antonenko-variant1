DROP DATABASE IF EXISTS it_support;
CREATE DATABASE it_support CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE it_support;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE equipment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    serial_number VARCHAR(50) NOT NULL,
    criticality INT NOT NULL
);

CREATE TABLE requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    equipment_id INT NOT NULL,
    category_id INT NOT NULL,
    urgency INT NOT NULL,
    impact INT NOT NULL,
    priority_index DOUBLE NOT NULL,
    status VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (equipment_id) REFERENCES equipment(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

INSERT INTO users (name, email, role) VALUES 
('Марія Антоненко', 'maria@example.com', 'Admin'),
('Олександр Петренко', 'oleksandr@example.com', 'User'),
('Аліна Коваль', 'alina@example.com', 'User');

INSERT INTO categories (name) VALUES 
('Апаратні проблеми'),
('Програмне забезпечення'),
('Мережа та інтернет');

INSERT INTO equipment (name, serial_number, criticality) VALUES 
('ПК Dell OptiPlex', 'DL-98234-X', 80),
('Принтер HP LaserJet', 'HP-44512-B', 50),
('Мережевий комутатор Cisco', 'CS-11029-C', 95);

INSERT INTO requests (user_id, equipment_id, category_id, urgency, impact, priority_index, status) VALUES 
(2, 1, 2, 70, 80, 74.5, 'NEW'),
(3, 2, 1, 40, 50, 44.5, 'ASSIGNED'),
(2, 3, 3, 90, 85, 88.2, 'IN_PROGRESS');