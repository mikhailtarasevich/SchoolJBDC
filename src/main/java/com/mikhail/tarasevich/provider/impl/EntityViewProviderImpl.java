package com.mikhail.tarasevich.provider.impl;

import com.mikhail.tarasevich.entity.Course;
import com.mikhail.tarasevich.entity.Group;
import com.mikhail.tarasevich.entity.Student;
import com.mikhail.tarasevich.provider.EntityViewProvider;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class EntityViewProviderImpl implements EntityViewProvider {

    private static final String STUDENT_TEMPLATE = "%-5s%-20s%-20s%-20s%-50s\n";
    private static final String GROUP_TEMPLATE = "%-5s%-10s\n";
    private static final String COURSE_TEMPLATE = "%-5s%-20s%-40s\n";

    @Override
    public String provideStudentTableView(List<Student> students) {

        AtomicInteger counter = new AtomicInteger();

        return students.stream().map(student -> {
            StringBuilder resultTable = new StringBuilder();
            counter.getAndIncrement();
            if (counter.get() == 1) {
                resultTable.append(String.format(STUDENT_TEMPLATE, "ID", "First name", "Last name", "Group ID",
                        "Student's courses"));
            }
            return resultTable.append(studentTableLineMaker(student));
        }).collect(Collectors.joining());
    }

    @Override
    public String provideGroupTableView(List<Group> groups) {

        AtomicInteger counter = new AtomicInteger();

        return groups.stream().map(group -> {
            StringBuilder resultTable = new StringBuilder();
            counter.getAndIncrement();
            if (counter.get() == 1) {
                resultTable.append(String.format(GROUP_TEMPLATE, "ID", "Group name"));
            }
            return resultTable.append(groupTableLineMaker(group));
        }).collect(Collectors.joining());
    }

    @Override
    public String provideCourseTableView(List<Course> courses) {

        AtomicInteger counter = new AtomicInteger();

        return courses.stream().map(course -> {
            StringBuilder resultTable = new StringBuilder();
            counter.getAndIncrement();
            if (counter.get() == 1) {
                resultTable.append(String.format(COURSE_TEMPLATE, "ID", "Course name", "Description"));
            }
            return resultTable.append(courseTableLineMaker(course));
        }).collect(Collectors.joining());
    }

    private String studentTableLineMaker(Student student) {

        StringBuilder resultTable = new StringBuilder();

        return resultTable.append(String.format(STUDENT_TEMPLATE, student.getId(), student.getFirstName(),
                student.getLastName(), student.getGroupId(), student.getCoursesList())).toString();
    }

    private String groupTableLineMaker(Group group) {

        StringBuilder resultTable = new StringBuilder();

        return resultTable.append(String.format(GROUP_TEMPLATE, group.getId(), group.getGroupName())).toString();
    }

    private String courseTableLineMaker(Course course) {

        StringBuilder resultTable = new StringBuilder();

        return resultTable.append(String.format(COURSE_TEMPLATE, course.getId(), course.getCourseName(),
                course.getDescription())).toString();
    }

}
