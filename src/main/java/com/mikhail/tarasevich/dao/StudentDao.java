package com.mikhail.tarasevich.dao;

import com.mikhail.tarasevich.entity.Student;

import java.util.List;

public interface StudentDao extends CrudPageableDao<Student>{

    //save
    void subscribeStudentToCourses(Student student);
    void subscribeStudentToCourse(int studentId, int courseId);

    //read
    List<Student> findByFirstName(String firstName);
    List<Student> findStudentsRelatedToCourse(int courseId);
    List<Student> findStudentsByGroupId(int id);

    //delete
    void deleteStudentById(int id);
    void removeStudentFromCourse (int studentId, int courseId);
    void removeStudentFromGroup(int studentId);

}
