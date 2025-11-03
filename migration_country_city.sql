-- Country와 City 테이블 생성 및 데이터 마이그레이션 스크립트

-- 1. 새 테이블 생성
CREATE TABLE countries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    english_name VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT NULL
);

CREATE TABLE cities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    english_name VARCHAR(50) NOT NULL,
    country_id BIGINT NOT NULL,
    is_favorite BOOLEAN NOT NULL DEFAULT FALSE,
    display_order INT DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT NULL,
    FOREIGN KEY (country_id) REFERENCES countries(id)
);

-- 2. 국가 데이터 삽입
INSERT INTO countries (code, name, english_name) VALUES
('USA', '미국', 'United States'),
('KOREA', '한국', 'South Korea'),
('CHINA', '중국', 'China'),
('JAPAN', '일본', 'Japan'),
('UK', '영국', 'United Kingdom'),
('SINGAPORE', '싱가포르', 'Singapore'),
('AUSTRALIA', '호주', 'Australia'),
('CANADA', '캐나다', 'Canada'),
('GERMANY', '독일', 'Germany'),
('FRANCE', '프랑스', 'France'),
('ITALY', '이탈리아', 'Italy'),
('NETHERLANDS', '네덜란드', 'Netherlands'),
('SPAIN', '스페인', 'Spain'),
('NORWAY', '노르웨이', 'Norway'),
('SWITZERLAND', '스위스', 'Switzerland'),
('UAE', '아랍에미리트', 'United Arab Emirates');

-- 3. 도시 데이터 삽입

-- 즐겨찾는 도시 (is_favorite = true)
INSERT INTO cities (code, name, english_name, country_id, is_favorite, display_order) VALUES
('SEOUL', '서울', 'Seoul', (SELECT id FROM countries WHERE code = 'KOREA'), true, 1),
('TOKYO', '도쿄', 'Tokyo', (SELECT id FROM countries WHERE code = 'JAPAN'), true, 2),
('SHANGHAI', '상하이', 'Shanghai', (SELECT id FROM countries WHERE code = 'CHINA'), true, 3),
('LOS_ANGELES', '로스앤젤레스', 'Los Angeles', (SELECT id FROM countries WHERE code = 'USA'), true, 4),
('LONDON', '런던', 'London', (SELECT id FROM countries WHERE code = 'UK'), true, 5);

-- 미국 도시들
INSERT INTO cities (code, name, english_name, country_id, is_favorite, display_order) VALUES
('NEW_YORK', '뉴욕', 'New York', (SELECT id FROM countries WHERE code = 'USA'), false, 1),
('BOSTON', '보스턴', 'Boston', (SELECT id FROM countries WHERE code = 'USA'), false, 3),
('SAN_FRANCISCO', '샌프란시스코', 'San Francisco', (SELECT id FROM countries WHERE code = 'USA'), false, 4),
('CHICAGO', '시카고', 'Chicago', (SELECT id FROM countries WHERE code = 'USA'), false, 5),
('WASHINGTON', '워싱턴', 'Washington', (SELECT id FROM countries WHERE code = 'USA'), false, 6),
('AUSTIN', '어스틴', 'Austin', (SELECT id FROM countries WHERE code = 'USA'), false, 7);

-- 한국 도시들 (서울은 이미 즐겨찾는 도시로 삽입됨)
INSERT INTO cities (code, name, english_name, country_id, is_favorite, display_order) VALUES
('BUSAN', '부산', 'Busan', (SELECT id FROM countries WHERE code = 'KOREA'), false, 2),
('JEJU', '제주', 'Jeju', (SELECT id FROM countries WHERE code = 'KOREA'), false, 3),
('INCHEON', '인천', 'Incheon', (SELECT id FROM countries WHERE code = 'KOREA'), false, 4),
('GANGNEUNG', '강릉', 'Gangneung', (SELECT id FROM countries WHERE code = 'KOREA'), false, 5),
('GWANGJU', '광주', 'Gwangju', (SELECT id FROM countries WHERE code = 'KOREA'), false, 6);

-- 중국 도시들 (상하이는 이미 즐겨찾는 도시로 삽입됨)
INSERT INTO cities (code, name, english_name, country_id, is_favorite, display_order) VALUES
('BEIJING', '베이징', 'Beijing', (SELECT id FROM countries WHERE code = 'CHINA'), false, 2),
('HANGZHOU', '항저우', 'Hangzhou', (SELECT id FROM countries WHERE code = 'CHINA'), false, 3),
('HONGKONG', '홍콩', 'Hong Kong', (SELECT id FROM countries WHERE code = 'CHINA'), false, 4);

-- 싱가포르
INSERT INTO cities (code, name, english_name, country_id, is_favorite, display_order) VALUES
('SINGAPORE_CITY', '싱가포르', 'Singapore', (SELECT id FROM countries WHERE code = 'SINGAPORE'), false, 1);

-- 일본 도시들 (도쿄는 이미 즐겨찾는 도시로 삽입됨)
INSERT INTO cities (code, name, english_name, country_id, is_favorite, display_order) VALUES
('OSAKA', '오사카', 'Osaka', (SELECT id FROM countries WHERE code = 'JAPAN'), false, 2),
('KYOTO', '교토', 'Kyoto', (SELECT id FROM countries WHERE code = 'JAPAN'), false, 3);

