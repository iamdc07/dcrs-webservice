package client;

import advisor.AdvisorOperations;
import server.Server;
import server.ServerInterface;
import student.StudentOperations;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class Client {
    private static Logger logs;
    private static FileHandler fileHandler;

    public static void main(String[] args) throws MalformedURLException, InterruptedException {
        ServerInterface serverInterface = null;
        Scanner sc = new Scanner(System.in);
        Server server;
        Service service;
        URL url = null;
        String dept = "";
        Boolean student = false, advisor = false;

        // Get Id from the User
        System.out.println("Enter your Id: ");
        String id = sc.nextLine().toUpperCase();

        logs = Logger.getLogger("User Id: " + id);
        AdvisorOperations advisoroperations = new AdvisorOperations(logs);
        StudentOperations studentoperations = new StudentOperations(logs);


        // Check for Student or Advisor
        if (id.startsWith("COMP") || id.startsWith("SOEN") || id.startsWith("INSE")) {
            if (id.charAt(4) == ('A')) {
                advisor = true;
                dept = (id.indexOf("COMPA") != -1) ? "COMP" : (id.indexOf("SOENA") != -1) ? "SOEN" : "INSE";
            } else if (id.charAt(4) == ('S')) {
                student = true;
                dept = (id.indexOf("COMPS") != -1) ? "COMP" : (id.indexOf("SOENS") != -1) ? "SOEN" : "INSE";
            } else {
                System.out.println("Invalid ID, Try Again");
                return;
            }
        } else {
            System.out.println("Invalid ID");
            return;
        }

        // Webservices
        int compPort = 6789, soenPort = 6791, insePort = 6793;

        switch (dept) {
            case "COMP":
                url = new URL("http://localhost:6789/COMP?wsdl");
                break;
            case "SOEN":
                url = new URL("http://localhost:6791/SOEN?wsdl");
                break;
            case "INSE":
                url = new URL("http://localhost:6793/INSE?wsdl");
                break;
        }

        QName qName = new QName("http://server/", "ServerOperationsService");

        if (url != null) {
            service = Service.create(url, qName);
        } else {
            logs.severe("URL is null");
            return;
        }


        serverInterface = service.getPort(ServerInterface.class);

        // no Object. no work.
        if (serverInterface == null) {
            logs.severe("Error initializing interface object. Try again later!");
            return;
        }

        // Make Log files
        File logDir = new File("./userlogs/");
        if (!(logDir.exists()))
            logDir.mkdir();


        // Check if Advisor
        if (advisor && !(dept.equals(""))) {
            // Check if Advisor is valid
            Boolean valid_advisor = serverInterface.advisor_exists(id, dept);
            if (valid_advisor) {
                // Set up the logging mechanism
                try {
                    fileHandler = new FileHandler("userlogs/" + id + ".log", true);
                    logs.addHandler(fileHandler);
                } catch (IOException ioe) {
                    logs.warning("Failed to create handler for log file.\n Message: " + ioe.getMessage());
                }

                while (true) {
                    System.out.println("\nEnter Term: ");
                    String term = sc.nextLine();

                    System.out.println("\nEnter your Choice: ");
                    System.out.println("1. Add Course: ");
                    System.out.println("2. Remove Course: ");
                    System.out.println("3. List Course Availability: ");
                    System.out.println("4. Enroll a Student: ");
                    System.out.println("5. Drop a Course: ");
                    System.out.println("6. Get Class Schedule: ");
                    String choice = sc.nextLine();

                    if (choice.equals("1")) {
                        logs.info(LocalDateTime.now() + " Operation: Add Course\n");
                        advisoroperations.add_Course(id, term, serverInterface, dept);
                    } else if (choice.equals("2")) {
                        logs.info(LocalDateTime.now() + " Operation: Remove Course\n");
                        advisoroperations.remove_Course(id, term, serverInterface, dept);
                    } else if (choice.equals("3")) {
                        logs.info(LocalDateTime.now() + " Operation: List Course Availability\n");
                        advisoroperations.listCourseAvailability(id, serverInterface, term, dept);
                    } else if (choice.equals("4")) {
                        logs.info(LocalDateTime.now() + " Operation: Enroll Course for Student\n");
                        System.out.println("Enter the Student Id: ");
                        String studentId = sc.nextLine().toUpperCase();
                        studentoperations.enrollCourse(studentId, term, serverInterface, dept);
                    } else if (choice.equals("5")) {
                        logs.info(LocalDateTime.now() + " Operation: Drop Course for Student\n");
                        System.out.println("Enter the Student Id: ");
                        String studentId = sc.nextLine().toUpperCase();
                        studentoperations.dropCourse(studentId, term, serverInterface, dept);
                    } else if (choice.equals("6")) {
                        logs.info(LocalDateTime.now() + " Operation: Get Class Schedule for Student\n");
                        System.out.println("Enter the Student Id: ");
                        String studentId = sc.nextLine().toUpperCase();
                        studentoperations.getClassSchedule(studentId, serverInterface);
                    }
                }

            } else {
                System.out.println("ID not registered in database!");
            }
        } else if (student && !(dept.equals(""))) {
            // Check if StudentId is valid
            Boolean valid_student = serverInterface.student_exists(id, dept);
            if (valid_student) {
                // Set up the logging mechanism
                logs = Logger.getLogger("User Id: " + id);
                try {
                    fileHandler = new FileHandler("userlogs/" + id + ".log", true);
                    logs.addHandler(fileHandler);
                } catch (IOException ioe) {
                    logs.warning("Failed to create handler for log file.\n Message: " + ioe.getMessage());
                }
                while (true) {
                    System.out.println("\nEnter Term: ");
                    String term = sc.nextLine();

                    System.out.println("\nEnter your Choice: ");
                    System.out.println("1. Enroll Course: ");
                    System.out.println("2. Drop Course: ");
                    System.out.println("3. Get Class Schedule: ");
                    System.out.println("4. Swap Course: ");
                    System.out.println("5. Swap Course(Multi Threaded): ");
                    String choice = sc.nextLine();

                    if (choice.equals("1")) {
                        logs.info(LocalDateTime.now() + " Operation: Enroll Course");
                        studentoperations.enrollCourse(id, term, serverInterface, dept);
                    } else if (choice.equals("2")) {
                        logs.info(LocalDateTime.now() + " Operation: Drop Course");
                        studentoperations.dropCourse(id, term, serverInterface, dept);
                    } else if (choice.equals("3")) {
                        logs.info(LocalDateTime.now() + " Operation: Get Class Schedule");
                        studentoperations.getClassSchedule(id, serverInterface);
                    } else if (choice.equals("4")) {
                        logs.info(LocalDateTime.now() + " Operation: Swap Course");
                        studentoperations.swapCourse(id, dept, term, serverInterface);
                    } else if (choice.equals("5")) {
                        logs.info(LocalDateTime.now() + " Operation: Swap Course(MultiThreaded)");
                        studentoperations.swapMultiThread(dept, term, serverInterface);
                    }
                }
            }
        } else {
            System.out.print("Invalid ID");
        }
        sc.close();

    }
}
