package com.mikhail.tarasevich.provider.impl;

import com.mikhail.tarasevich.entity.Course;
import com.mikhail.tarasevich.entity.Group;
import com.mikhail.tarasevich.entity.Student;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityViewProviderImplTest {

    private static final String STUDENT_TEMPLATE = "%-5s%-20s%-20s%-20s%-50s\n";
    private static final String GROUP_TEMPLATE = "%-5s%-10s\n";
    private static final String COURSE_TEMPLATE = "%-5s%-20s%-40s\n";
    EntityViewProviderImpl entityViewProvider = new EntityViewProviderImpl();
    private static final Course course1 = Course.builder()
            .withId(1)
            .withCourseName("History")
            .withDescription("History of Europe")
            .build();
    private static final Course course2 = Course.builder()
            .withId(2)
            .withCourseName("English")
            .withDescription("British english")
            .build();
    private final List<Course> courses = new ArrayList<>();
    private static Student student1;
    private static Student student2;
    private final List<Student> students = new ArrayList<>();
    private static Group group1;
    private static Group group2;
    private final List<Group> groups = new ArrayList<>();
    private final List<Student> studentsFromGroup1 = new ArrayList<>();
    private final List<Student> studentsFromGroup2 = new ArrayList<>();

    {
        courses.add(course1);
        courses.add(course2);

        student1 = Student.builder()
                .withId(1)
                .withFirstName("John")
                .withLastName("Locke")
                .withGroupId(1)
                .withCoursesList(courses)
                .build();
        student2 = Student.builder()
                .withId(2)
                .withFirstName("Jack")
                .withLastName("Shephard")
                .withGroupId(2)
                .withCoursesList(courses)
                .build();

        students.add(student1);
        students.add(student2);

        studentsFromGroup1.add(student1);
        studentsFromGroup2.add(student2);

        group1 = Group.builder()
                .withId(1)
                .withGroupName("go-21")
                .withStudentsList(studentsFromGroup1)
                .build();
        group2 = Group.builder()
                .withId(2)
                .withGroupName("go-22")
                .withStudentsList(studentsFromGroup2)
                .build();

        groups.add(group1);
        groups.add(group2);
    }

    @Test
    void provideStudentTableView_inputStudentsList_expectedTable() {

        String expected =
        String.format(STUDENT_TEMPLATE, "ID", "First name", "Last name", "Group ID", "Student's courses") +
        String.format(STUDENT_TEMPLATE, "1", "John", "Locke", "1", courses) +
        String.format(STUDENT_TEMPLATE, "2", "Jack", "Shephard", "2", courses);

        String resultTable = entityViewProvider.provideStudentTableView(students);

        assertEquals(expected, resultTable);
    }

    @Test
    void provideGroupTableView_inputGroupsList_expectedTable() {

        String expected =
        String.format(GROUP_TEMPLATE, "ID", "Group name") +
        String.format(GROUP_TEMPLATE, "1", "go-21", studentsFromGroup1) +
        String.format(GROUP_TEMPLATE, "2", "go-22", studentsFromGroup2);

        String resultTable = entityViewProvider.provideGroupTableView(groups);

        assertEquals(expected, resultTable);
    }

    @Test
    void provideCourseTableView_inputCoursesList_expectedTable() {

        String expected =
        String.format(COURSE_TEMPLATE, "ID", "Course name", "Description") +
        String.format(COURSE_TEMPLATE, "1", "History", "History of Europe") +
        String.format(COURSE_TEMPLATE, "2", "English", "British english");

        String resultTable = entityViewProvider.provideCourseTableView(courses);

        assertEquals(expected, resultTable);
    }

}
