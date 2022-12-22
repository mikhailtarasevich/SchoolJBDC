package com.mikhail.tarasevich.dao.impl;

import com.google.inject.Inject;
import com.mikhail.tarasevich.dao.ConnectorDB;
import com.mikhail.tarasevich.dao.CourseDao;
import com.mikhail.tarasevich.dao.exception.DataBaseSqlRuntimeException;
import com.mikhail.tarasevich.entity.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CourseDaoImpl extends AbstractPageableCrudDaoImpl<Course> implements CourseDao {

    private static final Logger LOG = LoggerFactory.getLogger(CourseDaoImpl.class);
    private static final String SAVE_COURSE_QUERY = "INSERT INTO school.courses (course_name, description) VALUES(?, ?)";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM school.courses WHERE id=?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM school.courses ORDER BY id";
    private static final String FIND_ALL_PAGEABLE_QUERY = "SELECT * FROM school.courses ORDER BY id LIMIT ? OFFSET ?";
    private static final String FIND_BY_COURSE_NAME_QUERY = "SELECT * FROM school.courses WHERE course_name=?";
    private static final String FIND_COURSES_BY_STUDENT_ID_QUERY =
            "SELECT courses.id, course_name, courses.description\n" +
                    "FROM school.student_courses\n" +
                    "LEFT JOIN school.students ON students.id = student_id\n" +
                    "LEFT JOIN school.courses ON courses.id = course_id\n" +
                    "WHERE students.id = ?\n" +
                    "ORDER BY courses.id";
    private static final String UPDATE_COURSE_QUERY = "UPDATE school.courses SET course_name = ?," +
            "description = ? WHERE id= ?";
    private static final String DELETE_COURSE_QUERY = "DELETE FROM school.courses WHERE id=?";
    private static final String DELETE_COURSE_DEPENDENCIES_QUERY =
            "DELETE FROM school.student_courses WHERE course_id=?";
    private static final String COUNT_TABLE_ROWS_QUERY = "SELECT COUNT(*) FROM school.courses";

    @Inject
    public CourseDaoImpl(ConnectorDB connector) {
        super(connector, SAVE_COURSE_QUERY, FIND_BY_ID_QUERY, FIND_ALL_QUERY, FIND_ALL_PAGEABLE_QUERY,
                UPDATE_COURSE_QUERY, DELETE_COURSE_QUERY, COUNT_TABLE_ROWS_QUERY);
    }

    @Override
    public Optional<Course> findByCourseName(String courseName) {
        return findByStringParam(courseName, FIND_BY_COURSE_NAME_QUERY);
    }

    @Override
    public List<Course> findCoursesByStudentId(int id) {
        return findManyByIntParam(id, FIND_COURSES_BY_STUDENT_ID_QUERY);
    }

    @Override
    public void deleteCourseById(int id) {
        deleteCourseFromCourseStudentsTable(id);
        deleteById(id);
        LOG.info("Course with id = {} was deleted", id);
    }

    @Override
    protected void setStatementForSave(PreparedStatement preparedStatement, Course course) {
        try {
            preparedStatement.setString(1, course.getCourseName());
            preparedStatement.setString(2, course.getDescription());
        } catch (SQLException e) {
            LOG.error("Course has incorrect data for save. Course parameters:  {}. Thrown exception: {}",
                    course, e);
            throw new DataBaseSqlRuntimeException("", e);
        }
    }

    @Override
    protected void setStatementForUpdate(PreparedStatement preparedStatement, Course course) {
        try {
            preparedStatement.setString(1, course.getCourseName());
            preparedStatement.setString(2, course.getDescription());
            preparedStatement.setInt(3, course.getId());
        } catch (SQLException e) {
            LOG.error("Course has incorrect data for update. Course parameters:  {}. Thrown exception: {}",
                    course, e);
            throw new DataBaseSqlRuntimeException("", e);
        }
    }

    @Override
    protected Course mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        return Course.builder()
                .withId(resultSet.getInt("id"))
                .withCourseName(resultSet.getString("course_name"))
                .withDescription(resultSet.getString("description"))
                .build();
    }

    @Override
    protected Course makeEntityWithId(Course course, int id) {
        return Course.builder()
                .withId(id)
                .withCourseName(course.getCourseName())
                .withDescription(course.getDescription())
                .build();
    }

    private void deleteCourseFromCourseStudentsTable(Integer id) {
        try (final Connection connection = connector.getConnection();
             final PreparedStatement preparedStatement = connection
                     .prepareStatement(DELETE_COURSE_DEPENDENCIES_QUERY)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOG.error("Course with id {} wasn't removed from table by SQL statement {}. Thrown exception: {}",
                    id, DELETE_COURSE_DEPENDENCIES_QUERY, e);
            throw new DataBaseSqlRuntimeException("", e);
        }
    }

}
