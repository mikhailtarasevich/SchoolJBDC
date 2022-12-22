package com.mikhail.tarasevich.dao.impl;

import com.google.inject.Inject;
import com.mikhail.tarasevich.dao.ConnectorDB;
import com.mikhail.tarasevich.dao.GroupDao;
import com.mikhail.tarasevich.dao.StudentDao;
import com.mikhail.tarasevich.dao.exception.DataBaseSqlRuntimeException;
import com.mikhail.tarasevich.entity.Group;
import com.mikhail.tarasevich.entity.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class GroupDaoImpl extends AbstractPageableCrudDaoImpl<Group> implements GroupDao {

    private static final Logger LOG = LoggerFactory.getLogger(StudentDaoImpl.class);
    private static final String SAVE_GROUP_QUERY = "INSERT INTO school.groups (group_name) VALUES(?)";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM school.groups WHERE id=?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM school.groups ORDER BY id";
    private static final String FIND_ALL_PAGEABLE_QUERY = "SELECT * FROM school.groups ORDER BY id LIMIT ? OFFSET ?";
    private static final String FIND_BY_GROUP_NAME_QUERY = "SELECT * FROM school.groups WHERE group_name=?";
    private static final String FIND_GROUP_BY_STUDENT_ID_QUERY =
            "SELECT groups.id, groups.group_name\n" +
                    "FROM school.groups\n" +
                    "LEFT JOIN school.students ON students.group_id = groups.id\n" +
                    "WHERE students.id = ? ORDER BY groups.id";
    private static final String FIND_GROUPS_WITH_LESS_EQUAL_STUDENTS_QUERY =
            "SELECT groups.id, groups.group_name, COUNT(groups.id) AS number_of_students " +
                    "FROM school.students " +
                    "RIGHT JOIN school.groups " +
                    "ON group_id = groups.id " +
                    "GROUP BY groups.id " +
                    "HAVING COUNT(groups.id) <= ? " +
                    "ORDER BY groups.id";
    private static final String UPDATE_GROUP_QUERY = "UPDATE school.groups SET group_name = ? WHERE id = ?";
    private static final String UPDATE_STUDENT_BEFORE_DELETE_GROUP_QUERY =
            "UPDATE school.students SET group_id = NULL  WHERE group_id = ?";
    private static final String DELETE_GROUP_QUERY = "DELETE FROM school.groups WHERE id = ?";
    private static final String COUNT_TABLE_ROWS_QUERY = "SELECT COUNT(*) FROM school.groups";
    private final StudentDao studentDao;

    @Inject
    public GroupDaoImpl(ConnectorDB connector, StudentDao studentDao) {
        super(connector, SAVE_GROUP_QUERY, FIND_BY_ID_QUERY, FIND_ALL_QUERY, FIND_ALL_PAGEABLE_QUERY,
                UPDATE_GROUP_QUERY, DELETE_GROUP_QUERY, COUNT_TABLE_ROWS_QUERY);
        this.studentDao = studentDao;
    }

    @Override
    public Optional<Group> findByGroupName(String groupName) {
        return findByStringParam(groupName, FIND_BY_GROUP_NAME_QUERY);
    }

    @Override
    public Optional<Group> findGroupByStudentId(Integer id) {
        return findByIntParam(id, FIND_GROUP_BY_STUDENT_ID_QUERY);
    }

    @Override
    public List<Group> findGroupsWithLessEqualCountOfStudents(int countOfStudents) {
        return findManyByIntParam(countOfStudents, FIND_GROUPS_WITH_LESS_EQUAL_STUDENTS_QUERY);
    }

    @Override
    public void deleteGroupById(Integer id) {
        updateStudentBeforeDeleteGroup(id);
        deleteById(id);
        LOG.info("Group with id = {} was deleted", id);
    }

    @Override
    protected void setStatementForSave(PreparedStatement preparedStatement, Group group) {
        try {
            preparedStatement.setString(1, group.getGroupName());
        } catch (SQLException e) {
            LOG.error("Group has incorrect data for save. Group parameters:  {}. Thrown exception: {}",
                    group, e);
            throw new DataBaseSqlRuntimeException("", e);
        }
    }

    @Override
    protected void setStatementForUpdate(PreparedStatement preparedStatement, Group group) {
        try {
            preparedStatement.setString(1, group.getGroupName());
            preparedStatement.setInt(2, group.getId());
        } catch (SQLException e) {
            LOG.error("Group has incorrect data for update. Group parameters:  {}. Thrown exception: {}",
                    group, e);
            throw new DataBaseSqlRuntimeException("", e);
        }
    }

    @Override
    protected Group mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        List<Student> students = studentDao.findStudentsByGroupId(resultSet.getInt("id"));
        return Group.builder()
                .withId(resultSet.getInt("id"))
                .withGroupName(resultSet.getString("group_name"))
                .withStudentsList(students)
                .build();
    }

    @Override
    protected Group makeEntityWithId(Group group, int id) {
        return Group.builder()
                .withId(id)
                .withGroupName(group.getGroupName())
                .withStudentsList(group.getStudentsList())
                .build();
    }

    private void updateStudentBeforeDeleteGroup(Integer studentId) {
        try (final Connection connection = connector.getConnection();
             final PreparedStatement preparedStatement = connection
                     .prepareStatement(UPDATE_STUDENT_BEFORE_DELETE_GROUP_QUERY)) {
            preparedStatement.setInt(1, studentId);
            preparedStatement.executeUpdate();
            LOG.info("Group with id = {} was deleted from student by SQL statement {}.",
                    studentId, UPDATE_STUDENT_BEFORE_DELETE_GROUP_QUERY);
        } catch (SQLException e) {
            LOG.error("Group with id = {}  wasn't deleted from student by SQL statement {}. Thrown exception: {}",
                    studentId, UPDATE_STUDENT_BEFORE_DELETE_GROUP_QUERY, e);
            throw new DataBaseSqlRuntimeException("", e);
        }
    }

}
