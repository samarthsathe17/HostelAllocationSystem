-- Add new columns to students
ALTER TABLE students 
ADD COLUMN cgpa DECIMAL(4,2) DEFAULT 0.0,
ADD COLUMN attendance DECIMAL(5,2) DEFAULT 0.0,
ADD COLUMN year_of_study INT DEFAULT 1,
ADD COLUMN distance_from_campus INT DEFAULT 0,
ADD COLUMN medical_needs VARCHAR(255) DEFAULT NULL,
ADD COLUMN financial_need BOOLEAN DEFAULT FALSE,
ADD COLUMN conduct_status ENUM('GOOD', 'WARNING', 'SUSPENDED') DEFAULT 'GOOD',
ADD COLUMN preferred_room_type ENUM('SINGLE', 'SHARED') DEFAULT 'SHARED';

-- Create allocation_settings table
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

INSERT IGNORE INTO allocation_settings (id) VALUES (1);

-- Create allocation_requests table
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
