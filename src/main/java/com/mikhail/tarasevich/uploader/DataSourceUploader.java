package com.mikhail.tarasevich.uploader;

import com.google.inject.Inject;
import com.mikhail.tarasevich.dao.CourseDao;
import com.mikhail.tarasevich.dao.GroupDao;
import com.mikhail.tarasevich.dao.StudentDao;
import com.mikhail.tarasevich.entity.Course;
import com.mikhail.tarasevich.entity.Group;
import com.mikhail.tarasevich.entity.Student;
import com.mikhail.tarasevich.factory.CourseFactory;
import com.mikhail.tarasevich.factory.GroupFactory;
import com.mikhail.tarasevich.factory.StudentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DataSourceUploader {

    private static final Logger LOG = LoggerFactory.getLogger(DataSourceUploader.class);
    private final StudentFactory studentFactory;
    private final CourseFactory courseFactory;
    private final GroupFactory groupFactory;
    private final StudentDao studentDao;
    private final CourseDao courseDao;
    private final GroupDao groupDao;

    @Inject
    public DataSourceUploader(StudentDao studentDao, CourseDao courseDao, GroupDao groupDao,
                              StudentFactory studentFactory, CourseFactory courseFactory, GroupFactory groupFactory) {
        this.studentDao = studentDao;
        this.courseDao = courseDao;
        this.groupDao = groupDao;
        this.studentFactory = studentFactory;
        this.courseFactory = courseFactory;
        this.groupFactory = groupFactory;
    }

    public void uploadRandomDataToDB(int groupQuantity, int studentQuantity) {

        LOG.debug("Method uploadRandomDataToDB (int groupQuantity, int studentQuantity) was called");

        List<Course> generatedCourses = courseFactory.generateCourses();
        courseDao.saveAll(generatedCourses);
        List<Course> coursesFromDB = courseDao.findAll();
        LOG.info("Generated courses, uploaded to DB, parsed saved courses from DB. Courses parameters: {}",
                coursesFromDB);

        List<Group> generatedGroups = groupFactory.generateGroups(groupQuantity);
        groupDao.saveAll(generatedGroups);
        LOG.info("Generated groups, uploaded to DB. Groups parameters: {}", generatedGroups);

        List<Student> generatedStudents = studentFactory.generateStudents(studentQuantity, groupQuantity, coursesFromDB);
        studentDao.saveAll(generatedStudents);
        LOG.info("Generated students, uploaded to DB. Students parameters: {}", generatedStudents);

        for (Student student : generatedStudents) {
            studentDao.subscribeStudentToCourses(student);
        }
        LOG.info("Students was subscribed on courses. Information added to DB");
    }

}
