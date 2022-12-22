package com.mikhail.tarasevich.dao;

import com.mikhail.tarasevich.entity.Course;

import java.util.List;
import java.util.Optional;

public interface CourseDao extends CrudPageableDao<Course> {

    //read
    Optional<Course> findByCourseName(String courseName);
    List<Course> findCoursesByStudentId(int id);

    //delete
    void deleteCourseById(int id);
}
