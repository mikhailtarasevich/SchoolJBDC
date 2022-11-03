package com.mikhail.tarasevich.dao;

import com.mikhail.tarasevich.entity.Group;

import java.util.List;
import java.util.Optional;

public interface GroupDao extends CrudPageableDao<Group> {

    //read
    Optional<Group> findByGroupName(String groupName);
    Optional<Group> findGroupByStudentId(Integer id);
    List<Group> findGroupsWithLessEqualCountOfStudents(int countOfStudents);

    //delete
    void deleteGroupById(Integer id);

}