-- 영국 도시들 (런던은 이미 즐겨찾는 도시로 삽입됨)
INSERT INTO cities (code, name, english_name, country_id, is_favorite, display_order) VALUES
('MANCHESTER', '멘체스터', 'Manchester', (SELECT id FROM countries WHERE code = 'UK'), false, 2),
('OXFORD', '옥스퍼드', 'Oxford', (SELECT id FROM countries WHERE code = 'UK'), false, 3),
('CAMBRIDGE', '캠브리지', 'Cambridge', (SELECT id FROM countries WHERE code = 'UK'), false, 4);

-- 호주 도시들
INSERT INTO cities (code, name, english_name, country_id, is_favorite, display_order) VALUES
('SYDNEY', '시드니', 'Sydney', (SELECT id FROM countries WHERE code = 'AUSTRALIA'), false, 1),
('MELBOURNE', '멜버른', 'Melbourne', (SELECT id FROM countries WHERE code = 'AUSTRALIA'), false, 2),
('BRISBANE', '브리즈번', 'Brisbane', (SELECT id FROM countries WHERE code = 'AUSTRALIA'), false, 3),
('PERTH', '펄스', 'Perth', (SELECT id FROM countries WHERE code = 'AUSTRALIA'), false, 4);

-- 캐나다 도시들
INSERT INTO cities (code, name, english_name, country_id, is_favorite, display_order) VALUES
('TORONTO', '토론토', 'Toronto', (SELECT id FROM countries WHERE code = 'CANADA'), false, 1),
('VANCOUVER', '벤쿠버', 'Vancouver', (SELECT id FROM countries WHERE code = 'CANADA'), false, 2),
('MONTREAL', '몬트리올', 'Montreal', (SELECT id FROM countries WHERE code = 'CANADA'), false, 3);

-- 독일 도시들
INSERT INTO cities (code, name, english_name, country_id, is_favorite, display_order) VALUES
('BERLIN', '베를린', 'Berlin', (SELECT id FROM countries WHERE code = 'GERMANY'), false, 1),
('MUNICH', '뮈헨', 'Munich', (SELECT id FROM countries WHERE code = 'GERMANY'), false, 2),
('HEIDELBERG', '하이델베르크', 'Heidelberg', (SELECT id FROM countries WHERE code = 'GERMANY'), false, 3),
('HAMBURG', '함부르크', 'Hamburg', (SELECT id FROM countries WHERE code = 'GERMANY'), false, 4);

-- 프랑스 도시들
INSERT INTO cities (code, name, english_name, country_id, is_favorite, display_order) VALUES
('PARIS', '파리', 'Paris', (SELECT id FROM countries WHERE code = 'FRANCE'), false, 1),
('LYON', '리옹', 'Lyon', (SELECT id FROM countries WHERE code = 'FRANCE'), false, 2);

-- 이탈리아 도시들
INSERT INTO cities (code, name, english_name, country_id, is_favorite, display_order) VALUES
('ROME', '로마', 'Rome', (SELECT id FROM countries WHERE code = 'ITALY'), false, 1),
('MILAN', '밀라노', 'Milan', (SELECT id FROM countries WHERE code = 'ITALY'), false, 2),
('BOLOGNA', '볼로냐', 'Bologna', (SELECT id FROM countries WHERE code = 'ITALY'), false, 3);

-- 네덜란드 도시들
INSERT INTO cities (code, name, english_name, country_id, is_favorite, display_order) VALUES
('AMSTERDAM', '암스테르담', 'Amsterdam', (SELECT id FROM countries WHERE code = 'NETHERLANDS'), false, 1),
('ROTTERDAM', '로테르담', 'Rotterdam', (SELECT id FROM countries WHERE code = 'NETHERLANDS'), false, 2);

-- 스페인 도시들
INSERT INTO cities (code, name, english_name, country_id, is_favorite, display_order) VALUES
('BARCELONA', '바르셀로나', 'Barcelona', (SELECT id FROM countries WHERE code = 'SPAIN'), false, 1),
('MADRID', '마드리드', 'Madrid', (SELECT id FROM countries WHERE code = 'SPAIN'), false, 2),
('VALENCIA', '발렌시아', 'Valencia', (SELECT id FROM countries WHERE code = 'SPAIN'), false, 3);

-- 노르웨이 도시들
INSERT INTO cities (code, name, english_name, country_id, is_favorite, display_order) VALUES
('OSLO', '오슬로', 'Oslo', (SELECT id FROM countries WHERE code = 'NORWAY'), false, 1),
('TRONDHEIM', '트론드하임', 'Trondheim', (SELECT id FROM countries WHERE code = 'NORWAY'), false, 2);

-- 스위스 도시들
INSERT INTO cities (code, name, english_name, country_id, is_favorite, display_order) VALUES
('ZURICH', '취리히', 'Zurich', (SELECT id FROM countries WHERE code = 'SWITZERLAND'), false, 1);

-- 아랍에미리트 도시들
INSERT INTO cities (code, name, english_name, country_id, is_favorite, display_order) VALUES
('DUBAI', '두바이', 'Dubai', (SELECT id FROM countries WHERE code = 'UAE'), false, 1);

-- 4. 데이터 확인 쿼리
-- SELECT c.name as country, ci.name as city, ci.is_favorite 
-- FROM countries c 
-- JOIN cities ci ON c.id = ci.country_id 
-- ORDER BY c.name, ci.display_order;

-- 5. 기존 enum 데이터 마이그레이션 (필요시 실행)
-- 기존 survey_basic_infos 테이블의 country 컬럼을 city_id로 변경하는 작업은
-- 새로운 엔티티 구조 완성 후 별도 스크립트로 진행