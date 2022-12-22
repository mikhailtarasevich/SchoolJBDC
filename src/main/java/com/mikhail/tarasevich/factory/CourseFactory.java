package com.mikhail.tarasevich.factory;

import com.mikhail.tarasevich.entity.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CourseFactory {

    private static final Logger LOG = LoggerFactory.getLogger(CourseFactory.class);
    private static final String[] COURSES_NAMES = {"Algebra", "Astronomy",
            "Literature", "Computer science", "Geography", "History",
            "Physics", "English", "Ecology", "Music"};
    private static final String[] COURSES_DESCRIPTIONS = {"Math course for beginners", "Subject about stars and space",
            "Literature of 18 century", "Subject about computers and how it works", "World geography", "Modern history",
            "Physics of Electricity", "British english", "Subject is about how to save a Earth", "Modern music"};

    public List<Course> generateCourses(){

        LOG.debug("Method generateCourses() was called");

        List<Course> courses = new ArrayList<>();
        for(int i = 0; i < COURSES_NAMES.length; i++){
            Course course = Course.builder()
                    .withCourseName(COURSES_NAMES[i])
                    .withDescription(COURSES_DESCRIPTIONS[i])
                    .build();
            courses.add(course);
            LOG.debug("Course was added to output method list: {}", course);
        }
        LOG.debug("Output method courses' list: {}", courses);
        return courses;
    }

}
