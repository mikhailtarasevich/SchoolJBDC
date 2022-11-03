DROP SCHEMA IF EXISTS school CASCADE;

CREATE SCHEMA school;

CREATE TABLE school.groups
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    group_name VARCHAR(255) NOT NULL
);

CREATE TABLE school.students
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    group_id   INT,
    CONSTRAINT fk_groups
        FOREIGN KEY (group_id)
            REFERENCES school.groups (id)
);

CREATE TABLE school.courses
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE school.student_courses
(
    student_id INT,
    course_id  INT,
    CONSTRAINT fk_students
        FOREIGN KEY (student_id)
            REFERENCES school.students (id),
    CONSTRAINT fk_courses
        FOREIGN KEY (course_id)
            REFERENCES school.courses (id)
);

INSERT INTO school.groups
    (group_name)
VALUES ('go-21'),
       ('go-22'),
       ('go-23'),
       ('go-24');

INSERT INTO school.students
    (first_name, last_name, group_id)
VALUES ('John', 'Locke', 3),
       ('Jack', 'Shephard', 1),
       ('Kate', 'Austin', 1),
       ('James', 'Ford', 2),
       ('Leonardo', 'DiCaprio', NULL);

INSERT INTO school.courses
    (course_name, description)
VALUES ('History', 'Modern history of Europe'),
       ('English', 'British english'),
       ('Geography', 'Geography of North America'),
       ('RPA', 'Relay protection and automation');

INSERT INTO school.student_courses
    (student_id, course_id)
VALUES (1, 2),
       (1, 1),
       (2, 2),
       (2, 3),
       (3, 1),
       (3, 3),
       (4, 1),
       (4, 2);