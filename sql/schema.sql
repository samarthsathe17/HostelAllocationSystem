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
    phone VARCHAR(15),
    cgpa DECIMAL(4,2) DEFAULT 0.0,
    attendance DECIMAL(5,2) DEFAULT 0.0,
    year_of_study INT DEFAULT 1,
    distance_from_campus INT DEFAULT 0,
    medical_needs VARCHAR(255) DEFAULT NULL,
    financial_need BOOLEAN DEFAULT FALSE,
    conduct_status ENUM('GOOD', 'WARNING', 'SUSPENDED') DEFAULT 'GOOD',
    preferred_room_type ENUM('SINGLE', 'SHARED') DEFAULT 'SHARED'
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

-- 6. allocation_settings
CREATE TABLE IF NOT EXISTS allocation_settings (
    id INT PRIMARY KEY DEFAULT 1,
    min_cgpa DECIMAL(4,2) DEFAULT 6.0,
    min_attendance DECIMAL(5,2) DEFAULT 75.0,
    weight_cgpa INT DEFAULT 30,
    weight_attendance INT DEFAULT 20,
    weight_distance INT DEFAULT 30,
    weight_financial INT DEFAULT 10,
    weight_year INT DEFAULT 10
);

-- 7. allocation_requests
CREATE TABLE IF NOT EXISTS allocation_requests (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    priority_score DECIMAL(6,2) DEFAULT 0.0,
    is_eligible BOOLEAN DEFAULT FALSE,
    eligibility_details TEXT,
    recommended_room VARCHAR(10),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (recommended_room) REFERENCES rooms(room_no) ON DELETE SET NULL
);

-- Seed Data

-- Clear existing data (if re-running script)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE allocations;
TRUNCATE TABLE mess_subscriptions;
TRUNCATE TABLE dues_and_billing;
TRUNCATE TABLE allocation_requests;
TRUNCATE TABLE allocation_settings;
TRUNCATE TABLE students;
TRUNCATE TABLE rooms;
SET FOREIGN_KEY_CHECKS = 1;

-- Seed Rooms (15 rooms across 3 floors)
INSERT INTO rooms (room_no, type, capacity, current_occupancy, base_price) VALUES
('101', 'SINGLE', 1, 0, 2000.00),
('102', 'SINGLE', 1, 0, 2000.00),
('103', 'SINGLE', 1, 0, 1500.00),
('104', 'SINGLE', 1, 0, 1500.00),
('105', 'SINGLE', 1, 0, 1500.00),
('201', 'SHARED', 2, 0, 1200.00),
('202', 'SHARED', 2, 0, 1200.00),
('203', 'SHARED', 2, 0, 900.00),
('204', 'SHARED', 2, 0, 900.00),
('205', 'SHARED', 2, 0, 900.00),
('301', 'SHARED', 3, 0, 1000.00),
('302', 'SHARED', 3, 0, 1000.00),
('303', 'SHARED', 3, 0, 750.00),
('304', 'SHARED', 3, 0, 750.00),
('305', 'SHARED', 3, 0, 750.00);

-- Seed Students
INSERT INTO students (roll_no, name, email, phone, cgpa, attendance, year_of_study, distance_from_campus, medical_needs, financial_need, conduct_status, preferred_room_type) VALUES
('CS2024001', 'Alice Smith', 'alice@example.com', '1234567890', 9.2, 95.0, 3, 500, NULL, TRUE, 'GOOD', 'SINGLE'),
('EE2024012', 'Bob Johnson', 'bob@example.com', '9876543210', 7.5, 80.0, 2, 50, NULL, FALSE, 'GOOD', 'SHARED'),
('ME2024033', 'Charlie Brown', 'charlie@example.com', '5551234567', 5.5, 60.0, 1, 10, NULL, FALSE, 'WARNING', 'SHARED');

-- Seed Settings
INSERT IGNORE INTO allocation_settings (id, min_cgpa, min_attendance, weight_cgpa, weight_attendance, weight_distance, weight_financial, weight_year) VALUES
(1, 6.0, 75.0, 30, 20, 30, 10, 10);

-- Initial Dues (seed a pending due to test billing feature)
INSERT INTO dues_and_billing (student_id, amount, due_date, payment_status) VALUES
(1, 500.00, '2026-10-15', 'PENDING'),
(2, 200.00, '2026-10-10', 'PENDING');
