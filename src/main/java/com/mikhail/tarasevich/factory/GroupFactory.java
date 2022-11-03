package com.mikhail.tarasevich.factory;

import com.mikhail.tarasevich.entity.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GroupFactory {

    private static final Logger LOG = LoggerFactory.getLogger(GroupFactory.class);

    public List<Group> generateGroups(int groupQuantity){

        LOG.debug("Method generateGroups(int groupQuantity) was called");

        List<Group> groups = new ArrayList<>();
        List<String> generatedGroupNames = generateGroupNames(groupQuantity);

        for(String groupName : generatedGroupNames){
            Group group = Group.builder()
                    .withGroupName(groupName)
                    .build();
            groups.add(group);
            LOG.debug("Group was added to output method list, group parameters: {}", group);
        }
        LOG.debug("Output method group's list: {}", groups);
        return groups;
    }

    private List<String> generateGroupNames(int groupQuantity){

        LOG.debug("Method generateGroupNames(int groupQuantity) was called");

        int successIterationQuantity = 0;
        List<String> groupNames = new ArrayList<>();
        Random random = new Random();

        while (successIterationQuantity < groupQuantity){
            StringBuilder generatedName = new StringBuilder();
            generatedName.append((char)(random.nextInt(122 - 97 + 1) + 97))
                    .append((char)(random.nextInt(122 - 97 + 1) + 97))
                    .append("-")
                    .append(random.nextInt(9 - 1 + 1) + 1)
                    .append(random.nextInt(9 - 0 + 1) + 0);
            if(!groupNames.contains(generatedName.toString())){
                groupNames.add(generatedName.toString());
                successIterationQuantity++;
                LOG.debug("Group name = {} was added to output method groupNameS list", generatedName);
            } else {
                LOG.debug("Group name {} wasn't added to output method group name list," +
                        " because list already has the same group name", generatedName);
            }
        }
        LOG.debug("Output method group names list: {}", groupNames);
        return groupNames;
    }

}
