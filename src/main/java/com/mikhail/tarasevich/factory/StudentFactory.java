package com.mikhail.tarasevich.factory;

import com.mikhail.tarasevich.entity.Course;
import com.mikhail.tarasevich.entity.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StudentFactory {

    private static final Logger LOG = LoggerFactory.getLogger(StudentFactory.class);
    private static final String[] FIRST_NAMES = {"Andy", "Carlos", "John", "Lenny", "Leonard", "Henry", "Bob",
            "Bill", "Benjamin", "Leonel", "James", "Tracy", "Steven", "Robbie", "Harry", "Garold", "Yan", "Richard",
            "Vincent", "Hernandez", "Luis", "Kevin", "Neil", "Benny", "Dennis", "Lory", "Kenny", "Daniel", "Rupert",
            "Paul", "Ronald", "Neville", "Chester", "Roland"};
    private static final String[] LAST_NAMES = {"Robertson", "Johnson", "James", "Irving", "Iverson", "Gerard",
            "Price", "Jordan", "Antony", "Gunter", "Harden", "Barry", "Cole", "Potter", "Arenas", "O'Neil", "Carter",
            "Wade", "Hernandez", "Ivanov", "Cox", "Don", "Brown", "Yellow", "Allen", "Green", "Oreo", "Lord", "Smith",
            "Howard", "George", "Storm", "Seagull", "Snow", "Durant", "Henderson"};

    public List<Student> generateStudents(int studentQuantity, int groupQuantity, List<Course> courses) {

        LOG.debug("Method generateStudents(int studentQuantity, int groupQuantity, List<Course> courses) was called");

        int successIterationQuantity = 1;
        int groupIdDistributor = 1;
        int groupIdChanger = (int) Math.round((double)studentQuantity / (double)groupQuantity);
        int groupIdChangerStep = (int) Math.ceil((double)studentQuantity / (double)groupQuantity);
        Random random = new Random();
        List<Student> students = new ArrayList<>();

        while (successIterationQuantity <= studentQuantity) {

            LOG.debug("Number of Success iteration successIterationQuantity = {} ", successIterationQuantity);

            int successCourseIteration = 0;
            List<Course> studentCourses = new ArrayList<>();

            while(successCourseIteration < 3){
                Course course = courses.get(random.nextInt(courses.size()));
                if(!studentCourses.contains(course)){
                    studentCourses.add(course);
                    successCourseIteration++;
                }
            }

            LOG.debug("Courses list for next generated student was made {}", courses);

            if (successIterationQuantity > groupIdChanger) {
                groupIdDistributor++;
                groupIdChanger += groupIdChangerStep;
            }

            LOG.debug("Next students will be generated with group id = {}", groupIdDistributor);

            Student student = Student.builder()
                    .withId(successIterationQuantity)
                    .withFirstName(FIRST_NAMES[random.nextInt(FIRST_NAMES.length)])
                    .withLastName(LAST_NAMES[random.nextInt(LAST_NAMES.length)])
                    .withGroupId(groupIdDistributor)
                    .withCoursesList(studentCourses)
                    .build();

            LOG.debug("Student was generated with parameters: {}", student);

            if (!students.contains(student)) {
                students.add(student);
                successIterationQuantity++;
                LOG.debug("Student was added to output method student list");
            } else {
                LOG.debug("Student wasn't added to output method student list," +
                        " because list already has the same student");
            }
        }

        LOG.debug("Output method student's list: {}", students);
        return students;
    }

}
