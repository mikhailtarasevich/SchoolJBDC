package com.mikhail.tarasevich.dao.impl;

import com.mikhail.tarasevich.dao.ConnectorDB;
import com.mikhail.tarasevich.dao.ScriptRunner;
import com.mikhail.tarasevich.dao.StudentDao;
import com.mikhail.tarasevich.dao.exception.DataBaseSqlRuntimeException;
import com.mikhail.tarasevich.entity.Course;
import com.mikhail.tarasevich.entity.Group;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupDaoImplTest {

    private static final String SCRIPT_TEST_DB_PATH = "src/test/resources/sql/testDB.SQL";
    private static final String PATH_TO_TEST_DB_PROP = "database";
    private final ConnectorDB connectorDB;
    private final GroupDaoImpl groupDao;
    private final StudentDao studentDao;
    private final ScriptRunner scriptRunner;
    @InjectMocks
    private GroupDaoImpl sutGroupDao;
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
    private static Group group1;
    private static Group group2;
    private static Group group3;
    private static Group group4;
    private static final List<Course> student1Courses = new ArrayList<>();
    private static final List<Course> student2Courses = new ArrayList<>();
    private static final List<Course> student3Courses = new ArrayList<>();
    private static final List<Course> student4Courses = new ArrayList<>();
    private static final List<Student> studentsFromGroup1 = new ArrayList<>();
    private static final List<Student> studentsFromGroup2 = new ArrayList<>();
    private static final List<Student> studentsFromGroup3 = new ArrayList<>();
    private static final List<Student> studentsFromGroup4 = new ArrayList<>();

    {
        connectorDB = new ConnectorDBImpl(PATH_TO_TEST_DB_PROP);
        studentDao = mock(StudentDao.class);
        groupDao = new GroupDaoImpl(connectorDB, studentDao);
        scriptRunner = new ScriptRunnerImpl(connectorDB);

        student1Courses.add(course1);
        student1Courses.add(course2);

        student2Courses.add(course2);
        student2Courses.add(course3);

        student3Courses.add(course1);
        student3Courses.add(course3);

        student4Courses.add(course1);
        student4Courses.add(course2);

        Student student1 = Student.builder()
                .withId(1)
                .withFirstName("John")
                .withLastName("Locke")
                .withGroupId(3)
                .withCoursesList(student1Courses)
                .build();

        Student student2 = Student.builder()
                .withId(2)
                .withFirstName("Jack")
                .withLastName("Shephard")
                .withGroupId(1)
                .withCoursesList(student2Courses)
                .build();

        Student student3 = Student.builder()
                .withId(3)
                .withFirstName("Kate")
                .withLastName("Austin")
                .withGroupId(1)
                .withCoursesList(student3Courses)
                .build();

        Student student4 = Student.builder()
                .withId(4)
                .withFirstName("James")
                .withLastName("Ford")
                .withGroupId(2)
                .withCoursesList(student4Courses)
                .build();

        studentsFromGroup1.add(student2);
        studentsFromGroup1.add(student3);
        studentsFromGroup2.add(student4);
        studentsFromGroup3.add(student1);

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

        group3 = Group.builder()
                .withId(3)
                .withGroupName("go-23")
                .withStudentsList(studentsFromGroup3)
                .build();

        group4 = Group.builder()
                .withId(4)
                .withGroupName("go-24")
                .withStudentsList(new ArrayList<>())
                .build();
    }

    @BeforeEach
    public void runScript() {
        scriptRunner.runScript(SCRIPT_TEST_DB_PATH);
    }

    @Test
    void save_inputGroup_expectedGroupWithId() {

        final Group groupForSave = Group.builder()
                .withGroupName("testGroup")
                .build();

        Group returnedGroupAfterSave = groupDao.save(groupForSave);

        final Group expectedGroup = Group.builder()
                .withId(5)
                .withGroupName("testGroup")
                .build();

        assertEquals(expectedGroup, returnedGroupAfterSave);

        verifyNoInteractions(studentDao);
    }

    @Test
    void save_inputNotCorrectGroupData_expectedException() {

        final Group group1 = Group.builder().build();

        assertThatThrownBy(() -> groupDao.save(group1)).isInstanceOf(DataBaseSqlRuntimeException.class);

        verifyNoInteractions(studentDao);
    }

    @Test
    void saveAll_inputGroups_expectedSavedGroupsInDB() {

        final Group testGroup1 = Group.builder()
                .withGroupName("testGroup1")
                .build();

        final Group testGroup2 = Group.builder()
                .withGroupName("testGroup2")
                .build();

        List<Group> groups = new ArrayList<>();
        groups.add(testGroup1);
        groups.add(testGroup2);

        groupDao.saveAll(groups);

        List<Group> savedGroups = returnGroupsFromDB("SELECT * FROM school.groups " +
                "WHERE group_name = 'testGroup1' OR group_name = 'testGroup2'");

        final Group expectedGroup0 = Group.builder()
                .withId(5)
                .withGroupName("testGroup1")
                .build();

        final Group expectedGroup1 = Group.builder()
                .withId(6)
                .withGroupName("testGroup2")
                .build();

        int expectedSize = 2;

        assertEquals(expectedGroup0, savedGroups.get(0));
        assertEquals(expectedGroup1, savedGroups.get(1));
        assertEquals(expectedSize, savedGroups.size());

        verifyNoInteractions(studentDao);
    }

    @Test
    void saveAll_inputNotCorrectGroupsDataList_expectedException() {

        final Group group1 = Group.builder()
                .build();
        final Group group2 = Group.builder()
                .build();

        List<Group> groups = new ArrayList<>();
        groups.add(group1);
        groups.add(group2);

        assertThatThrownBy(() -> groupDao.saveAll(groups)).isInstanceOf(DataBaseSqlRuntimeException.class);

        verifyNoInteractions(studentDao);
    }

    @Test
    void findById_inputId_expectedGroupWithThisIdFromDB() {

        when(studentDao.findStudentsByGroupId(3)).thenReturn(studentsFromGroup3);

        final Group expectedGroup = group3;

        Optional<Group> optionalGroup = groupDao.findById(3);
        Group foundGroup;
        if(optionalGroup.isPresent()){
            foundGroup = optionalGroup.get();
            assertEquals(expectedGroup, foundGroup);
            verify(studentDao, times(1)).findStudentsByGroupId(3);
        }
    }

    @Test
    void findByParam_inputIncorrectId_expectedGroupNotExistInDB() {

        assertFalse(groupDao.findById(10).isPresent());

        verifyNoInteractions(studentDao);
    }

    @Test
    void findByParam_inputTableNotExist_expectedException() {

        updateDataInDB("DROP TABLE IF EXISTS school.groups CASCADE");

        assertThatThrownBy(() -> groupDao.findById(10)).isInstanceOf(DataBaseSqlRuntimeException.class);

        verifyNoInteractions(studentDao);
    }

    @Test
    void findAll_inputNothing_expectedAllGroupsFromDB() {

        when(studentDao.findStudentsByGroupId(1)).thenReturn(studentsFromGroup1);
        when(studentDao.findStudentsByGroupId(2)).thenReturn(studentsFromGroup2);
        when(studentDao.findStudentsByGroupId(3)).thenReturn(studentsFromGroup3);
        when(studentDao.findStudentsByGroupId(4)).thenReturn(studentsFromGroup4);

        List<Group> foundGroups = groupDao.findAll();

        final Group expectedGroup0 = group1;
        final Group expectedGroup1 = group2;
        final Group expectedGroup2 = group3;
        final Group expectedGroup3 = group4;

        int expectedSize = 4;

        assertEquals(expectedGroup0, foundGroups.get(0));
        assertEquals(expectedGroup1, foundGroups.get(1));
        assertEquals(expectedGroup2, foundGroups.get(2));
        assertEquals(expectedGroup3, foundGroups.get(3));
        assertEquals(expectedSize, foundGroups.size());

        verify(studentDao, times(1)).findStudentsByGroupId(1);
        verify(studentDao, times(1)).findStudentsByGroupId(2);
        verify(studentDao, times(1)).findStudentsByGroupId(3);
        verify(studentDao, times(1)).findStudentsByGroupId(4);
    }

    @Test
    void findAll_inputTableNotExist_expectedException() {

        updateDataInDB("DROP TABLE IF EXISTS school.groups CASCADE");

        assertThatThrownBy(() -> groupDao.findAll()).isInstanceOf(DataBaseSqlRuntimeException.class);

        verifyNoInteractions(studentDao);
    }

    @Test
    void findAllPageable_inputPageOneItemsPerPageTwo_expectedTwoGroups() {

        when(studentDao.findStudentsByGroupId(3)).thenReturn(studentsFromGroup3);
        when(studentDao.findStudentsByGroupId(4)).thenReturn(studentsFromGroup4);

        List<Group> foundGroups = groupDao.findAll(1, 2);

        final Group expectedGroup0 = group3;
        final Group expectedGroup1 = group4;

        int expectedSize = 2;

        assertEquals(expectedGroup0, foundGroups.get(0));
        assertEquals(expectedGroup1, foundGroups.get(1));
        assertEquals(expectedSize, foundGroups.size());

        verify(studentDao, times(1)).findStudentsByGroupId(3);
        verify(studentDao, times(1)).findStudentsByGroupId(4);
    }

    @Test
    void findAllPageable_inputTableNotExist_expectedException() {

        updateDataInDB("DROP TABLE IF EXISTS school.groups CASCADE");

        assertThatThrownBy(() -> groupDao.findAll(1, 2))
                .isInstanceOf(DataBaseSqlRuntimeException.class);

        verifyNoInteractions(studentDao);
    }

    @Test
    void findByGroupName_inputGroupName_expectedGroupWithThisNameFromDB() {

        when(studentDao.findStudentsByGroupId(2)).thenReturn(studentsFromGroup2);

        final Group expectedGroup = group2;

        Optional<Group> optionalGroup = groupDao.findByGroupName("go-22");
        Group foundGroup;
        if(optionalGroup.isPresent()){
            foundGroup = optionalGroup.get();
            assertEquals(expectedGroup, foundGroup);
            verify(studentDao, times(1)).findStudentsByGroupId(2);
        }
    }

    @Test
    void findGroupByStudentId_inputStudentId_expectedGroupWhichRelateThisStudent() {

        when(studentDao.findStudentsByGroupId(3)).thenReturn(studentsFromGroup3);

        final Group expectedGroup = group3;

        Optional<Group> optionalGroup = groupDao.findGroupByStudentId(1);
        Group foundGroup;
        if(optionalGroup.isPresent()){
            foundGroup = optionalGroup.get();
            assertEquals(expectedGroup, foundGroup);
            verify(studentDao, times(1)).findStudentsByGroupId(3);
        }
    }

    @Test
    void findGroupsWithLessEqualCountOfStudents_inputCountOfStudent_expectedListOfGroups() {

        when(studentDao.findStudentsByGroupId(2)).thenReturn(studentsFromGroup2);
        when(studentDao.findStudentsByGroupId(3)).thenReturn(studentsFromGroup3);
        when(studentDao.findStudentsByGroupId(4)).thenReturn(studentsFromGroup4);

        List<Group> foundGroups = groupDao.findGroupsWithLessEqualCountOfStudents(1);

        final Group expectedGroup0 = group2;
        final Group expectedGroup1 = group3;
        final Group expectedGroup2 = group4;

        int expectedSize = 3;

        assertEquals(expectedGroup0, foundGroups.get(0));
        assertEquals(expectedGroup1, foundGroups.get(1));
        assertEquals(expectedGroup2, foundGroups.get(2));
        assertEquals(expectedSize, foundGroups.size());

        verify(studentDao, times(1)).findStudentsByGroupId(2);
        verify(studentDao, times(1)).findStudentsByGroupId(3);
        verify(studentDao, times(1)).findStudentsByGroupId(4);
    }

    @Test
    void findManyByParam_inputTableNotExist_expectedException() {

        updateDataInDB("DROP TABLE IF EXISTS school.students CASCADE");

        assertThatThrownBy(() -> groupDao.findGroupsWithLessEqualCountOfStudents(1))
                .isInstanceOf(DataBaseSqlRuntimeException.class);

        verifyNoInteractions(studentDao);
    }

    @Test
    void update_inputUpdatedGroup_expectedGroupInDBWasUpdated() {

        final Group updatedGroup = Group.builder()
                .withId(2)
                .withGroupName("updatedGroupName")
                .build();

        groupDao.update(updatedGroup);

        List<Group> updatedGroupFromDB = returnGroupsFromDB("SELECT * FROM school.groups WHERE id = 2");

        int expectedSize = 1;

        assertEquals(updatedGroup, updatedGroupFromDB.get(0));
        assertEquals(expectedSize, updatedGroupFromDB.size());

        verifyNoInteractions(studentDao);
    }

    @Test
    void update_inputTableNotExist_expectedException() {

        updateDataInDB("DROP TABLE IF EXISTS school.groups CASCADE");

        assertThatThrownBy(() -> groupDao.update(group1)).isInstanceOf(DataBaseSqlRuntimeException.class);

        verifyNoInteractions(studentDao);
    }

    @Test
    void deleteById_inputIdOfDeletedGroup_expectedAllGroupsInsteadOfDeleted() {

        groupDao.deleteById(4);

        List<Group> leftGroupsAfterDelete = returnGroupsFromDB("SELECT * FROM school.groups");

        final Group expectedGroup0 = group1;
        final Group expectedGroup1 = group2;
        final Group expectedGroup2 = group3;

        int expectedSize = 3;

        assertEquals(expectedGroup0.getId(), leftGroupsAfterDelete.get(0).getId());
        assertEquals(expectedGroup0.getGroupName(), leftGroupsAfterDelete.get(0).getGroupName());
        assertEquals(expectedGroup1.getId(), leftGroupsAfterDelete.get(1).getId());
        assertEquals(expectedGroup1.getGroupName(), leftGroupsAfterDelete.get(1).getGroupName());
        assertEquals(expectedGroup2.getId(), leftGroupsAfterDelete.get(2).getId());
        assertEquals(expectedGroup2.getGroupName(), leftGroupsAfterDelete.get(2).getGroupName());
        assertEquals(expectedSize, leftGroupsAfterDelete.size());

        verifyNoInteractions(studentDao);
    }

    @Test
    void deleteGroupById_inputDeleteThreeGroups_expectedAllLeftGroupsInDB() {

        groupDao.deleteGroupById(1);
        groupDao.deleteGroupById(2);
        groupDao.deleteGroupById(3);

        List<Group> leftGroupsAfterDelete = returnGroupsFromDB("SELECT * FROM school.groups");

        final Group expectedGroup = group4;

        int expectedSize = 1;

        assertEquals(expectedGroup.getId(), leftGroupsAfterDelete.get(0).getId());
        assertEquals(expectedGroup.getGroupName(), leftGroupsAfterDelete.get(0).getGroupName());
        assertEquals(expectedSize, leftGroupsAfterDelete.size());

        verifyNoInteractions(studentDao);
    }

    @Test
    void deleteGroupById_inputConnectionDBThrowsException_expectedException() throws SQLException {

        doThrow(new SQLException()).when(sutConnectorDB).getConnection();

        assertThatThrownBy(() -> sutGroupDao.deleteGroupById(1)).isInstanceOf(DataBaseSqlRuntimeException.class);

        verify(sutConnectorDB, times(1)).getConnection();
    }

    @Test
    void deleteById_inputTableNotExist_expectedException() {

        updateDataInDB("DROP TABLE IF EXISTS school.groups CASCADE");

        assertThatThrownBy(() -> groupDao.deleteById(1)).isInstanceOf(DataBaseSqlRuntimeException.class);

        verifyNoInteractions(studentDao);
    }

    @Test
    void count_inputNothing_expectedNumberOfRowsInTable() {

        long numberOfRowsInTable = groupDao.count();

        long expected = 4L;

        assertEquals(expected, numberOfRowsInTable);

        verifyNoInteractions(studentDao);
    }

    @Test
    void count_inputTableNotExist_expectedException() {

        updateDataInDB("DROP TABLE IF EXISTS school.groups CASCADE");

        assertThatThrownBy(() -> groupDao.count()).isInstanceOf(DataBaseSqlRuntimeException.class);

        verifyNoInteractions(studentDao);
    }

    @Test
    void count_inputTableIsEmpty_expectedException() {

        updateDataInDB("ALTER TABLE school.students DROP CONSTRAINT fk_groups; " +
                "TRUNCATE TABLE school.groups");

        long expected = 0L;

        assertEquals(expected, groupDao.count());

        verifyNoInteractions(studentDao);
    }

    @Test
    void setStatementForSave_inputIncorrectPreparedStatement_expectedException() {

        final String QUERY = "INSERT INTO school.groups (group_name) VALUES(1)";

        try (PreparedStatement preparedStatement = connectorDB.getConnection()
                .prepareStatement(QUERY, Statement.RETURN_GENERATED_KEYS)) {
            assertThatThrownBy(() -> groupDao.setStatementForSave(preparedStatement, group1))
                    .isInstanceOf(DataBaseSqlRuntimeException.class);
            verifyNoInteractions(studentDao);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void setStatementForUpdate_inputIncorrectPreparedStatement_expectedException() {

        final String QUERY = "UPDATE school.groups SET group_name = 1 WHERE id = 1";

        try (PreparedStatement preparedStatement = connectorDB.getConnection()
                .prepareStatement(QUERY, Statement.RETURN_GENERATED_KEYS)) {
            assertThatThrownBy(() -> groupDao.setStatementForUpdate(preparedStatement, group1))
                    .isInstanceOf(DataBaseSqlRuntimeException.class);
            verifyNoInteractions(studentDao);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Group> returnGroupsFromDB(String query) {
        List<Group> groups = new ArrayList<>();
        try (final Connection connection = connectorDB.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Group group = Group.builder()
                        .withId(resultSet.getInt("id"))
                        .withGroupName(resultSet.getString("group_name"))
                        .build();
                groups.add(group);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return groups;
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
