package server;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface ServerInterface {
    boolean advisor_exists(String advisor_id, String dept);

    boolean student_exists(String student_id, String dept);

    String enroll(String courseId, String studentId, String term, String dept, boolean swapOp);

    String addCourse(String advisor_id, String course_id, String course_name, String term, String dept,
                     int capacity);

    String dropCourse(String advisor_id, String course_id, String term, String dept);

    String removeCourse(String advisor_id, String course_id, String term, String dept);

    String[] listCourseAvailability(String advisor_id, String term, String dept);

    String getClassSchedule(String studentId);

    String swapCourse(String studentIid, String oldCourseId, String newCourseId, String dept, String term);
}
