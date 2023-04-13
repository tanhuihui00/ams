package my.edu.utar.attendancemanagementapplication;

public class StudentOverallAttendance {
    String classcode;
    String classname;
    String count;
    String type;


    public StudentOverallAttendance(String classcode, String count, String type) {
        this.classcode = classcode;
        this.count = count;
        this.type = type;
    }

    public String getClasscode() {
        return classcode;
    }

    public void setClasscode(String classcode) {
        this.classcode = classcode;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
