package com.mikhail.tarasevich.factory;

import com.mikhail.tarasevich.entity.Course;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CourseFactoryTest {

    private static final CourseFactory courseFactory = new CourseFactory();
    private static final String[] COURSES_NAMES = {"Algebra", "Astronomy",
            "Literature", "Computer science", "Geography", "History",
            "Physics", "English", "Ecology", "Music"};
    private static final String[] COURSES_DESCRIPTIONS = {"Math course for beginners", "Subject about stars and space",
            "Literature of 18 century", "Subject about computers and how it works", "World geography", "Modern history",
            "Physics of Electricity", "British english", "Subject is about how to save a Earth", "Modern music"};

    @Test
    void generateCourses_inputNothing_expectedCourses() {

        List<Course> courses = courseFactory.generateCourses();

        for(int i = 0; i < courses.size(); i++){
            assertEquals(courses.get(i).getCourseName(), COURSES_NAMES[i]);
            assertEquals(courses.get(i).getDescription(), COURSES_DESCRIPTIONS[i]);
        }

        int expectedSize = 10;

        assertEquals(expectedSize, courses.size());
    }

}
