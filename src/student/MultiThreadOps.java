package student;

import server.Server;
import server.ServerInterface;

public class MultiThreadOps implements Runnable {
    private Thread thread;
    private String studentId, oldCourseId, newCourseId, dept, term;
    private ServerInterface server;
    private static int counter = 0;

    MultiThreadOps(String studentId, String oldCourseId, String newCourseId, String dept, String term, ServerInterface server) {
        this.studentId = studentId;
        this.oldCourseId = oldCourseId;
        this.newCourseId = newCourseId;
        this.dept = dept;
        this.term = term;
        this.server = server;
    }

    @Override
    public void run() {
        String result = server.swapCourse(studentId, oldCourseId, newCourseId, dept, term);
        System.out.println("For " + studentId + " " + result);
    }

    public void start() throws InterruptedException {
		// One in coming connection. Forking a thread.

        if (thread == null) {
            thread = new Thread(this, "Multi req Swap");
            counter++;
            thread.start();
            if(counter == 1){
                thread.join();
                counter = 0;
            }
        }
    }
}
