package com.mikhail.tarasevich.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class Student {

    private static final Logger LOG = LoggerFactory.getLogger(Student.class);
    private final int id;
    private final String firstName;
    private final String lastName;
    private final int groupId;
    private final List<Course> coursesList;

    private Student(Builder builder) {
        this.id = builder.id;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.groupId = builder.groupId;
        this.coursesList = builder.coursesList;
        LOG.info("Student was made with parameters: id = {}, firstName = {}, lastName = {}, groupId = {}, coursesList = {}",
                id, firstName, lastName, groupId, coursesList);
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getGroupId() {
        return groupId;
    }

    public List<Course> getCoursesList() {
        return coursesList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student student = (Student) o;
        return getId() == student.getId() && getGroupId() == student.getGroupId() && Objects.equals(getFirstName(),
                student.getFirstName()) && Objects.equals(getLastName(),
                student.getLastName()) && Objects.equals(getCoursesList(), student.getCoursesList());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFirstName(), getLastName(), getGroupId(), getCoursesList());
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", groupId=" + groupId +
                ", coursesList=" + coursesList +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int id;
        private String firstName;
        private String lastName;
        private int groupId;
        private List<Course> coursesList;

        private Builder() {
        }

        public Builder withId(final int id) {
            this.id = id;
            return this;
        }

        public Builder withFirstName(final String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder withLastName(final String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder withGroupId(final int groupId) {
            this.groupId = groupId;
            return this;
        }

        public Builder withCoursesList(final List<Course> coursesList) {
            this.coursesList = coursesList;
            return this;
        }

        public Student build() {
            return new Student(this);
        }
    }

}
