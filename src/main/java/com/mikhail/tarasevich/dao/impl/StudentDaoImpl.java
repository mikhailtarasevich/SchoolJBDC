package com.mikhail.tarasevich.dao.impl;

import com.google.inject.Inject;
import com.mikhail.tarasevich.dao.ConnectorDB;
import com.mikhail.tarasevich.dao.CourseDao;
import com.mikhail.tarasevich.dao.StudentDao;
import com.mikhail.tarasevich.dao.exception.DataBaseSqlRuntimeException;
import com.mikhail.tarasevich.entity.Course;
import com.mikhail.tarasevich.entity.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class StudentDaoImpl extends AbstractPageableCrudDaoImpl<Student> implements StudentDao {

    private static final Logger LOG = LoggerFactory.getLogger(StudentDaoImpl.class);
    private static final String SAVE_STUDENT_QUERY =
            "INSERT INTO school.students (first_name, last_name, group_id) VALUES(?, ?, ?)";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM school.students WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM school.students ORDER BY id";
    private static final String FIND_ALL_PAGEABLE_QUERY = "SELECT * FROM school.students ORDER BY id LIMIT ? OFFSET ?";
    private static final String FIND_BY_FIRST_NAME_QUERY = "SELECT * FROM school.students " +
            "WHERE first_name = ? ORDER BY id";
    private static final String FIND_STUDENTS_RELATED_TO_COURSE_QUERY =
            "SELECT students.id, first_name, last_name, group_id\n" +
                    "FROM school.student_courses\n" +
                    "LEFT JOIN school.students ON students.id = student_id\n" +
                    "WHERE course_id = ?";
    private static final String FIND_STUDENTS_BY_GROUP_ID_QUERY = "SELECT * FROM school.students WHERE group_id = ?";
    private static final String SUBSCRIBE_STUDENT_ON_COURSE_QUERY =
            "INSERT INTO school.student_courses (student_id, course_id) VALUES(?, ?)";
    private static final String UPDATE_STUDENT_QUERY =
            "UPDATE school.students SET first_name = ?, last_name = ?, group_id = ? WHERE id = ?";
    private static final String DELETE_STUDENT_QUERY = "DELETE FROM school.students WHERE id = ?";
    private static final String DELETE_STUDENT_FROM_STUDENT_COURSES_TABLE_QUERY =
            "DELETE FROM school.student_courses WHERE student_id = ?";
    private static final String REMOVE_STUDENT_FROM_GROUP_QUERY =
            "UPDATE school.students SET group_id = NULL WHERE id = ?";
    private static final String REMOVE_STUDENT_FROM_COURSE_QUERY =
            "DELETE FROM school.student_courses WHERE student_id = ? AND course_id = ?";
    private static final String COUNT_TABLE_ROWS_QUERY = "SELECT COUNT(*) FROM school.students";
    private final CourseDao courseDao;

    @Inject
    public StudentDaoImpl(ConnectorDB connector, CourseDao courseDao) {
        super(connector, SAVE_STUDENT_QUERY, FIND_BY_ID_QUERY, FIND_ALL_QUERY, FIND_ALL_PAGEABLE_QUERY,
                UPDATE_STUDENT_QUERY, DELETE_STUDENT_QUERY, COUNT_TABLE_ROWS_QUERY);
        this.courseDao = courseDao;
    }

    @Override
    public void subscribeStudentToCourses(Student student) {
        try (final Connection connection = connector.getConnection();
             final PreparedStatement preparedStatement = connection
                     .prepareStatement(SUBSCRIBE_STUDENT_ON_COURSE_QUERY)) {
            for (Course course : student.getCoursesList()) {
                preparedStatement.setInt(1, student.getId());
                preparedStatement.setInt(2, course.getId());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            LOG.info("Student was subscribed to courses.");
        } catch (SQLException e) {
            LOG.error("Student wasn't subscribed to courses. Thrown exception: {}", e);
            throw new DataBaseSqlRuntimeException("", e);
        }
    }

    @Override
    public void subscribeStudentToCourse(int studentId, int courseId) {
        try (final Connection connection = connector.getConnection();
             final PreparedStatement preparedStatement = connection
                     .prepareStatement(SUBSCRIBE_STUDENT_ON_COURSE_QUERY)) {
            preparedStatement.setInt(1, studentId);
            preparedStatement.setInt(2, courseId);
            preparedStatement.executeUpdate();
            LOG.info("Student with id = {} was subscribed to course with id = {}", studentId, courseId);
        } catch (SQLException e) {
            LOG.error("Student with id = {} wasn't subscribed to course with id = {}. Thrown exception: {}",
                    studentId, courseId, e);
            throw new DataBaseSqlRuntimeException("", e);
        }
    }

    @Override
    public List<Student> findByFirstName(String firstName) {
        return findManyByStringParam(firstName, FIND_BY_FIRST_NAME_QUERY);
    }

    @Override
    public List<Student> findStudentsRelatedToCourse(int courseId) {
        return findManyByIntParam(courseId, FIND_STUDENTS_RELATED_TO_COURSE_QUERY);
    }

    @Override
    public List<Student> findStudentsByGroupId(int id) {
        return findManyByIntParam(id, FIND_STUDENTS_BY_GROUP_ID_QUERY);
    }

    @Override
    public void deleteStudentById(int id) {
        removeStudentFromTable(id, DELETE_STUDENT_FROM_STUDENT_COURSES_TABLE_QUERY);
        deleteById(id);
        LOG.info("Student was deleted. Student id = {}", id);
    }

    @Override
    public void removeStudentFromGroup(int studentId) {
        removeStudentFromTable(studentId, REMOVE_STUDENT_FROM_GROUP_QUERY);
        LOG.info("Student with id = {} was removed from group", studentId);
    }

    @Override
    public void removeStudentFromCourse(int studentId, int courseId) {
        try (final Connection connection = connector.getConnection();
             final PreparedStatement preparedStatement = connection
                     .prepareStatement(REMOVE_STUDENT_FROM_COURSE_QUERY)) {
            preparedStatement.setInt(1, studentId);
            preparedStatement.setInt(2, courseId);
            preparedStatement.executeUpdate();
            LOG.info("Student with id = {} was removed from course with id = {}.", studentId, courseId);
        } catch (SQLException e) {
            LOG.error("Student with id = {} wasn't removed from course with id = {}. Thrown exception: {}",
                    studentId, courseId, e);
            throw new DataBaseSqlRuntimeException("", e);
        }
    }

    @Override
    protected void setStatementForSave(PreparedStatement preparedStatement, Student student) {
        try {
            preparedStatement.setString(1, student.getFirstName());
            preparedStatement.setString(2, student.getLastName());
            preparedStatement.setInt(3, student.getGroupId());
        } catch (SQLException e) {
            LOG.error("Student has incorrect data for save. Student parameters:  {}. Thrown exception: {}",
                    student, e);
            throw new DataBaseSqlRuntimeException("", e);
        }
    }

    @Override
    protected void setStatementForUpdate(PreparedStatement preparedStatement, Student student) {
        try {
            preparedStatement.setString(1, student.getFirstName());
            preparedStatement.setString(2, student.getLastName());
            preparedStatement.setInt(3, student.getGroupId());
            preparedStatement.setInt(4, student.getId());
        } catch (SQLException e) {
            LOG.error("Student has incorrect data for update. Student parameters:  {}. Thrown exception: {}",
                    student, e);
            throw new DataBaseSqlRuntimeException("", e);
        }
    }

    @Override
    protected Student mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        List<Course> courses = courseDao.findCoursesByStudentId(id);
        return Student.builder()
                .withId(id)
                .withFirstName(resultSet.getString("first_name"))
                .withLastName(resultSet.getString("last_name"))
                .withGroupId(resultSet.getInt("group_id"))
                .withCoursesList(courses)
                .build();
    }

    @Override
    protected Student makeEntityWithId(Student student, int id) {
        return Student.builder()
                .withId(id)
                .withFirstName(student.getFirstName())
                .withLastName(student.getLastName())
                .withGroupId(student.getGroupId())
                .withCoursesList(student.getCoursesList())
                .build();
    }

    private void removeStudentFromTable(Integer id, String removeStudentFromTable) {
        try (final Connection connection = connector.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(removeStudentFromTable)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOG.info("Student with id {} was removed from table by SQL statement {}.",
                    id, removeStudentFromTable);
        } catch (SQLException e) {
            LOG.error("Student with id {} wasn't removed from table by SQL statement {}. Thrown exception: {}",
                    id, removeStudentFromTable, e);
            throw new DataBaseSqlRuntimeException("", e);
        }
    }

}
