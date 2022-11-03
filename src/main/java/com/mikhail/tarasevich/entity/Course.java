package com.mikhail.tarasevich.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class Course {

    private static final Logger LOG = LoggerFactory.getLogger(Course.class);
    private final int id;
    private final String courseName;
    private final String description;

    private Course(Builder builder) {
        this.id = builder.id;
        this.courseName = builder.courseName;
        this.description = builder.description;
        LOG.info("Course was made with parameters: id = {}, courseName = {}, description = {}",
                id, courseName, description);
    }

    public int getId() {
        return id;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        Course course = (Course) o;
        return getId() == course.getId() && Objects.equals(getCourseName(), course.getCourseName())
                && Objects.equals(getDescription(), course.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCourseName(), getDescription());
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", courseName='" + courseName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int id;
        private String courseName;
        private String description;

        private Builder() {

        }

        public Builder withId(final int id) {
            this.id = id;
            return this;
        }

        public Builder withCourseName(final String courseName) {
            this.courseName = courseName;
            return this;
        }

        public Builder withDescription(final String description) {
            this.description = description;
            return this;
        }

        public Course build() {
            return new Course(this);
        }
    }

}
