package com.mikhail.tarasevich.provider;

import com.mikhail.tarasevich.entity.Course;
import com.mikhail.tarasevich.entity.Group;
import com.mikhail.tarasevich.entity.Student;

import java.util.List;

public interface EntityViewProvider {

    String provideStudentTableView (List<Student> students);
    String provideGroupTableView (List<Group> groups);
    String provideCourseTableView (List<Course> courses);

}
