package student;

import server.ServerInterface;

import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.logging.Logger;

public class StudentOperations {
    private Logger logs;
    Scanner sc = new Scanner(System.in);

    public StudentOperations(Logger logs) {
        super();
        this.logs = logs;
    }

    public void enrollCourse(String student_id, String term, ServerInterface server, String dept) {
        System.out.println("Enter Course ID: ");
        String c_id = sc.nextLine().toUpperCase();

        String result = server.enroll(c_id, student_id, term, dept, false);
        logs.info(LocalDateTime.now() + " Response from Server: " + result);
        System.out.println(result);
    }

    public void dropCourse(String student_id, String term, ServerInterface server, String dept) {
        System.out.println("Enter Course ID: ");
        String c_id = sc.nextLine().toUpperCase();

        String result = server.dropCourse(student_id, c_id, term, dept);
        logs.info(LocalDateTime.now() + " Response from Server: " + result);
        System.out.println(result);
    }

    public static void getClassSchedule(String student_id, ServerInterface server) {
        int counter = 0;

        String courseList = server.getClassSchedule(student_id);
        System.out.println("All the courses enrolled by " + student_id);

        if (courseList != null && courseList != "") {
            String[] eachTerm = courseList.split(",");
            for (String item : eachTerm) {
                String[] termsCourses = eachTerm[counter++].split(";");
                System.out.print("Term: ");
                for (String elements : termsCourses) {
                    System.out.print(elements);
                    System.out.println("\t");
                }
            }
        }
    }

    public void swapCourse(String studentId, String dept, String term, ServerInterface server) {
        System.out.println("Enter the ID of Course to be dropped: ");
        String oldCourseId = sc.nextLine().toUpperCase();

        System.out.println("Enter ID of Course to enroll: ");
        String newCourseId = sc.nextLine().toUpperCase();

        String result = server.swapCourse(studentId, oldCourseId, newCourseId, dept, term);
        logs.info(LocalDateTime.now() + "Response from Server: " + result);
        System.out.println(result);
    }

    public void swapMultiThread(String dept, String term, ServerInterface server) throws InterruptedException {
        String firstStudentId = "COMPS1001";
        String secondStudentId = "COMPS1002";

        System.out.println("FIRST STUDENT ID : " + firstStudentId);
        System.out.println("SECOND STUDENT ID : " + secondStudentId);

        server.enroll("SOEN1001", firstStudentId, term, dept, false);
        server.enroll("INSE1001", secondStudentId, term, dept, false);

        // Get Schedule before swapping
        getClassSchedule(firstStudentId, server);
        getClassSchedule(secondStudentId, server);

        MultiThreadOps req1 = new MultiThreadOps(firstStudentId, "SOEN1001", "COMP1001", dept, term, server);
        MultiThreadOps req2 = new MultiThreadOps(secondStudentId, "INSE1001", "COMP1001", dept, term, server);

        req1.start();
        req2.start();

        // Get Schedule after swapping
        getClassSchedule(firstStudentId, server);
        getClassSchedule(secondStudentId, server);
    }
}
