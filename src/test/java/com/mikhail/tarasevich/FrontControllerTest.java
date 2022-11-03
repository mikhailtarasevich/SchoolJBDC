package com.mikhail.tarasevich;

import com.mikhail.tarasevich.dao.CourseDao;
import com.mikhail.tarasevich.dao.GroupDao;
import com.mikhail.tarasevich.dao.StudentDao;
import com.mikhail.tarasevich.entity.Course;
import com.mikhail.tarasevich.entity.Group;
import com.mikhail.tarasevich.entity.Student;
import com.mikhail.tarasevich.provider.EntityViewProvider;
import com.mikhail.tarasevich.reader.ConsoleReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FrontControllerTest {

    private static final int ITEM_PER_PAGE = 3;
    private static final String MENU = "\nPress 0 to exit;\n" +
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
            "Press 12 to find all students related to a course;\n\n";
    @Mock
    private ConsoleReader consoleReader;
    @Mock
    private StudentDao studentDao;
    @Mock
    private CourseDao courseDao;
    @Mock
    private GroupDao groupDao;
    @Mock
    private EntityViewProvider entityViewProvider;
    @InjectMocks
    private FrontController frontController;

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
    private static Group group1;
    private static Group group2;
    private static Group group3;
    private static final List<Course> courses = new ArrayList<>();
    private static final List<Course> student1Courses = new ArrayList<>();
    private static final List<Course> student2Courses = new ArrayList<>();
    private static final List<Course> student3Courses = new ArrayList<>();
    private static final List<Student> students = new ArrayList<>();
    private static final List<Student> studentsFromGroup1 = new ArrayList<>();
    private static final List<Student> studentsFromGroup2 = new ArrayList<>();
    private static final List<Student> studentsFromGroup3 = new ArrayList<>();
    private static final List<Group> groups = new ArrayList<>();

    {
        courses.add(course1);
        courses.add(course2);
        courses.add(course3);

        student1Courses.add(course1);
        student1Courses.add(course2);

        student2Courses.add(course2);
        student2Courses.add(course3);

        student3Courses.add(course1);
        student3Courses.add(course3);

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

        students.add(student1);
        students.add(student2);
        students.add(student3);
        studentsFromGroup1.add(student2);
        studentsFromGroup1.add(student3);
        studentsFromGroup2.add(student1);
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

        groups.add(group1);
        groups.add(group2);
        groups.add(group3);
    }

    @Test
    void startMenu_inputCaseZero_expectedNothing() {

        when(consoleReader.readInt()).thenReturn(0);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        frontController.startMenu(ITEM_PER_PAGE);

        assertEquals(MENU, out.toString());

        verify(consoleReader, times(1)).readInt();
        verifyNoInteractions(studentDao);
        verifyNoInteractions(courseDao);
        verifyNoInteractions(groupDao);
        verifyNoInteractions(entityViewProvider);
    }

    @Test
    void findStudentsByName_inputCamelCaseName_expectedNothing() {

        List<Student> studentsWithJohn = new ArrayList<>();
        studentsWithJohn.add(student1);

        when(consoleReader.readInt()).thenReturn(1, 0);
        when(consoleReader.read()).thenReturn("jOhN", anyString());
        when(studentDao.findByFirstName("John")).thenReturn(studentsWithJohn);
        when(entityViewProvider.provideStudentTableView(studentsWithJohn)).thenReturn(student1.toString());

        frontController.startMenu(ITEM_PER_PAGE);

        verify(consoleReader, times(2)).readInt();
        verify(consoleReader, times(2)).read();
        verify(studentDao, times(1)).findByFirstName("John");
        verify(entityViewProvider, times(1)).provideStudentTableView(studentsWithJohn);
        verifyNoInteractions(courseDao);
        verifyNoInteractions(groupDao);
    }

    @Test
    void findStudentsByName_inputNameNotFromDataBase_expectedNothing() {

        when(consoleReader.readInt()).thenReturn(1, 0);
        when(consoleReader.read()).thenReturn("jOhN", anyString());
        when(studentDao.findByFirstName("John")).thenReturn(new ArrayList<>());

        frontController.startMenu(ITEM_PER_PAGE);

        verify(consoleReader, times(2)).readInt();
        verify(consoleReader, times(2)).read();
        verify(studentDao, times(1)).findByFirstName("John");
        verifyNoInteractions(courseDao);
        verifyNoInteractions(groupDao);
        verifyNoInteractions(entityViewProvider);
    }

    @Test
    void findAllStudents_inputExistingPage_expectedNothing() {

        when(consoleReader.readInt()).thenReturn(2, 1, 0);
        when(consoleReader.read()).thenReturn("any");
        when(studentDao.count()).thenReturn(3L);
        when(studentDao.findAll(0, 3)).thenReturn(students);
        when(entityViewProvider.provideStudentTableView(students)).thenReturn(students.toString());

        frontController.startMenu(ITEM_PER_PAGE);

        verify(consoleReader, times(3)).readInt();
        verify(consoleReader, times(1)).read();
        verify(studentDao, times(1)).findAll(0, 3);
        verify(entityViewProvider, times(1)).provideStudentTableView(students);
        verifyNoInteractions(courseDao);
        verifyNoInteractions(groupDao);
    }

    @Test
    void findAllStudents_inputPageLessThanOne_expectedNothing() {

        when(consoleReader.readInt()).thenReturn(2, 0, 0);
        when(consoleReader.read()).thenReturn("any");
        when(studentDao.count()).thenReturn(3L);

        frontController.startMenu(ITEM_PER_PAGE);

        verify(consoleReader, times(3)).readInt();
        verify(consoleReader, times(1)).read();
        verify(studentDao, times(1)).count();
        verifyNoInteractions(courseDao);
        verifyNoInteractions(groupDao);
        verifyNoInteractions(entityViewProvider);
    }

    @Test
    void findAllStudents_inputPageMoreThanPagesQuantity_expectedNothing() {

        when(consoleReader.readInt()).thenReturn(2, 4, 0);
        when(consoleReader.read()).thenReturn("any");
        when(studentDao.count()).thenReturn(3L);

        frontController.startMenu(ITEM_PER_PAGE);

        verify(consoleReader, times(3)).readInt();
        verify(consoleReader, times(1)).read();
        verify(studentDao, times(1)).count();
        verifyNoInteractions(courseDao);
        verifyNoInteractions(groupDao);
        verifyNoInteractions(entityViewProvider);
    }

    @Test
    void findAllCourses_inputExistingPage_expectedNothing() {

        when(consoleReader.readInt()).thenReturn(3, 1, 0);
        when(consoleReader.read()).thenReturn("any");
        when(courseDao.count()).thenReturn(3L);
        when(courseDao.findAll(0, 3)).thenReturn(courses);
        when(entityViewProvider.provideCourseTableView(courses)).thenReturn(courses.toString());

        frontController.startMenu(ITEM_PER_PAGE);

        verify(consoleReader, times(3)).readInt();
        verify(consoleReader, times(1)).read();
        verify(courseDao, times(1)).findAll(0, 3);
        verify(entityViewProvider, times(1)).provideCourseTableView(courses);
        verifyNoInteractions(studentDao);
        verifyNoInteractions(groupDao);
    }

    @Test
    void findAllCourses_inputPageLessThanOne_expectedNothing() {

        when(consoleReader.readInt()).thenReturn(3, 0, 0);
        when(consoleReader.read()).thenReturn("any");
        when(courseDao.count()).thenReturn(3L);

        frontController.startMenu(ITEM_PER_PAGE);

        verify(consoleReader, times(3)).readInt();
        verify(consoleReader, times(1)).read();
        verify(courseDao, times(1)).count();
        verifyNoInteractions(studentDao);
        verifyNoInteractions(groupDao);
        verifyNoInteractions(entityViewProvider);
    }

    @Test
    void findAllCourses_inputPageMoreThanPagesQuantity_expectedNothingu() {

        when(consoleReader.readInt()).thenReturn(3, 4, 0);
        when(consoleReader.read()).thenReturn("any");
        when(courseDao.count()).thenReturn(3L);

        frontController.startMenu(ITEM_PER_PAGE);

        verify(consoleReader, times(3)).readInt();
        verify(consoleReader, times(1)).read();
        verify(courseDao, times(1)).count();
        verifyNoInteractions(studentDao);
        verifyNoInteractions(groupDao);
        verifyNoInteractions(entityViewProvider);
    }

    @Test
    void findAllGroups_inputExistingPage_expectedNothing() {

        when(consoleReader.readInt()).thenReturn(4, 1, 0);
        when(consoleReader.read()).thenReturn("any");
        when(groupDao.count()).thenReturn(3L);
        when(groupDao.findAll(0, 3)).thenReturn(groups);
        when(entityViewProvider.provideGroupTableView(groups)).thenReturn(groups.toString());

        frontController.startMenu(ITEM_PER_PAGE);

        verify(consoleReader, times(3)).readInt();
        verify(consoleReader, times(1)).read();
        verify(groupDao, times(1)).findAll(0, 3);
        verify(entityViewProvider, times(1)).provideGroupTableView(groups);
        verifyNoInteractions(courseDao);
        verifyNoInteractions(studentDao);
    }

    @Test
    void findAllGroups_inputPageLessThanOne_expectedNothing() {

        when(consoleReader.readInt()).thenReturn(4, 0, 0);
        when(consoleReader.read()).thenReturn("any");
        when(groupDao.count()).thenReturn(3L);

        frontController.startMenu(ITEM_PER_PAGE);

        verify(consoleReader, times(3)).readInt();
        verify(consoleReader, times(1)).read();
        verify(groupDao, times(1)).count();
        verifyNoInteractions(studentDao);
        verifyNoInteractions(courseDao);
        verifyNoInteractions(entityViewProvider);
    }

    @Test
    void findAllGroups_inputPageMoreThanPagesQuantity_expectedNothing() {

        when(consoleReader.readInt()).thenReturn(4, 4, 0);
        when(consoleReader.read()).thenReturn("any");
        when(groupDao.count()).thenReturn(3L);

        frontController.startMenu(ITEM_PER_PAGE);

        verify(consoleReader, times(3)).readInt();
        verify(consoleReader, times(1)).read();
        verify(groupDao, times(1)).count();
        verifyNoInteractions(studentDao);
        verifyNoInteractions(courseDao);
        verifyNoInteractions(entityViewProvider);
    }

    @Test
    void saveGroup_inputFirstTwoTimesIncorrectGroupNumberThirdTimeCorrectData_expectedNothing() {

        Student studentOneWithOutId = Student.builder()
                .withFirstName("John")
                .withLastName("Locke")
                .withGroupId(3)
                .build();

        List<Student> studentsWithStudent1 = new ArrayList<>();
        studentsWithStudent1.add(student1);

        when(consoleReader.readInt()).thenReturn(5, 4, 0, 3, 0);
        when(consoleReader.read()).thenReturn("John", "Locke", "John", "Locke", "John", "Locke", "any");
        when(studentDao.save(studentOneWithOutId)).thenReturn(student1);
        when(groupDao.findAll()).thenReturn(groups);
        when(groupDao.count()).thenReturn(3L);
        when(entityViewProvider.provideGroupTableView(groups)).thenReturn(groups.toString());
        when(entityViewProvider.provideStudentTableView(studentsWithStudent1))
                .thenReturn(studentsWithStudent1.toString());

        frontController.startMenu(ITEM_PER_PAGE);

        verify(consoleReader, times(5)).readInt();
        verify(consoleReader, times(7)).read();
        verify(studentDao, times(1)).save(studentOneWithOutId);
        verify(groupDao, times(3)).findAll();
        verify(groupDao, times(2)).count();
        verify(entityViewProvider, times(3)).provideGroupTableView(groups);
        verify(entityViewProvider, times(1)).provideStudentTableView(studentsWithStudent1);
        verifyNoInteractions(courseDao);
    }

    @Test
    void addStudentToGroup_inputFirstlyStudentIsNotExistThenIncorrectGroupNumbersThenCorrectData_expectedNothing() {

        Optional<Student> optionalEmpty = Optional.empty();
        Optional<Student> optionalStudent1 = Optional.of(student1);

        when(consoleReader.readInt()).thenReturn(6, 10, 1, 0, 1, 4, 1, 3, 0);
        when(consoleReader.read()).thenReturn("any");
        when(studentDao.findById(10)).thenReturn(optionalEmpty);
        when(studentDao.findById(1)).thenReturn(optionalStudent1);
        when(groupDao.findAll()).thenReturn(groups);
        when(groupDao.count()).thenReturn(3L);
        doNothing().when(studentDao).update(student1);
        when(entityViewProvider.provideGroupTableView(groups)).thenReturn(groups.toString());


        frontController.startMenu(ITEM_PER_PAGE);

        verify(consoleReader, times(9)).readInt();
        verify(consoleReader, times(1)).read();
        verify(studentDao, times(1)).findById(10);
        verify(studentDao, times(3)).findById(1);
        verify(groupDao, times(3)).findAll();
        verify(groupDao, times(2)).count();
        verify(studentDao, times(1)).update(student1);
        verify(entityViewProvider, times(3)).provideGroupTableView(groups);
        verifyNoInteractions(courseDao);
    }

    @Test
    void subscribeStudentToCourse_inputFirstlyStudentIsNotExistThenIncorrectCourseNumbersThenCorrectData_expectedNothing() {

        Optional<Student> optionalStudentEmpty = Optional.empty();
        Optional<Student> optionalStudent1 = Optional.of(student1);
        Optional<Course> optionalCourseEmpty = Optional.empty();
        Optional<Course> optionalCourse1 = Optional.of(course1);

        when(consoleReader.readInt()).thenReturn(7, 10, 1, 10, 1, 1, 0);
        when(consoleReader.read()).thenReturn("any");
        when(studentDao.findById(10)).thenReturn(optionalStudentEmpty);
        when(studentDao.findById(1)).thenReturn(optionalStudent1);
        when(courseDao.findById(10)).thenReturn(optionalCourseEmpty);
        when(courseDao.findById(1)).thenReturn(optionalCourse1);
        when(courseDao.findAll()).thenReturn(courses);
        when(courseDao.findCoursesByStudentId(1)).thenReturn(new ArrayList<>());
        doNothing().when(studentDao).subscribeStudentToCourse(1, 1);
        when(entityViewProvider.provideCourseTableView(courses)).thenReturn(courses.toString());

        frontController.startMenu(ITEM_PER_PAGE);

        verify(consoleReader, times(7)).readInt();
        verify(consoleReader, times(1)).read();
        verify(studentDao, times(1)).findById(10);
        verify(studentDao, times(2)).findById(1);
        verify(studentDao, times(1)).subscribeStudentToCourse(1, 1);
        verify(courseDao, times(1)).findById(10);
        verify(courseDao, times(1)).findById(1);
        verify(courseDao, times(2)).findAll();
        verify(courseDao, times(1)).findCoursesByStudentId(1);
        verify(entityViewProvider, times(2)).provideCourseTableView(courses);
        verifyNoInteractions(groupDao);
    }

    @Test
    void deleteStudent_inputFirstlyStudentNotExistThenCorrectData_expectedNothing() {

        Optional<Student> optionalStudentEmpty = Optional.empty();
        Optional<Student> optionalStudent1 = Optional.of(student1);

        when(consoleReader.readInt()).thenReturn(8, 10, 1, 0);
        when(consoleReader.read()).thenReturn("any");
        when(studentDao.findById(10)).thenReturn(optionalStudentEmpty);
        when(studentDao.findById(1)).thenReturn(optionalStudent1);
        doNothing().when(studentDao).deleteStudentById(1);

        frontController.startMenu(ITEM_PER_PAGE);

        verify(consoleReader, times(4)).readInt();
        verify(consoleReader, times(1)).read();
        verify(studentDao, times(1)).findById(10);
        verify(studentDao, times(1)).findById(1);
        verify(studentDao, times(1)).deleteStudentById(1);
        verifyNoInteractions(courseDao);
        verifyNoInteractions(groupDao);
        verifyNoInteractions(entityViewProvider);
    }

    @Test
    void removeStudentFromGroup_inputFirstlyStudentNotExistThenCorrectData_expectedNothing() {

        Optional<Student> optionalStudentEmpty = Optional.empty();
        Optional<Student> optionalStudent1 = Optional.of(student1);

        when(consoleReader.readInt()).thenReturn(9, 10, 1, 0);
        when(consoleReader.read()).thenReturn("any");
        when(studentDao.findById(10)).thenReturn(optionalStudentEmpty);
        when(studentDao.findById(1)).thenReturn(optionalStudent1);
        doNothing().when(studentDao).removeStudentFromGroup(1);

        frontController.startMenu(ITEM_PER_PAGE);

        verify(consoleReader, times(4)).readInt();
        verify(consoleReader, times(1)).read();
        verify(studentDao, times(1)).findById(10);
        verify(studentDao, times(1)).findById(1);
        verify(studentDao, times(1)).removeStudentFromGroup(1);
        verifyNoInteractions(courseDao);
        verifyNoInteractions(groupDao);
    }

    @Test
    void removeStudentFromCourse_inputFirstlyStudentNotExistThenIncorrectCourseThenCorrectData_expectedNothing() {
        Course courseNotRelateToStudent = Course.builder()
                .withId(4)
                .withCourseName("NotExist")
                .withDescription("NotExist")
                .build();
        Optional<Student> optionalStudentEmpty = Optional.empty();
        Optional<Student> optionalStudent1 = Optional.of(student1);
        Optional<Course> optionalCourseEmpty = Optional.empty();
        Optional<Course> optionalCourse1 = Optional.of(course1);
        Optional<Course> optionalCourseNotRelateToStudent = Optional.of(courseNotRelateToStudent);

        when(consoleReader.readInt()).thenReturn(10, 10, 1, 0, 1, 4, 1, 1, 0);
        when(consoleReader.read()).thenReturn("any");
        when(studentDao.findById(10)).thenReturn(optionalStudentEmpty);
        when(studentDao.findById(1)).thenReturn(optionalStudent1);
        when(courseDao.findById(0)).thenReturn(optionalCourseEmpty);
        when(courseDao.findById(4)).thenReturn(optionalCourseNotRelateToStudent);
        when(courseDao.findById(1)).thenReturn(optionalCourse1);
        when(entityViewProvider.provideCourseTableView(student1Courses)).thenReturn(student1Courses.toString());

        doNothing().when(studentDao).removeStudentFromCourse(1, 1);

        frontController.startMenu(ITEM_PER_PAGE);

        verify(consoleReader, times(9)).readInt();
        verify(consoleReader, times(1)).read();
        verify(studentDao, times(1)).findById(10);
        verify(studentDao, times(3)).findById(1);
        verify(courseDao, times(1)).findById(0);
        verify(courseDao, times(1)).findById(4);
        verify(courseDao, times(1)).findById(1);
        verify(entityViewProvider, times(3)).provideCourseTableView(student1Courses);
        verify(studentDao, times(1)).removeStudentFromCourse(1, 1);
        verifyNoInteractions(groupDao);
    }

    @Test
    void findGroupsWithLessEqualCountOfStudents_inputNoGroupWithThatStudentQuantity_expectedNothing() {

        when(consoleReader.readInt()).thenReturn(11, 1, 0);
        when(consoleReader.read()).thenReturn("any");
        when(groupDao.findGroupsWithLessEqualCountOfStudents(1)).thenReturn(new ArrayList<>());

        frontController.startMenu(ITEM_PER_PAGE);

        verify(consoleReader, times(3)).readInt();
        verify(consoleReader, times(1)).read();
        verify(groupDao, times(1)).findGroupsWithLessEqualCountOfStudents(1);
        verifyNoInteractions(courseDao);
        verifyNoInteractions(studentDao);
        verifyNoInteractions(entityViewProvider);
    }

    @Test
    void findGroupsWithLessEqualCountOfStudents_CorrectData_expectedNothing() {

        when(consoleReader.readInt()).thenReturn(11, 3, 0);
        when(consoleReader.read()).thenReturn("any");
        when(groupDao.findGroupsWithLessEqualCountOfStudents(3)).thenReturn(groups);
        when(entityViewProvider.provideGroupTableView(groups)).thenReturn(groups.toString());

        frontController.startMenu(ITEM_PER_PAGE);

        verify(consoleReader, times(3)).readInt();
        verify(consoleReader, times(1)).read();
        verify(groupDao, times(1)).findGroupsWithLessEqualCountOfStudents(3);
        verify(entityViewProvider, times(1)).provideGroupTableView(groups);
        verifyNoInteractions(courseDao);
        verifyNoInteractions(studentDao);
    }

    @Test
    void findAllStudentsRelateToCourse_CorrectData_expectedNothing() {

        when(consoleReader.readInt()).thenReturn(12, 1, 0);
        when(consoleReader.read()).thenReturn("any");
        when(courseDao.findAll()).thenReturn(courses);
        when(studentDao.findStudentsRelatedToCourse(1)).thenReturn(students);
        when(entityViewProvider.provideCourseTableView(courses)).thenReturn(courses.toString());
        when(entityViewProvider.provideStudentTableView(students)).thenReturn(students.toString());

        frontController.startMenu(ITEM_PER_PAGE);

        verify(consoleReader, times(3)).readInt();
        verify(consoleReader, times(1)).read();
        verify(courseDao, times(1)).findAll();
        verify(studentDao, times(1)).findStudentsRelatedToCourse(1);
        verify(entityViewProvider, times(1)).provideCourseTableView(courses);
        verify(entityViewProvider, times(1)).provideStudentTableView(students);
        verifyNoInteractions(groupDao);
    }

    @Test
    void findAllStudentsRelateToCourse_IncorrectCourseId_expectedNothing() {

        when(consoleReader.readInt()).thenReturn(12, 12, 0);
        when(consoleReader.read()).thenReturn("any");
        when(courseDao.findAll()).thenReturn(courses);
        when(studentDao.findStudentsRelatedToCourse(12)).thenReturn(new ArrayList<>());
        when(entityViewProvider.provideCourseTableView(courses)).thenReturn(courses.toString());

        frontController.startMenu(ITEM_PER_PAGE);

        verify(consoleReader, times(3)).readInt();
        verify(consoleReader, times(1)).read();
        verify(courseDao, times(1)).findAll();
        verify(studentDao, times(1)).findStudentsRelatedToCourse(12);
        verify(entityViewProvider, times(1)).provideCourseTableView(courses);
        verifyNoInteractions(groupDao);
    }

    @Test
    void startMenu_IncorrectCaseNumber_expectedNothing() {

        when(consoleReader.readInt()).thenReturn(13, 0);

        frontController.startMenu(ITEM_PER_PAGE);

        verifyNoInteractions(studentDao);
        verifyNoInteractions(courseDao);
        verifyNoInteractions(groupDao);
        verifyNoInteractions(entityViewProvider);
    }

}
