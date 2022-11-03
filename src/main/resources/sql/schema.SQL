DROP SCHEMA IF EXISTS school CASCADE;

CREATE SCHEMA school;

SET search_path TO school;

DROP TABLE IF EXISTS "groups" CASCADE;

CREATE TABLE "groups"
(
    "id" SERIAL PRIMARY KEY,
    group_name VARCHAR(255) NOT NULL
);

DROP TABLE IF EXISTS students CASCADE;

CREATE TABLE students 
(
    "id" SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
	last_name VARCHAR(255) NOT NULL,
    group_id INT,
    CONSTRAINT fk_groups
        FOREIGN KEY(group_id)
        REFERENCES "groups"("id")
);

DROP TABLE IF EXISTS courses CASCADE;

CREATE TABLE courses
(
	"id" SERIAL PRIMARY KEY,
	course_name VARCHAR(255) NOT NULL,
	description TEXT
);

DROP TABLE IF EXISTS student_courses CASCADE;

CREATE TABLE student_courses
(
	student_id INT,
	course_id INT,
	CONSTRAINT fk_students
        FOREIGN KEY(student_id)
        REFERENCES students("id"),
	CONSTRAINT fk_courses
        FOREIGN KEY(course_id)
        REFERENCES courses("id")
);


