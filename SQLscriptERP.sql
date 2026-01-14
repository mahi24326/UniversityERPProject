/* creating new databases */
CREATE DATABASE IF NOT EXISTS auth_db;
CREATE DATABASE IF NOT EXISTS erp_db;



/* dropping*/
USE erp_db;

DROP TABLE IF EXISTS assessment_scores;
DROP TABLE IF EXISTS final_grades;
DROP TABLE IF EXISTS grades;
DROP TABLE IF EXISTS enrollments;
DROP TABLE IF EXISTS sections;
DROP TABLE IF EXISTS instructors;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS assessment_components;
DROP TABLE IF EXISTS settings;
DROP TABLE IF EXISTS courses;



USE auth_db;

DROP TABLE IF EXISTS password_history;
DROP TABLE IF EXISTS users;



/* Auth DB structure */

CREATE TABLE users (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  password_hash VARCHAR(200) NOT NULL,
  role ENUM('admin','instructor','student') NOT NULL,
  status ENUM('active','inactive') DEFAULT 'active',
  last_login DATETIME NULL
);

CREATE TABLE password_history (
  history_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  old_password_hash VARCHAR(200),
  changed_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(user_id)
);

/* Seeding users */
INSERT INTO users (username, password_hash, role, status)
VALUES 
('admin1', '$2a$12$mB0VlAEoEo7ynLUvMTX1tO7fB5spJxJ7NdP9x/1e2kUwj7XrGArFi', 'admin', 'active'),
('inst1',  '$2a$12$yv.DF242wAH.XgBYHBEZt.I8o9dyzhdq90E4v91YoMNcTXtz/LBCu', 'instructor', 'active'),
('stu1',   '$2a$12$22kNBWaJ6ow8imrOIG341.AZEMY/3iW5.rKyxuWGsu5cioDt9PsjO', 'student', 'active'),
('stu2',   '$2a$12$pgd2aO.uDUAC9N3n4cT/pOHi2iQ3sZeQyOOVeoYGW0KsqREfJOYsK', 'student', 'active');



/* ERP DB structure */
USE erp_db;

/* Students */
CREATE TABLE students (
  student_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT UNIQUE NOT NULL,
  roll_no VARCHAR(20) UNIQUE NOT NULL,
  program VARCHAR(50),
  year INT,
  FOREIGN KEY (user_id) REFERENCES auth_db.users(user_id)
);

/* Instructors */
CREATE TABLE instructors (
  instructor_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT UNIQUE NOT NULL,
  department VARCHAR(100),
  FOREIGN KEY (user_id) REFERENCES auth_db.users(user_id)
);

/* Courses */
CREATE TABLE courses (
  course_id INT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(20) UNIQUE NOT NULL,
  title VARCHAR(100),
  credits INT NOT NULL
);

/* Sections */
CREATE TABLE sections (
  section_id INT AUTO_INCREMENT PRIMARY KEY,
  course_id INT NOT NULL,
  instructor_id INT NOT NULL,
  day VARCHAR(20),
  time VARCHAR(20),
  room VARCHAR(50),
  capacity INT,
  semester VARCHAR(10),
  year INT,
  semester_start DATE,   -- <<< REQUIRED FOR YOUR DAO
  FOREIGN KEY(course_id) REFERENCES courses(course_id),
  FOREIGN KEY(instructor_id) REFERENCES instructors(instructor_id)
);


/* Enrollments */
CREATE TABLE enrollments (
  enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
  student_id INT NOT NULL,
  section_id INT NOT NULL,
  status ENUM('enrolled','dropped','completed') DEFAULT 'enrolled',
  UNIQUE(student_id, section_id),
  FOREIGN KEY(student_id) REFERENCES students(student_id),
  FOREIGN KEY(section_id) REFERENCES sections(section_id)
);

/* grades table */
CREATE TABLE grades (
  grade_id INT AUTO_INCREMENT PRIMARY KEY,
  enrollment_id INT NOT NULL,
  component VARCHAR(50),
  score DECIMAL(5,2),
  final_grade VARCHAR(2),
  FOREIGN KEY(enrollment_id) REFERENCES enrollments(enrollment_id)
);



/* assessment components */
CREATE TABLE assessment_components (
    component_id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    name VARCHAR(50) NOT NULL,
    weight INT NOT NULL,
    max_marks INT NOT NULL,       -- <<< ADDED COLUMN
    FOREIGN KEY(course_id) REFERENCES courses(course_id)
);



/* assessment scores */
CREATE TABLE assessment_scores (
    score_id INT AUTO_INCREMENT PRIMARY KEY,
    enrollment_id INT NOT NULL,
    component_id INT NOT NULL,
    score DOUBLE,
    UNIQUE KEY unique_score (enrollment_id, component_id),
    FOREIGN KEY(enrollment_id) REFERENCES enrollments(enrollment_id) ON DELETE CASCADE,
    FOREIGN KEY(component_id) REFERENCES assessment_components(component_id) ON DELETE CASCADE
);



/* Final grades */
CREATE TABLE final_grades (
    final_id INT AUTO_INCREMENT PRIMARY KEY,
    enrollment_id INT NOT NULL UNIQUE,
    final_score DOUBLE NOT NULL,
    letter_grade VARCHAR(3),
    FOREIGN KEY(enrollment_id) REFERENCES enrollments(enrollment_id)
);



/* Settings */
CREATE TABLE settings (
  setting_key VARCHAR(50) PRIMARY KEY,
  value VARCHAR(10) NOT NULL CHECK (value IN ('true', 'false'))
);

/* Initial Value */
INSERT INTO settings VALUES ('maintenance_mode', 'false');



/* Initial ERP data */

/* Students */
INSERT INTO students (user_id, roll_no, program, year)
VALUES
((SELECT user_id FROM auth_db.users WHERE username = 'stu1'), 'STU001', 'B.Tech CSE', 1),
((SELECT user_id FROM auth_db.users WHERE username = 'stu2'), 'STU002', 'B.Tech CSE', 1);

/* Instructor */
INSERT INTO instructors (user_id, department)
VALUES
((SELECT user_id FROM auth_db.users WHERE username = 'inst1'), 'Computer Science');

/* Courses */
INSERT INTO courses (code, title, credits)
VALUES
('MATH101', 'Calculus', 4),
('CS202',   'Operating Systems', 3);

/* Sections */
INSERT INTO sections (course_id, instructor_id, day, time, room, capacity, semester, year)
VALUES
((SELECT course_id FROM courses WHERE code='MATH101'),
 (SELECT instructor_id FROM instructors LIMIT 1),
 'Mon', '10:00 AM', 'Room 101', 60, 'Winter', 2025),

((SELECT course_id FROM courses WHERE code='CS202'),
 (SELECT instructor_id FROM instructors LIMIT 1),
 'Wed', '2:00 PM', 'Room 202', 50, 'Monsoon', 2025);

/* Enrollments */
INSERT INTO enrollments (student_id, section_id, status)
VALUES
((SELECT student_id FROM students WHERE roll_no='STU001'),
 (SELECT section_id FROM sections LIMIT 1), 'enrolled'),

((SELECT student_id FROM students WHERE roll_no='STU002'),
 (SELECT section_id FROM sections LIMIT 1), 'enrolled');