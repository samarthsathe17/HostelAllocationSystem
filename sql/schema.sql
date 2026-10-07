-- schema.sql
-- Run this script to set up the database and seed data.

CREATE DATABASE IF NOT EXISTS hostel_db;
USE hostel_db;

-- 1. students
CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    roll_no VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(15)
);

-- 2. rooms
CREATE TABLE IF NOT EXISTS rooms (
    room_no VARCHAR(10) PRIMARY KEY,
    type ENUM('SINGLE', 'SHARED') NOT NULL,
    capacity INT NOT NULL,
    current_occupancy INT DEFAULT 0,
    base_price DECIMAL(10,2) NOT NULL
);

-- 3. allocations
CREATE TABLE IF NOT EXISTS allocations (
    allocation_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    room_no VARCHAR(10) NOT NULL,
    allocation_date DATE NOT NULL,
    status ENUM('ACTIVE', 'TERMINATED') DEFAULT 'ACTIVE',
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (room_no) REFERENCES rooms(room_no) ON DELETE RESTRICT
);

-- 4. mess_subscriptions
CREATE TABLE IF NOT EXISTS mess_subscriptions (
    sub_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    plan_type ENUM('STANDARD', 'PREMIUM', 'VEG_ONLY') NOT NULL,
    monthly_fee DECIMAL(8,2) NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

-- 5. dues_and_billing
CREATE TABLE IF NOT EXISTS dues_and_billing (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    due_date DATE NOT NULL,
    payment_status ENUM('PAID', 'PENDING') DEFAULT 'PENDING',
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

-- Seed Data

-- Clear existing data (if re-running script)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE allocations;
TRUNCATE TABLE mess_subscriptions;
TRUNCATE TABLE dues_and_billing;
TRUNCATE TABLE students;
TRUNCATE TABLE rooms;
SET FOREIGN_KEY_CHECKS = 1;

-- Seed Rooms (8 rooms across 2 floors)
INSERT INTO rooms (room_no, type, capacity, current_occupancy, base_price) VALUES
('101', 'SINGLE', 1, 0, 1500.00),
('102', 'SINGLE', 1, 0, 1500.00),
('103', 'SHARED', 2, 0, 1000.00),
('104', 'SHARED', 3, 0, 800.00),
('201', 'SINGLE', 1, 0, 1500.00),
('202', 'SINGLE', 1, 0, 1500.00),
('203', 'SHARED', 2, 0, 1000.00),
('204', 'SHARED', 3, 0, 800.00);

-- Seed Students
INSERT INTO students (roll_no, name, email, phone) VALUES
('CS2024001', 'Alice Smith', 'alice@example.com', '1234567890'),
('EE2024012', 'Bob Johnson', 'bob@example.com', '9876543210'),
('ME2024033', 'Charlie Brown', 'charlie@example.com', '5551234567');

-- Initial Dues (seed a pending due to test billing feature)
INSERT INTO dues_and_billing (student_id, amount, due_date, payment_status) VALUES
(1, 500.00, '2026-10-15', 'PENDING'),
(2, 200.00, '2026-10-10', 'PENDING');
