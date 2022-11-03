package com.mikhail.tarasevich.dao.impl;

import com.mikhail.tarasevich.dao.ConnectorDB;
import com.mikhail.tarasevich.dao.ScriptRunner;
import com.mikhail.tarasevich.dao.exception.DataBaseSqlRuntimeException;
import com.mikhail.tarasevich.entity.Course;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseDaoImplTest {

    private static final String SCRIPT_TEST_DB_PATH = "src/test/resources/sql/testDB.SQL";
    private static final String PATH_TO_TEST_DB_PROP = "database";
    private final ConnectorDB connectorDB = new ConnectorDBImpl(PATH_TO_TEST_DB_PROP);
    private final CourseDaoImpl courseDao = new CourseDaoImpl(connectorDB);
    private final ScriptRunner scriptRunner = new ScriptRunnerImpl(connectorDB);
    @InjectMocks
    private CourseDaoImpl sutCourseDao;
    @Mock
    private ConnectorDB sutConnectorDB;
    @Mock
    private Connection sutConnection;
    @Mock
    private PreparedStatement sutPreparedStatement;
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
    private static final Course course4 = Course.builder()
            .withId(4)
            .withCourseName("RPA")
            .withDescription("Relay protection and automation")
            .build();

    @BeforeEach
    public void runScript() {
        scriptRunner.runScript(SCRIPT_TEST_DB_PATH);
    }

    @Test
    void save_inputCourse_expectedCourseWithId() {

        final Course courseForSave = Course.builder()
                .withCourseName("testCourse")
                .withDescription("testCourse")
                .build();

        Course returnedCourseAfterSave = courseDao.save(courseForSave);

        final Course expectedCourse = Course.builder()
                .withId(5)
                .withCourseName("testCourse")
                .withDescription("testCourse")
                .build();

        assertEquals(expectedCourse, returnedCourseAfterSave);
    }

    @Test
    void save_inputNotCorrectCourseData_expectedException() {

        final Course courseForSave = Course.builder()
                .build();

        assertThatThrownBy(() -> courseDao.save(courseForSave)).isInstanceOf(DataBaseSqlRuntimeException.class);
    }

    @Test
    void saveAll_inputCourses_expectedSavedCoursesInDB() {

        final Course testCourse1 = Course.builder()
                .withCourseName("testCourse1")
                .withDescription("testCourse1")
                .build();

        final Course testCourse2 = Course.builder()
                .withCourseName("testCourse2")
                .withDescription("testCourse2")
                .build();

        List<Course> courses = new ArrayList<>();
        courses.add(testCourse1);
        courses.add(testCourse2);

        courseDao.saveAll(courses);

        List<Course> savedCourses = returnCoursesFromDB("SELECT * FROM school.courses " +
                "WHERE course_name = 'testCourse1' OR course_name = 'testCourse2'");

        final Course expectedCourse0 = Course.builder()
                .withId(5)
                .withCourseName("testCourse1")
                .withDescription("testCourse1")
                .build();

        final Course expectedCourse1 = Course.builder()
                .withId(6)
                .withCourseName("testCourse2")
                .withDescription("testCourse2")
                .build();

        int expectedSize = 2;

        assertEquals(expectedCourse0, savedCourses.get(0));
        assertEquals(expectedCourse1, savedCourses.get(1));
        assertEquals(expectedSize, savedCourses.size());
    }

    @Test
    void saveAll_inputNotCorrectCourseDataList_expectedException() {

        final Course course1 = Course.builder().build();
        final Course course2 = Course.builder().build();

        List<Course> courses = new ArrayList<>();
        courses.add(course1);
        courses.add(course2);

        assertThatThrownBy(() -> courseDao.saveAll(courses)).isInstanceOf(DataBaseSqlRuntimeException.class);
    }

    @Test
    void findById_inputId_expectedCourseWithThisIdFromDB() {

        Optional<Course> optionalCourse = courseDao.findById(1);
        Course foundCourse;
        if(optionalCourse.isPresent()) {
            foundCourse = optionalCourse.get();
            assertEquals(course1, foundCourse);
        }
    }

    @Test
    void findByParam_inputIncorrectId_expectedCourseNotExistInDB() {

        assertFalse(courseDao.findById(10).isPresent());
    }

    @Test
    void findByParam_inputTableNotExist_expectedException() {

        updateDataInDB("DROP TABLE IF EXISTS school.courses CASCADE");

        assertThatThrownBy(() -> courseDao.findById(10)).isInstanceOf(DataBaseSqlRuntimeException.class);
    }

    @Test
    void findAll_inputNothing_expectedAllCoursesFromDB() {

        List<Course> foundCourses = courseDao.findAll();

        int expectedSize = 4;

        assertEquals(course1, foundCourses.get(0));
        assertEquals(course2, foundCourses.get(1));
        assertEquals(course3, foundCourses.get(2));
        assertEquals(course4, foundCourses.get(3));
        assertEquals(expectedSize, foundCourses.size());
    }

    @Test
    void findAll_inputTableNotExist_expectedException() {

        updateDataInDB("DROP TABLE IF EXISTS school.courses CASCADE");

        assertThatThrownBy(() -> courseDao.findAll()).isInstanceOf(DataBaseSqlRuntimeException.class);
    }

    @Test
    void findAllPageable_inputPageOneItemsPerPageTwo_expectedTwoCourses() {

        List<Course> foundCourse = courseDao.findAll(1, 2);

        int expectedSize = 2;

        assertEquals(course3, foundCourse.get(0));
        assertEquals(course4, foundCourse.get(1));
        assertEquals(expectedSize, foundCourse.size());
    }

    @Test
    void findAllPageable_inputTableNotExist_expectedException() {

        updateDataInDB("DROP TABLE IF EXISTS school.courses CASCADE");

        assertThatThrownBy(() -> courseDao.findAll(1, 2))
                .isInstanceOf(DataBaseSqlRuntimeException.class);
    }

    @Test
    void findByCourseName_inputCourseName_expectedCourseWithThisNameFromDB() {

        Optional<Course> optionalCourse = courseDao.findByCourseName("History");
        Course foundCourse;
        if(optionalCourse.isPresent()) {
            foundCourse = optionalCourse.get();
            assertEquals(course1, foundCourse);
        }
    }

    @Test
    void findCoursesByStudentId_inputStudentId_expectedCoursesListRelateToThisStudent() {

        List<Course> foundCourses = courseDao.findCoursesByStudentId(1);

        int expectedSize = 2;

        assertEquals(course1, foundCourses.get(0));
        assertEquals(course2, foundCourses.get(1));
        assertEquals(expectedSize, foundCourses.size());
    }

    @Test
    void findManyByParam_inputTableNotExist_expectedException() {

        updateDataInDB("DROP TABLE IF EXISTS school.courses CASCADE");

        assertThatThrownBy(() -> courseDao.findCoursesByStudentId(1)).isInstanceOf(DataBaseSqlRuntimeException.class);
    }

    @Test
    void update_inputUpdatedCourse_expectedCourseInDBWasUpdated() {

        final Course updatedCourse = Course.builder()
                .withId(1)
                .withCourseName("Updated")
                .withDescription("Updated")
                .build();

        courseDao.update(updatedCourse);

        List<Course> updatedCourseFromDb = returnCoursesFromDB("SELECT * FROM school.courses WHERE id = 1");

        int expectedSize = 1;

        assertEquals(updatedCourse, updatedCourseFromDb.get(0));
        assertEquals(expectedSize, updatedCourseFromDb.size());
    }

    @Test
    void update_inputTableNotExist_expectedException() {

        updateDataInDB("DROP TABLE IF EXISTS school.courses CASCADE");

        assertThatThrownBy(() -> courseDao.update(course1)).isInstanceOf(DataBaseSqlRuntimeException.class);
    }

    @Test
    void deleteById_inputIdOfDeletedCourse_expectedAllCoursesInsteadOfDeleted() {

        courseDao.deleteById(4);

        List<Course> leftCoursesAfterDelete = returnCoursesFromDB("SELECT * FROM school.courses");

        int expectedSize = 3;

        assertEquals(course1, leftCoursesAfterDelete.get(0));
        assertEquals(course2, leftCoursesAfterDelete.get(1));
        assertEquals(course3, leftCoursesAfterDelete.get(2));
        assertEquals(expectedSize, leftCoursesAfterDelete.size());
    }

    @Test
    void deleteCourseById_inputDeleteThreeCourses_expectedAllLeftCoursesInDB() {

        courseDao.deleteCourseById(1);
        courseDao.deleteCourseById(2);
        courseDao.deleteCourseById(3);

        List<Course> leftCoursesAfterDelete = returnCoursesFromDB("SELECT * FROM school.courses");

        int expectedSize = 1;

        assertEquals(course4, leftCoursesAfterDelete.get(0));
        assertEquals(expectedSize, leftCoursesAfterDelete.size());
    }

    @Test
    void deleteCourseById_inputConnectionDBThrowsException_expectedException() throws SQLException {

        doThrow(new SQLException()).when(sutConnectorDB).getConnection();

        assertThatThrownBy(() -> sutCourseDao.deleteCourseById(1)).isInstanceOf(DataBaseSqlRuntimeException.class);

        verify(sutConnectorDB, times(1)).getConnection();
    }

    @Test
    void deleteById_inputTableNotExist_expectedException() {

        updateDataInDB("DROP TABLE IF EXISTS school.courses CASCADE");

        assertThatThrownBy(() -> courseDao.deleteById(1)).isInstanceOf(DataBaseSqlRuntimeException.class);
    }

    @Test
    void count_inputNothing_expectedNumberOfRowsInTable() {

        long numberOfRowsInTable = courseDao.count();

        long expected = 4L;

        assertEquals(expected, numberOfRowsInTable);
    }

    @Test
    void count_inputTableNotExist_expectedException() {

        updateDataInDB("DROP TABLE IF EXISTS school.courses CASCADE");

        assertThatThrownBy(() -> courseDao.count()).isInstanceOf(DataBaseSqlRuntimeException.class);
    }

    @Test
    void count_inputTableIsEmpty_expectedException() {

        updateDataInDB("ALTER TABLE school.student_courses DROP CONSTRAINT fk_courses; " +
                "TRUNCATE TABLE school.courses");

        long expected = 0L;

        assertEquals(expected, courseDao.count());
    }

    @Test
    void setStatementForSave_inputIncorrectPreparedStatement_expectedException() {

        final String QUERY = "INSERT INTO school.courses (course_name) VALUES(?)";

        try (PreparedStatement preparedStatement = connectorDB.getConnection()
                .prepareStatement(QUERY, Statement.RETURN_GENERATED_KEYS)) {
            assertThatThrownBy(() -> courseDao.setStatementForSave(preparedStatement, course1))
                    .isInstanceOf(DataBaseSqlRuntimeException.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void setStatementForUpdate_inputIncorrectPreparedStatement_expectedException() {

        final String QUERY = "UPDATE school.courses SET course_name = ? WHERE id= ?";

        try (PreparedStatement preparedStatement = connectorDB.getConnection()
                .prepareStatement(QUERY, Statement.RETURN_GENERATED_KEYS)) {
            assertThatThrownBy(() -> courseDao.setStatementForUpdate(preparedStatement, course1))
                    .isInstanceOf(DataBaseSqlRuntimeException.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void collectFoundEntitiesToList_inputIncorrectResultSet_expectedException() throws SQLException {

        final String QUERY = "SELECT * FROM school.courses ORDER BY id";

        when(sutConnectorDB.getConnection()).thenReturn(sutConnection);
        when(sutConnection.prepareStatement(QUERY)).thenReturn(sutPreparedStatement);
        doThrow(new SQLException()).when(sutPreparedStatement).executeQuery();

        assertThatThrownBy(() -> sutCourseDao.findAll()).isInstanceOf(DataBaseSqlRuntimeException.class);

        verify(sutConnectorDB, times(1)).getConnection();
        verify(sutConnection, times(1)).prepareStatement(QUERY);
        verify(sutPreparedStatement, times(1)).executeQuery();
    }

    private List<Course> returnCoursesFromDB(String query) {
        List<Course> courses = new ArrayList<>();
        try (final Connection connection = connectorDB.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Course course = Course.builder()
                        .withId(resultSet.getInt("id"))
                        .withCourseName(resultSet.getString("course_name"))
                        .withDescription(resultSet.getString("description"))
                        .build();
                courses.add(course);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return courses;
    }

    private void updateDataInDB(String query) {
        try (final Connection connection = connectorDB.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
