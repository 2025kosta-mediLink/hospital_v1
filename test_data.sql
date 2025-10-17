-- 처방전 테스트를 위한 샘플 데이터 생성

-- 1. 기본 테이블 생성 (필요한 경우)
CREATE DATABASE IF NOT EXISTS hospital_db;
USE hospital_db;

-- 2. 기본 데이터 삽입
INSERT IGNORE INTO department (department_id, name) VALUES 
(1, '내과'), (2, '외과'), (3, '소아과'), (4, '이비인후과'), (5, '정형외과');

INSERT IGNORE INTO doctor (doctor_id, name, department_id, profile_image_url) VALUES 
(1, '김의사', 1, '/static/images/default-doctor.png'),
(2, '이의사', 2, '/static/images/default-doctor.png'),
(3, '박의사', 3, '/static/images/default-doctor.png');

INSERT IGNORE INTO member (member_id, name, login_id, password, phone, created_at) VALUES 
(1, '테스트환자', 'test@test.com', 'password123', '010-1234-5678', NOW());

-- 3. 처방전 테스트 데이터
INSERT IGNORE INTO reception (reception_id, member_id, doctor_id, status, created_at) VALUES 
(1, 1, 1, 'COMPLETED', NOW()),
(2, 1, 2, 'COMPLETED', NOW()),
(3, 1, 3, 'COMPLETED', NOW());

INSERT IGNORE INTO prescription (prescription_id, reception_id, doctor_id, issued_at, content, created_at, updated_at) VALUES 
(1, 1, 1, '2025-10-16 10:30:00', '감기약 처방', NOW(), NOW()),
(2, 2, 2, '2025-10-15 14:20:00', '소화제 처방', NOW(), NOW()),
(3, 3, 3, '2025-10-14 09:15:00', '진통제 처방', NOW(), NOW());

INSERT IGNORE INTO pharmacy_prescription (pharmacy_prescription_id, pharmacy_id, prescription_id, expected_finish_time, status, assigned_pharmacist, created_at, updated_at) VALUES 
(1, NULL, 1, '2025-10-16 11:00:00', 'COMPLETED', '슬닥 약국', NOW(), NOW()),
(2, NULL, 2, '2025-10-15 15:00:00', 'COMPLETED', '건강 약국', NOW(), NOW()),
(3, NULL, 3, '2025-10-14 10:00:00', 'START', '행복 약국', NOW(), NOW());

INSERT IGNORE INTO pickup_history (pickup_history_id, pharmacy_prescription_id, pickup_at, verified_by, created_at) VALUES 
(1, 1, '2025-10-16 11:30:00', '김환자', NOW()),
(2, 2, '2025-10-15 15:30:00', '김환자', NOW());

