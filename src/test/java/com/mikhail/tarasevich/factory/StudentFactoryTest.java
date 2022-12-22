package com.mikhail.tarasevich.factory;

import com.mikhail.tarasevich.entity.Course;
import com.mikhail.tarasevich.entity.Student;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StudentFactoryTest {

    private static final StudentFactory studentFactory = new StudentFactory();

    private static final String[] FIRST_NAMES = {"Andy", "Carlos", "John", "Lenny", "Leonard", "Henry", "Bob",
            "Bill", "Benjamin", "Leonel", "James", "Tracy", "Steven", "Robbie", "Harry", "Garold", "Yan", "Richard",
            "Vincent", "Hernandez", "Luis", "Kevin", "Neil", "Benny", "Dennis", "Lory", "Kenny", "Daniel", "Rupert",
            "Paul", "Ronald", "Neville", "Chester", "Roland"};

    private static final String[] LAST_NAMES = {"Robertson", "Johnson", "James", "Irving", "Iverson", "Gerard",
            "Price", "Jordan", "Antony", "Gunter", "Harden", "Barry", "Cole", "Potter", "Arenas", "O'Neil", "Carter",
            "Wade", "Hernandez", "Ivanov", "Cox", "Don", "Brown", "Yellow", "Allen", "Green", "Oreo", "Lord", "Smith",
            "Howard", "George", "Storm", "Seagull", "Snow", "Durant", "Henderson"};
    private static final Course course1 = Course.builder()
            .withId(1)
            .withCourseName("History")
            .withDescription("Modern history of Europe")
            .build();
    private static final Course course2 = Course.builder()
            .withId(2)
            .withCourseName("English")
            .withDescription("British english")
            .build();
    private static final Course course3 = Course.builder()
            .withId(3)
            .withCourseName("Geography")
            .withDescription("Geography of North America")
            .build();

    @Test
    void generateStudents_inputStudentQuantity_expectedStudentList(){

        List<Course> courses = new ArrayList<>();
        courses.add(course1);
        courses.add(course2);
        courses.add(course3);

        List<Student> students = studentFactory.generateStudents(2, 2, courses);

        int expectedSize = 2;

        assertTrue(Arrays.asList(FIRST_NAMES).contains(students.get(0).getFirstName()));
        assertTrue(Arrays.asList(FIRST_NAMES).contains(students.get(1).getFirstName()));
        assertTrue(Arrays.asList(LAST_NAMES).contains(students.get(0).getLastName()));
        assertTrue(Arrays.asList(LAST_NAMES).contains(students.get(1).getLastName()));
        assertEquals(expectedSize, students.size());
    }

}
