package com.mikhail.tarasevich.factory;

import com.mikhail.tarasevich.entity.Group;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GroupFactoryTest {

    private static final GroupFactory groupFactory = new GroupFactory();

    @Test
    void generateGroups_inputGroupQuantity_expectedGroupList() {

        List<Group> groups = groupFactory.generateGroups(2);

        int expectedSize = 2;

        assertEquals(expectedSize, groups.size());
    }

}
