package com.mikhail.tarasevich;

import com.google.inject.Inject;
import com.mikhail.tarasevich.dao.CourseDao;
import com.mikhail.tarasevich.dao.GroupDao;
import com.mikhail.tarasevich.dao.StudentDao;
import com.mikhail.tarasevich.entity.Course;
import com.mikhail.tarasevich.entity.Group;
import com.mikhail.tarasevich.entity.Student;
import com.mikhail.tarasevich.provider.EntityViewProvider;
import com.mikhail.tarasevich.reader.ConsoleReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FrontController {

    private static final Logger LOG = LoggerFactory.getLogger(FrontController.class);
    private final ConsoleReader consoleReader;
    private final StudentDao studentDao;
    private final CourseDao courseDao;
    private final GroupDao groupDao;
    private final EntityViewProvider entityViewProvider;

    @Inject
    public FrontController(StudentDao studentDao, CourseDao courseDao, GroupDao groupDao,
                           EntityViewProvider entityViewProvider, ConsoleReader consoleReader) {
        this.studentDao = studentDao;
        this.courseDao = courseDao;
        this.groupDao = groupDao;
        this.entityViewProvider = entityViewProvider;
        this.consoleReader = consoleReader;
        LOG.debug("Object FrontControllerImpl.class has been made");
    }

    public void startMenu(int itemsPerPage) {

        LOG.debug("Method startMenu(int {}) has been called", itemsPerPage);

        int chooser;
        int pages;

        System.out.println("\nPress 0 to exit;\n" +
                "Press 1 to find students by their first name;\n" +
                "Press 2 find all students;\n" +
                "Press 3 find all courses;\n" +
                "Press 4 find all groups;\n" +
                "Press 5 to add new student;\n" +
                "Press 6 to add a student to a group;\n" +
                "Press 7 to add a student to a course;\n" +
                "Press 8 to delete a student;\n" +
                "Press 9 to remove a student from a group;\n" +
                "Press 10 to remove a student from a course;\n" +
                "Press 11 to find groups with less/equal count of students;\n" +
                "Press 12 to find all students related to a course;\n");

        chooser = consoleReader.readInt();
        LOG.debug("Position {} has been called from menu", chooser);

        switch (chooser) {

            case 0:
                break;

            case 1:
                findStudentsByName();
                returnToMenu(itemsPerPage);
                break;

            case 2:
                pages = (int) Math.ceil((double) studentDao.count() / (double) itemsPerPage);
                findAllStudents(pages, itemsPerPage);
                returnToMenu(itemsPerPage);
                break;

            case 3:
                pages = (int) Math.ceil((double) courseDao.count() / (double) itemsPerPage);
                findAllCourses(pages, itemsPerPage);
                returnToMenu(itemsPerPage);
                break;

            case 4:
                pages = (int) Math.ceil((double) groupDao.count() / (double) itemsPerPage);
                findAllGroups(pages, itemsPerPage);
                returnToMenu(itemsPerPage);
                break;

            case 5:
                saveStudent();
                returnToMenu(itemsPerPage);
                break;

            case 6:
                addStudentToGroup();
                LOG.debug("Method addStudentToGroup() finished successfully");
                returnToMenu(itemsPerPage);
                break;

            case 7:
                subscribeStudentToCourse();
                LOG.debug("Method subscribeStudentToCourse() finished successfully");
                returnToMenu(itemsPerPage);
                break;

            case 8:
                deleteStudent();
                LOG.debug("Method deleteStudent() finished successfully");
                returnToMenu(itemsPerPage);
                break;

            case 9:
                removeStudentFromGroup();
                LOG.debug("Method removeStudentFromGroup() finished successfully");
                returnToMenu(itemsPerPage);
                break;

            case 10:
                removeStudentFromCourse();
                LOG.debug("Method removeStudentFromCourse() finished successfully");
                returnToMenu(itemsPerPage);
                break;

            case 11:
                findGroupsWithLessEqualCountOfStudents();
                LOG.debug("Method findGroupsWithLessEqualCountOfStudents() finished successfully");
                returnToMenu(itemsPerPage);
                break;

            case 12:
                findAllStudentsRelateToCourse();
                LOG.debug("Method findAllStudentsRelateToCourse() finished successfully");
                returnToMenu(itemsPerPage);
                break;

            default:
                startMenu(itemsPerPage);
        }
    }

    private void findStudentsByName() {
        System.out.println("\nPlease, type student's first name:");
        String firstName = nameFormatter(consoleReader.read());
        LOG.debug("Formatted input student first name: {}", firstName);
        List<Student> students = studentDao.findByFirstName(firstName);
        LOG.debug("Found students by name: {}", students);
        if (students.isEmpty()) {
            LOG.debug("Students wasn't find");
            System.out.println("Students with first name " + firstName + " doesn't exist!\n");
            return;
        }
        System.out.println(entityViewProvider
                .provideStudentTableView(students));
        LOG.debug("Method findStudentsByName() finished successfully. Found students: {}", students);
    }

    private void findAllStudents(int pages, int itemsPerPage) {
        LOG.debug("Method findAllStudents(int {}, int {}) was called", pages, itemsPerPage);
        System.out.println("\nPlease, type a page number from 1 to " + pages + ":");
        int pageNumber = consoleReader.readInt();
        if (pageNumber <= 0 || pageNumber > pages) {
            System.out.println("You typed an incorrect page number, please, try again\n");
            return;
        }
        List<Student> students = studentDao.findAll(pageNumber - 1, itemsPerPage);
        System.out.println(entityViewProvider.provideStudentTableView(students));
        LOG.debug("Printed all students from a page {}, students per page = {}. Found students: {}",
                pageNumber, itemsPerPage, students);
    }

    private void findAllCourses(int pages, int itemsPerPage) {
        LOG.debug("Method findAllCourses(int {}, int {}) was called", pages, itemsPerPage);
        System.out.println("\nPlease, type a page number from 1 to " + pages + ":");
        int pageNumber = consoleReader.readInt();
        if (pageNumber <= 0 || pageNumber > pages) {
            System.out.println("You typed an incorrect page number, please, try again\n");
            return;
        }
        List<Course> courses = courseDao.findAll(pageNumber - 1, itemsPerPage);
        System.out.println(entityViewProvider.provideCourseTableView(courses));
        LOG.debug("Printed all courses from page {}, students per page = {}. Found courses: {}",
                pageNumber, itemsPerPage, courses);

    }

    private void findAllGroups(int pages, int itemsPerPage) {
        LOG.debug("Method findAllGroups(int {}, int {}) has been called", pages, itemsPerPage);
        System.out.println("\nPlease, type a page number from 1 to " + pages + ":");
        int pageNumber = consoleReader.readInt();
        if (pageNumber <= 0 || pageNumber > pages) {
            System.out.println("You typed an incorrect page number, please, try again\n");
            return;
        }
        List<Group> groups = groupDao.findAll(pageNumber - 1, itemsPerPage);
        System.out.println(entityViewProvider.provideGroupTableView(groups));
        LOG.debug("Printed all groups from page {}, students per page = {}. Found groups: {}",
                pageNumber, itemsPerPage, groups);
    }

    private void saveStudent() {
        LOG.debug("Method saveStudent() has been called");
        Student student = makeNewStudent();
        Student savedStudent = studentDao.save(student);
        LOG.debug("Student was saved to database: {}", savedStudent);
        List<Student> students = new ArrayList<>();
        students.add(savedStudent);
        System.out.println("The student with these parameters has been added to database: \n" +
                entityViewProvider.provideStudentTableView(students));
        LOG.debug("Method saveStudent() finished successfully");
    }

    private Student makeNewStudent() {
        LOG.debug("Method makeNewStudent() was called");
        System.out.println("\nPlease, type first name:");
        String firstName = nameFormatter(consoleReader.read());
        System.out.println("\nPlease, type last name:");
        String lastName = nameFormatter(consoleReader.read());
        System.out.println("\nPlease, type the group id. Choose the group id from the list:\n" +
                entityViewProvider.provideGroupTableView(groupDao.findAll()));
        int groupId = consoleReader.readInt();
        if (!(groupId >= 1 && groupId <= groupDao.count())) {
            System.out.println("You typed an incorrect group id, please, try again to make a student\n");
            return makeNewStudent();
        }
        LOG.debug("User typed next student parameters: First name = {}, Second name = {}, Group Id = {}",
                firstName, lastName, groupId);
        Student student = Student.builder()
                .withFirstName(firstName)
                .withLastName(lastName)
                .withGroupId(groupId)
                .build();
        LOG.debug("Student has been made with parameters: {}", student);
        return student;
    }

    private void addStudentToGroup() {
        LOG.debug("Method addStudentToGroup() was called");
        Student student;
        System.out.println("Which student do you want to add to the group? Please, type the student id: \n");
        int studentId = consoleReader.readInt();
        Optional<Student> optionalStudent = studentDao.findById(studentId);
        if (optionalStudent.isPresent()) {
            student = optionalStudent.get();
        } else {
            LOG.debug("Student with id = {} doesn't exist!", studentId);
            System.out.println("The student doesn't exist! Please, choose another student id (From 1 to " +
                    studentDao.count() + ")\n");
            addStudentToGroup();
            return;
        }
        System.out.println("\nPlease, type new student's group id. Choose the group id from the list:\n" +
                entityViewProvider.provideGroupTableView(groupDao.findAll()));
        int groupId = consoleReader.readInt();
        if (!(groupId >= 1 && groupId <= groupDao.count())) {
            LOG.debug("Group with id = {} doesn't exist!", groupId);
            System.out.println("You typed an incorrect group id, please, try again\n");
            addStudentToGroup();
        } else {
            Student upgradedStudent = Student.builder()
                    .withId(student.getId())
                    .withFirstName(student.getFirstName())
                    .withLastName(student.getLastName())
                    .withGroupId(groupId)
                    .withCoursesList(student.getCoursesList())
                    .build();
            studentDao.update(upgradedStudent);
            System.out.println("The student has been added to the group\n");
            LOG.debug("Method addStudentToGroup() finished successfully. " +
                    "Student's group was changed from:\n{}\nto:{}\n", student, upgradedStudent);
        }
    }

    private void subscribeStudentToCourse() {
        LOG.debug("Method subscribeStudentToCourse() was called");
        System.out.println("Which student do you want to subscribe to the course? Please, type the student id: \n");
        int studentId = consoleReader.readInt();
        if (!studentDao.findById(studentId).isPresent()) {
            LOG.debug("Student with id = {} doesn't exist!", studentId);
            System.out.println("The student doesn't exist! Please, choose another student id\n");
            subscribeStudentToCourse();
            return;
        }
        System.out.println("\nPlease, type new student's course id. Choose the course id from the list:\n" +
                entityViewProvider.provideCourseTableView(courseDao.findAll()));
        int courseId = consoleReader.readInt();
        Optional<Course> optionalCourse = courseDao.findById(courseId);
        if (!optionalCourse.isPresent() || courseDao.findCoursesByStudentId(studentId).contains(optionalCourse.get())) {
            LOG.debug("Incorrect course id = {} or student already subscribed on this course", courseId);
            System.out.println("You typed an incorrect course id or these student already been subscribed " +
                    "to this course, please, try again\n");
            subscribeStudentToCourse();
        } else {
            studentDao.subscribeStudentToCourse(studentId, courseId);
            System.out.println("The student has been subscribed to the course\n");
            LOG.debug("Method subscribeStudentToCourse() finished successfully");
        }
    }

    private void deleteStudent() {
        LOG.debug("Method deleteStudent() was called");
        System.out.println("Which student do you want to delete? Please, type the student id: \n");
        int studentId = consoleReader.readInt();
        if (!studentDao.findById(studentId).isPresent()) {
            LOG.debug("Student with id = {} doesn't exist!", studentId);
            System.out.println("The student doesn't exist! Please, choose another student id\n");
            deleteStudent();
            return;
        }
        studentDao.deleteStudentById(studentId);
        System.out.println("The student with id = " + studentId + " has been deleted from database\n");
        LOG.debug("Method deleteStudent() finished successfully. " +
                "Student with id = {} was deleted from database", studentId);
    }

    private void removeStudentFromGroup() {
        LOG.debug("Method removeStudentFromGroup() was called");
        System.out.println("Which student do you want to delete from the group? Please, type the student id: \n");
        int studentId = consoleReader.readInt();
        if (!studentDao.findById(studentId).isPresent()) {
            LOG.debug("Student with id = {} doesn't exist!", studentId);
            System.out.println("The student doesn't exist! Please, choose another student id\n");
            removeStudentFromGroup();
            return;
        }
        studentDao.removeStudentFromGroup(studentId);
        System.out.println("The student with id = " + studentId + " has been deleted from the group\n");
        LOG.debug("Method removeStudentFromGroup() finished successfully. " +
                "Student with id = {} was deleted from group", studentId);
    }

    private void removeStudentFromCourse() {
        LOG.debug("Method removeStudentFromGroup() was called");
        System.out.println("Which student do you want to remove from the course? Please, type the student id: \n");
        int studentId = consoleReader.readInt();
        Optional<Student> student = studentDao.findById(studentId);
        if (!student.isPresent()) {
            LOG.debug("Student with id = {} doesn't exist!", studentId);
            System.out.println("The student doesn't exist! Please, choose another student id\n");
            removeStudentFromCourse();
            return;
        }
        System.out.println("What course do you want to unsubscribe from the student? " +
                "The student is subscribed to :\n" +
                entityViewProvider.provideCourseTableView(student.get().getCoursesList()) +
                "Please, type the course id: \n");
        int courseId = consoleReader.readInt();
        Optional<Course> optionalCourse = courseDao.findById(courseId);
        if (!optionalCourse.isPresent()) {
            LOG.debug("Course with id = {} doesn't exist!", courseId);
            System.out.println("Course with this id doesn't exist! Please, try again.\n");
            removeStudentFromCourse();
            return;
        }
        if (!student.get().getCoursesList().contains(optionalCourse.get())) {
            LOG.debug("Course with id = {} doesn't relate to student!", courseId);
            System.out.println("Course with this id doesn't relate to student! Please, try again.\n");
            removeStudentFromCourse();
            return;
        }
        studentDao.removeStudentFromCourse(studentId, courseId);
        System.out.println("The student with id = " + studentId + " has been unsubscribed from the course with id = " +
                courseId + "\n");
        LOG.debug("Method removeStudentFromCourse() finished successfully. " +
                "Student with id = {} was deleted from course with id = {}", studentId, courseId);
    }

    private void findGroupsWithLessEqualCountOfStudents() {
        LOG.debug("Method findGroupsWithLessEqualCountOfStudents() was called");
        System.out.println("Please, type students' quantity: \n");
        int quantity = consoleReader.readInt();
        List<Group> groups = groupDao.findGroupsWithLessEqualCountOfStudents(quantity);
        if (groups.isEmpty()) {
            LOG.debug("Group wasn't find");
            System.out.println("The group with this students' quantity wasn't found\n");
            return;
        }
        System.out.println(entityViewProvider.provideGroupTableView(groups));
        LOG.debug("Method findGroupsWithLessEqualCountOfStudents() finished successfully. " +
                "Found groups: {}", groups);
    }

    private void findAllStudentsRelateToCourse() {
        LOG.debug("Method findAllStudentsRelateToCourse() was called");
        List<Course> courses = courseDao.findAll();
        System.out.println("Students of what course are you interested in?\n" +
                entityViewProvider.provideCourseTableView(courses) +
                "Please, type the course id: \n");
        int courseId = consoleReader.readInt();
        Optional<Course> optionalCourse = courseDao.findById(courseId);
        List<Student> students = studentDao.findStudentsRelatedToCourse(courseId);
        if (students.isEmpty() || optionalCourse.isPresent()) {
            LOG.debug("Nobody from students not subscribed on the course with id = {}!", courseId);
            System.out.println("No students is subscribed to the course with id = " + courseId);
            return;
        }
        System.out.println(entityViewProvider.provideStudentTableView(students));
        LOG.debug("Method findAllStudentsRelateToCourse() finished successfully. " +
                "Found students which relate to course (course id = {}): {}", courseId, students);
    }

    private void returnToMenu(int itemsPerPage) {
        LOG.debug("Method returnToMenu(int {}) was called", itemsPerPage);
        System.out.println("\nPlease, enter any symbol to return to menu\n");
        consoleReader.read();
        startMenu(itemsPerPage);
    }

    private String nameFormatter(String name) {
        LOG.debug("Method nameFormatter(String {}) was called", name);
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

}
