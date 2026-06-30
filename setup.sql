-- ============================================================
--  Sufiyan Health Clinic — MySQL Setup Script
--  Run this ONCE in MySQL before launching the application.
--
--  Steps:
--    1. Open MySQL Workbench or terminal
--    2. Run:  mysql -u root -p < setup.sql
-- ============================================================

-- Create database
CREATE DATABASE IF NOT EXISTS sufiyan_health_clinic
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE sufiyan_health_clinic;

-- The application creates all tables automatically on first run.
-- This script just creates the database and a dedicated user.

-- (Optional) Create a dedicated DB user instead of using root:
-- CREATE USER IF NOT EXISTS 'shc_user'@'localhost' IDENTIFIED BY 'YourStrongPassword!';
-- GRANT ALL PRIVILEGES ON sufiyan_health_clinic.* TO 'shc_user'@'localhost';
-- FLUSH PRIVILEGES;

-- If you used the optional user above, update db.properties:
--   db.user=shc_user
--   db.password=YourStrongPassword!

SELECT 'Database created successfully. You can now launch the application.' AS status;
