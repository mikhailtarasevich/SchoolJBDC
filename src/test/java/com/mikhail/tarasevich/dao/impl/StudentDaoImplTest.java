package com.mikhail.tarasevich.dao.impl;

import com.mikhail.tarasevich.dao.ConnectorDB;
import com.mikhail.tarasevich.dao.CourseDao;
import com.mikhail.tarasevich.dao.ScriptRunner;
import com.mikhail.tarasevich.dao.exception.DataBaseSqlRuntimeException;
import com.mikhail.tarasevich.entity.Course;
import com.mikhail.tarasevich.entity.Student;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentDaoImplTest {

    private static final String SCRIPT_TEST_DB_PATH = "src/test/resources/sql/testDB.SQL";
    private static final String PATH_TO_TEST_DB_PROP = "database";
    private final ConnectorDB connectorDB;
    private final StudentDaoImpl studentDao;
    private final CourseDao courseDao;
    private final ScriptRunner scriptRunner;
    @InjectMocks
    private StudentDaoImpl sutStudentDao;
    @Mock
    private ConnectorDB sutConnectorDB;
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
    private static Student student1;
    private static Student student2;
    private static Student student3;
    private static Student student4;
    private static Student student5;
    private static final List<Course> student1Courses = new ArrayList<>();
    private static final List<Course> student2Courses = new ArrayList<>();
    private static final List<Course> student3Courses = new ArrayList<>();
    private static final List<Course> student4Courses = new ArrayList<>();

    {
        connectorDB = new ConnectorDBImpl(PATH_TO_TEST_DB_PROP);
        courseDao = mock(CourseDao.class);
        studentDao = new StudentDaoImpl(connectorDB, courseDao);
        scriptRunner = new ScriptRunnerImpl(connectorDB);

        student1Courses.add(course1);
        student1Courses.add(course2);

        student2Courses.add(course2);
        student2Courses.add(course3);

        student3Courses.add(course1);
        student3Courses.add(course3);

        student4Courses.add(course1);
        student4Courses.add(course2);

        student1 = Student.builder()
                .withId(1)
                .withFirstName("John")
                .withLastName("Locke")
                .withGroupId(3)
                .withCoursesList(student1Courses)
                .build();

        student2 = Student.builder()
                .withId(2)
                .withFirstName("Jack")
                .withLastName("Shephard")
                .withGroupId(1)
                .withCoursesList(student2Courses)
                .build();

        student3 = Student.builder()
                .withId(3)
                .withFirstName("Kate")
                .withLastName("Austin")
                .withGroupId(1)
                .withCoursesList(student3Courses)
                .build();

        student4 = Student.builder()
                .withId(4)
                .withFirstName("James")
                .withLastName("Ford")
                .withGroupId(2)
                .withCoursesList(student4Courses)
                .build();

        student5 = Student.builder()
                .withId(5)
                .withFirstName("Leonardo")
                .withLastName("DiCaprio")
                .build();
    }

    @BeforeEach
    public void runScript() {
        scriptRunner.runScript(SCRIPT_TEST_DB_PATH);
    }

    @Test
    void save_inputStudent_expectedStudentWithId() {

        final Student studentForSave = Student.builder()
                .withFirstName("testStudent")
                .withLastName("testStudent")
                .withGroupId(1)
                .withCoursesList(new ArrayList<>())
                .build();

        Student returnedStudentAfterSave = studentDao.save(studentForSave);

        final Student expectedStudent = Student.builder()
                .withId(6)
                .withFirstName("testStudent")
                .withLastName("testStudent")
                .withGroupId(1)
                .withCoursesList(new ArrayList<>())
                .build();

        assertEquals(expectedStudent, returnedStudentAfterSave);

        verifyNoInteractions(courseDao);
    }

    @Test
    void save_inputNotCorrectStudentData_expectedException() {

        final Student studentForSave = Student.builder().build();

        assertThatThrownBy(() -> studentDao.save(studentForSave)).isInstanceOf(DataBaseSqlRuntimeException.class);

        verifyNoInteractions(courseDao);
    }

    @Test
    void saveAll_inputGroups_expectedSavedGroupsInDB() {

        final Student testStudent1 = Student.builder()
                .withFirstName("testStudent1")
                .withLastName("testStudent1")
                .withGroupId(1)
                .withCoursesList(new ArrayList<>())
                .build();
        final Student testStudent2 = Student.builder()
                .withFirstName("testStudent2")
                .withLastName("testStudent2")
                .withGroupId(4)
                .withCoursesList(new ArrayList<>())
                .build();

        List<Student> students = new ArrayList<>();
        students.add(testStudent1);
        students.add(testStudent2);

        studentDao.saveAll(students);

        List<Student> savedStudents = returnStudentsFromDB("SELECT * FROM school.students " +
                "WHERE first_name = 'testStudent1' OR first_name = 'testStudent2'");

        final Student expectedStudent0 = Student.builder()
                .withId(6)
                .withFirstName("testStudent1")
                .withLastName("testStudent1")
                .withGroupId(1)
                .withCoursesList(new ArrayList<>())
                .build();
        final Student expectedStudent1 = Student.builder()
                .withId(7)
                .withFirstName("testStudent2")
                .withLastName("testStudent2")
                .withGroupId(4)
                .withCoursesList(new ArrayList<>())
                .build();

        int expectedSize = 2;

        assertEquals(expectedStudent0, savedStudents.get(0));
        assertEquals(expectedStudent1, savedStudents.get(1));
        assertEquals(expectedSize, savedStudents.size());

        verifyNoInteractions(courseDao);
    }

    @Test
    void saveAll_inputNotCorrectStudentsDataList_expectedException() {

        final Student student1 = Student.builder()
                .build();
        final Student student2 = Student.builder()
                .build();

        List<Student> students = new ArrayList<>();
        students.add(student1);
        students.add(student2);

        assertThatThrownBy(() -> studentDao.saveAll(students)).isInstanceOf(DataBaseSqlRuntimeException.class);

        verifyNoInteractions(courseDao);
    }

    @Test
    void subscribeStudentToCourses_inputStudentWithCourses_expectedStudentWithCoursesInDB() {

        List<Course> newStudentCourses = new ArrayList<>();
        newStudentCourses.add(course1);
        newStudentCourses.add(course2);

        final Student student = Student.builder()
                .withId(5)
                .withFirstName("Leonardo")
                .withLastName("DiCaprio")
                .withCoursesList(newStudentCourses)
                .build();

        studentDao.subscribeStudentToCourses(student);

        List<Integer> studentCourses = new ArrayList<>();
        try (Connection connection = connectorDB.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT *" +
                    "FROM school.student_courses WHERE student_id = 5 ORDER BY course_id");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                studentCourses.add(resultSet.getInt("course_id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        int expectedCourseId1 = 1;
        int expectedCourseId2 = 2;
        int expectedSize = 2;

        assertEquals(expectedCourseId1, studentCourses.get(0));
        assertEquals(expectedCourseId2, studentCourses.get(1));
        assertEquals(expectedSize, studentCourses.size());

        verifyNoInteractions(courseDao);
    }

    @Test
    void subscribeStudentToCourses_inputCourseIdDoesNotExistInDB_expectedException() {

        Course course = Course.builder().withId(10).build();

        List<Course> newStudentCourses = new ArrayList<>();
        newStudentCourses.add(course);

        final Student student = Student.builder()
                .withId(5)
                .withFirstName("Leonardo")
                .withLastName("DiCaprio")
                .withCoursesList(newStudentCourses)
                .build();

        assertThatThrownBy(() -> studentDao.subscribeStudentToCourses(student))
                .isInstanceOf(DataBaseSqlRuntimeException.class);

        verifyNoInteractions(courseDao);
    }

    @Test
    void subscribeStudentToCourse_inputStudentIdCourseId_expectedStudentWithCourseInDB() {

        studentDao.subscribeStudentToCourse(student5.getId(), course1.getId());

        List<Integer> studentCourses = new ArrayList<>();
        try (Connection connection = connectorDB.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT *" +
                    "FROM school.student_courses WHERE student_id = 5 ORDER BY course_id");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                studentCourses.add(resultSet.getInt("course_id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        int expectedCourseId1 = 1;
        int expectedSize = 1;

        assertEquals(expectedCourseId1, studentCourses.get(0));
        assertEquals(expectedSize, studentCourses.size());

        verifyNoInteractions(courseDao);
    }

    @Test
    void subscribeStudentToCourse_inputCourseIdDoesNotExistInDB_expectedException() {

        assertThatThrownBy(() -> studentDao.subscribeStudentToCourse(student5.getId(), 10))
                .isInstanceOf(DataBaseSqlRuntimeException.class);

        verifyNoInteractions(courseDao);
    }

    @Test
    void findById_inputId_expectedStudentWithThisIdFromDB() {

        when(courseDao.findCoursesByStudentId(4)).thenReturn(student4Courses);

        final Student expectedStudent = student4;

        Optional<Student> optionalStudent = studentDao.findById(4);
        Student foundStudent;
        if(optionalStudent.isPresent()) {
            foundStudent = optionalStudent.get();
            assertEquals(expectedStudent, foundStudent);
            verify(courseDao, times(1)).findCoursesByStudentId(4);
        }
    }

    @Test
    void findByParam_inputIncorrectId_expectedStudentNotExistInDB() {

        assertFalse(studentDao.findById(10).isPresent());

        verifyNoInteractions(courseDao);
    }

    @Test
    void findByParam_inputTableNotExist_expectedException() {

        updateDataInDB("DROP TABLE IF EXISTS school.students CASCADE");

        assertThatThrownBy(() -> studentDao.findById(10)).isInstanceOf(DataBaseSqlRuntimeException.class);

        verifyNoInteractions(courseDao);
    }

    @Test
    void findAll_inputNothing_expectedAllStudentsFromDB() {

        when(courseDao.findCoursesByStudentId(1)).thenReturn(student1Courses);
        when(courseDao.findCoursesByStudentId(2)).thenReturn(student2Courses);
        when(courseDao.findCoursesByStudentId(3)).thenReturn(student3Courses);
        when(courseDao.findCoursesByStudentId(4)).thenReturn(student4Courses);

        List<Student> foundStudents = studentDao.findAll();

        final Student expectedStudent0 = student1;
        final Student expectedStudent1 = student2;
        final Student expectedStudent2 = student3;
        final Student expectedStudent3 = student4;
        final Student expectedStudent4 = student5;

        int expectedSize = 5;

        assertEquals(expectedStudent0, foundStudents.get(0));
        assertEquals(expectedStudent1, foundStudents.get(1));
        assertEquals(expectedStudent2, foundStudents.get(2));
        assertEquals(expectedStudent3, foundStudents.get(3));
        assertEquals(expectedStudent4.getId(), foundStudents.get(4).getId());
        assertEquals(expectedStudent4.getFirstName(), foundStudents.get(4).getFirstName());
        assertEquals(expectedStudent4.getLastName(), foundStudents.get(4).getLastName());
        assertEquals(expectedSize, foundStudents.size());

        verify(courseDao, times(1)).findCoursesByStudentId(1);
        verify(courseDao, times(1)).findCoursesByStudentId(2);
        verify(courseDao, times(1)).findCoursesByStudentId(3);
        verify(courseDao, times(1)).findCoursesByStudentId(4);
        verify(courseDao, times(1)).findCoursesByStudentId(5);
    }

    @Test
    void findAll_inputTableNotExist_expectedException() {

        updateDataInDB("DROP TABLE IF EXISTS school.students CASCADE");

        assertThatThrownBy(() -> studentDao.findAll()).isInstanceOf(DataBaseSqlRuntimeException.class);

        verifyNoInteractions(courseDao);
    }

    @Test
    void findAllPageable_inputPageOneItemsPerPageTwo_expectedTwoStudents() {

        when(courseDao.findCoursesByStudentId(1)).thenReturn(student1Courses);
        when(courseDao.findCoursesByStudentId(2)).thenReturn(student2Courses);

        List<Student> foundStudents = studentDao.findAll(0, 2);

        int expectedSize = 2;

        Student expectedStudent0 = student1;
        Student expectedStudent1 = student2;

        assertEquals(expectedStudent0, foundStudents.get(0));
        assertEquals(expectedStudent1, foundStudents.get(1));
        assertEquals(expectedSize, foundStudents.size());

        verify(courseDao, times(1)).findCoursesByStudentId(1);
        verify(courseDao, times(1)).findCoursesByStudentId(2);
    }

    @Test
    void findAllPageable_inputTableNotExist_expectedException() {

        try (Connection connection = connectorDB.getConnection()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("DROP TABLE IF EXISTS school.students CASCADE");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        assertThatThrownBy(() -> studentDao.findAll(0, 2))
                .isInstanceOf(DataBaseSqlRuntimeException.class);

        verifyNoInteractions(courseDao);
    }

    @Test
    void findByFirstName_inputStudentFirstName_expectedStudentWithThisNameFromDB() {

        when(courseDao.findCoursesByStudentId(3)).thenReturn(student3Courses);

        List<Student> foundStudents = studentDao.findByFirstName("Kate");

        final Student expectedStudent = student3;

        int expectedSize = 1;

        assertEquals(expectedStudent, foundStudents.get(0));
        assertEquals(expectedSize, foundStudents.size());

        verify(courseDao, times(1)).findCoursesByStudentId(3);
    }

    @Test
    void findManyByParam_inputTableNotExist_expectedException() {

        updateDataInDB("DROP TABLE IF EXISTS school.students CASCADE");

        assertThatThrownBy(() -> studentDao.findByFirstName("Kate")).isInstanceOf(DataBaseSqlRuntimeException.class);

        verifyNoInteractions(courseDao);
    }

    @Test
    void findStudentsRelatedToCourse_inputCourseId_expectedStudentsRelatedToCourseFromDB() {

        when(courseDao.findCoursesByStudentId(1)).thenReturn(student1Courses);
        when(courseDao.findCoursesByStudentId(3)).thenReturn(student3Courses);
        when(courseDao.findCoursesByStudentId(4)).thenReturn(student4Courses);

        List<Student> foundStudents = studentDao.findStudentsRelatedToCourse(1);

        final Student expectedStudent0 = student1;
        final Student expectedStudent1 = student3;
        final Student expectedStudent2 = student4;

        int expectedSize = 3;

        assertEquals(expectedStudent0, foundStudents.get(0));
        assertEquals(expectedStudent1, foundStudents.get(1));
        assertEquals(expectedStudent2, foundStudents.get(2));
        assertEquals(expectedSize, foundStudents.size());

        verify(courseDao, times(1)).findCoursesByStudentId(1);
        verify(courseDao, times(1)).findCoursesByStudentId(3);
        verify(courseDao, times(1)).findCoursesByStudentId(4);
    }

    @Test
    void findStudentsByGroupId_inputGroupId_expectedStudentsRelatedToGroupFromDB() {

        when(courseDao.findCoursesByStudentId(2)).thenReturn(student2Courses);
        when(courseDao.findCoursesByStudentId(3)).thenReturn(student3Courses);

        List<Student> foundStudents = studentDao.findStudentsByGroupId(1);

        final Student expectedStudent0 = student2;
        final Student expectedStudent1 = student3;

        int expectedSize = 2;

        assertEquals(expectedStudent0, foundStudents.get(0));
        assertEquals(expectedStudent1, foundStudents.get(1));
        assertEquals(expectedSize, foundStudents.size());

        verify(courseDao, times(1)).findCoursesByStudentId(2);
        verify(courseDao, times(1)).findCoursesByStudentId(3);
    }

    @Test
    void update_inputUpdatedStudent_expectedStudentInDBWasUpdated() {

        final Student updatedStudent = Student.builder()
                .withId(5)
                .withFirstName("updatedStudent")
                .withLastName("updatedStudent")
                .withGroupId(2)
                .withCoursesList(new ArrayList<>())
                .build();

        studentDao.update(updatedStudent);

        List<Student> updatedStudentFromDB = returnStudentsFromDB("SELECT * FROM school.students WHERE id = 5");

        int expectedSize = 1;

        assertEquals(updatedStudent, updatedStudentFromDB.get(0));
        assertEquals(expectedSize, updatedStudentFromDB.size());

        verifyNoInteractions(courseDao);
    }

    @Test
    void update_inputTableNotExist_expectedException() {

        updateDataInDB("DROP TABLE IF EXISTS school.students CASCADE");

        assertThatThrownBy(() -> studentDao.update(student1)).isInstanceOf(DataBaseSqlRuntimeException.class);

        verifyNoInteractions(courseDao);
    }

    @Test
    void deleteById_inputIdOfDeletedStudent_expectedAllStudentsInsteadOfDeleted() {

        studentDao.deleteById(5);

        List<Student> leftStudentsAfterDelete = returnStudentsFromDB("SELECT * FROM school.students ORDER BY id");

        final Student expectedStudent0 = student1;
        final Student expectedStudent1 = student2;
        final Student expectedStudent2 = student3;
        final Student expectedStudent3 = student4;

        int expectedSize = 4;

        assertEquals(expectedStudent0.getId(), leftStudentsAfterDelete.get(0).getId());
        assertEquals(expectedStudent0.getFirstName(), leftStudentsAfterDelete.get(0).getFirstName());
        assertEquals(expectedStudent0.getLastName(), leftStudentsAfterDelete.get(0).getLastName());
        assertEquals(expectedStudent1.getId(), leftStudentsAfterDelete.get(1).getId());
        assertEquals(expectedStudent1.getFirstName(), leftStudentsAfterDelete.get(1).getFirstName());
        assertEquals(expectedStudent1.getLastName(), leftStudentsAfterDelete.get(1).getLastName());
        assertEquals(expectedStudent2.getId(), leftStudentsAfterDelete.get(2).getId());
        assertEquals(expectedStudent2.getFirstName(), leftStudentsAfterDelete.get(2).getFirstName());
        assertEquals(expectedStudent2.getLastName(), leftStudentsAfterDelete.get(2).getLastName());
        assertEquals(expectedStudent3.getId(), leftStudentsAfterDelete.get(3).getId());
        assertEquals(expectedStudent3.getFirstName(), leftStudentsAfterDelete.get(3).getFirstName());
        assertEquals(expectedStudent3.getLastName(), leftStudentsAfterDelete.get(3).getLastName());
        assertEquals(expectedSize, leftStudentsAfterDelete.size());

        verifyNoInteractions(courseDao);
    }

    @Test
    void deleteStudentById_inputDeleteFourStudents_expectedAllLeftStudentInDB() {

        studentDao.deleteStudentById(1);
        studentDao.deleteStudentById(2);
        studentDao.deleteStudentById(3);
        studentDao.deleteStudentById(4);

        List<Student> leftStudentsAfterDelete = returnStudentsFromDB("SELECT * FROM school.students");

        final Student expectedStudent = student5;

        int expectedSize = 1;

        assertEquals(expectedStudent.getId(), leftStudentsAfterDelete.get(0).getId());
        assertEquals(expectedStudent.getFirstName(), leftStudentsAfterDelete.get(0).getFirstName());
        assertEquals(expectedStudent.getLastName(), leftStudentsAfterDelete.get(0).getLastName());
        assertEquals(expectedStudent.getGroupId(), leftStudentsAfterDelete.get(0).getGroupId());
        assertEquals(expectedSize, leftStudentsAfterDelete.size());

        verifyNoInteractions(courseDao);
    }

    @Test
    void deleteById_inputTableNotExist_expectedException() {

        updateDataInDB("DROP TABLE IF EXISTS school.students CASCADE");

        assertThatThrownBy(() -> studentDao.deleteById(1)).isInstanceOf(DataBaseSqlRuntimeException.class);

        verifyNoInteractions(courseDao);
    }

    @Test
    void removeStudentFromGroup_inputStudentId_expectedStudentGroupIsZero() {

        studentDao.removeStudentFromGroup(1);

        List<Student> leftStudentsAfterDelete = returnStudentsFromDB("SELECT * FROM school.students WHERE id =1");

        int expectedSize = 1;

        assertEquals(0, leftStudentsAfterDelete.get(0).getGroupId());
        assertEquals(expectedSize, leftStudentsAfterDelete.size());

        verifyNoInteractions(courseDao);
    }

    @Test
    void removeStudentFromGroup_inputConnectorDBThrowsException_expectedException() throws SQLException {

        doThrow(new SQLException()).when(sutConnectorDB).getConnection();

        assertThatThrownBy(() -> sutStudentDao.removeStudentFromGroup(1))
                .isInstanceOf(DataBaseSqlRuntimeException.class);

        verify(sutConnectorDB, times(1)).getConnection();
    }

    @Test
    void removeStudentFromCourse_inputStudentAndCoursesId_expectedStudentHasNoCourses() {

        studentDao.removeStudentFromCourse(1, 1);
        studentDao.removeStudentFromCourse(1, 2);

        List<Student> leftStudentsAfterDelete = returnStudentsFromDB("SELECT * " +
                "FROM school.student_courses WHERE student_id =1");

        assertTrue(leftStudentsAfterDelete.isEmpty());

        verifyNoInteractions(courseDao);
    }

    @Test
    void removeStudentFromCourse_inputConnectorDBThrowsException_expectedException() throws SQLException {

        doThrow(new SQLException()).when(sutConnectorDB).getConnection();

        assertThatThrownBy(() -> sutStudentDao.removeStudentFromCourse(1, 1))
                .isInstanceOf(DataBaseSqlRuntimeException.class);

        verify(sutConnectorDB, times(1)).getConnection();
    }

    @Test
    void count_inputNothing_expectedNumberOfRowsInTable() {

        long numberOfRowsInTable = studentDao.count();

        long expected = 5L;

        assertEquals(expected, numberOfRowsInTable);

        verifyNoInteractions(courseDao);
    }

    @Test
    void count_inputTableNotExist_expectedException() {

        updateDataInDB("DROP TABLE IF EXISTS school.students CASCADE");

        assertThatThrownBy(() -> studentDao.count()).isInstanceOf(DataBaseSqlRuntimeException.class);

        verifyNoInteractions(courseDao);
    }

    @Test
    void count_inputTableIsEmpty_expectedZero() {

        updateDataInDB("ALTER TABLE school.student_courses DROP CONSTRAINT fk_students; " +
                "TRUNCATE TABLE school.students");

        long expected = 0L;

        assertEquals(expected, studentDao.count());

        verifyNoInteractions(courseDao);
    }

    @Test
    void setStatementForSave_inputIncorrectPreparedStatement_expectedException() {

        final String QUERY = "INSERT INTO school.students (first_name, last_name) VALUES(?, ?)";

        try (PreparedStatement preparedStatement = connectorDB.getConnection()
                .prepareStatement(QUERY, Statement.RETURN_GENERATED_KEYS)) {
            assertThatThrownBy(() -> studentDao.setStatementForSave(preparedStatement, student1))
                    .isInstanceOf(DataBaseSqlRuntimeException.class);
            verifyNoInteractions(courseDao);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void setStatementForUpdate_inputIncorrectResultSt_expectedException() {

        final String QUERY = "UPDATE school.students SET first_name = ?, last_name = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connectorDB.getConnection()
                .prepareStatement(QUERY, Statement.RETURN_GENERATED_KEYS)) {
            assertThatThrownBy(() -> studentDao.setStatementForUpdate(preparedStatement, student1))
                    .isInstanceOf(DataBaseSqlRuntimeException.class);
            verifyNoInteractions(courseDao);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Student> returnStudentsFromDB(String query) {
        List<Student> students = new ArrayList<>();
        try (final Connection connection = connectorDB.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Student student = Student.builder()
                        .withId(resultSet.getInt("id"))
                        .withFirstName(resultSet.getString("first_name"))
                        .withLastName(resultSet.getString("last_name"))
                        .withGroupId(resultSet.getInt("group_id"))
                        .withCoursesList(new ArrayList<>())
                        .build();
                students.add(student);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return students;
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
