package advisor;

import server.Server;
import server.ServerInterface;

import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.logging.Logger;

public class AdvisorOperations {
    private static Logger logs;
    Scanner sc = new Scanner(System.in);

    public AdvisorOperations(Logger logs) {
        super();
        this.logs = logs;
    }

    public void add_Course(String advisor_id, String term, ServerInterface server, String dept) {
        if (term.equalsIgnoreCase("winter") || term.equalsIgnoreCase("fall") || term.equalsIgnoreCase("summer")) {
            System.out.println("Enter Course ID: ");
            String c_id = sc.nextLine().toUpperCase();

            System.out.println("Enter Course Name: ");
            String c_name = sc.nextLine();

            System.out.println("Enter Capacity: ");
            String cap = sc.nextLine();
            int capacity = Integer.parseInt(cap);

            String result;
            if (c_id.substring(0, 4).equalsIgnoreCase(dept)) {
                result = server.addCourse(advisor_id, c_id, c_name, term, dept, capacity);
                logs.info(LocalDateTime.now() + "Response from Server: " + result);
                System.out.println(result);
            } else {
                System.out.println("Invalid course ID");
            }

        } else {
            System.out.println("Invalid term!");
        }
    }

    public void remove_Course(String advisor_id, String term, ServerInterface server, String dept) {
        System.out.println("Enter Course ID: ");
        String c_id = sc.nextLine().toUpperCase();

        String result = server.removeCourse(advisor_id, c_id, term, dept);
        logs.info(LocalDateTime.now() + "Response from Server: " + result);
        System.out.println(result);
    }

    public void listCourseAvailability(String advisorId, ServerInterface server, String term, String dept) {
        int k = 0;

        String[] courses = server.listCourseAvailability(advisorId, term, dept);
        logs.info(LocalDateTime.now() + "Response from Server: ");
        if (courses != null && courses.length != 0) {
            System.out.println("Courses available for " + term + " term: ");
            for (String courseId : courses) {
                if (!(courseId.trim().equalsIgnoreCase(","))) {
                    String[] idAndSeats = courseId.split(",");
                    String[] ids = idAndSeats[0].split(";");
                    String[] seats = idAndSeats[1].split(";");
                    int counter = 0;
                    for (String item : ids) {
                        System.out.println("COURSE: " + item + "\t" + "Seats Available: " + seats[counter++]);
                    }
                }
            }
        } else {
            System.out.println("Error in response, No courses Found");
        }
    }

}
