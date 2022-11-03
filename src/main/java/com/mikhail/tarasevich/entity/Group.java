package com.mikhail.tarasevich.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class Group {

    private static final Logger LOG = LoggerFactory.getLogger(Group.class);
    private final int id;
    private final String groupName;
    private final List<Student> studentsList;

    private Group(Builder builder) {
        this.id = builder.id;
        this.groupName = builder.groupName;
        this.studentsList = builder.studentsList;
        LOG.info("Group was made with parameters: id = {}, groupName = {}, studentsLIst = {}",
                id, groupName, studentsList);
    }

    public int getId() {
        return id;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<Student> getStudentsList() {
        return studentsList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Group)) return false;
        Group group = (Group) o;
        return getId() == group.getId() && Objects.equals(getGroupName(), group.getGroupName()) &&
                Objects.equals(getStudentsList(), group.getStudentsList());
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", groupName='" + groupName + '\'' +
                ", studentsList=" + studentsList +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getGroupName(), getStudentsList());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int id;
        private String groupName;
        private List<Student> studentsList;

        private Builder() {

        }

        public Builder withId(final int id) {
            this.id = id;
            return this;
        }

        public Builder withGroupName(final String groupName) {
            this.groupName = groupName;
            return this;
        }

        public Builder withStudentsList(final List<Student> studentsList) {
            this.studentsList = studentsList;
            return this;
        }

        public Group build() {
            return new Group(this);
        }
    }

}
