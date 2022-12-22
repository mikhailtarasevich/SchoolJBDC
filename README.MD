# SchoolJDBS 

This is simple application for making CRUD operation with entities, like Student, Course, Group.
Each student can only be in one group and be subscribed to an unlimited number of courses.
This application works with PostgreSQL database. Connecting to the database is done using the HikariCP framework.
After the application is launched, the database is filled with random auto-generated entities.

For using the application just follow tips on the screen.

## Install the application
### Download the project from github:
https://github.com/mikhailtarasevich/SchoolJBDC

### Open terminal in folder with the project and execute next commands:
    mvn clean 
    mvn install 

## Run the application
### Open terminal in folder "target" and execute next command:
    java -jar path/to/jar/SchoolBDC-1.0-SNAPSHOT-jar-with-dependencies.jar

