package my.edu.utar.attendancemanagementapplication;

public class AttendanceClass {
    String time;
    String status;
    String date;

    public AttendanceClass(String time, String status, String date) {
        this.time = time;
        this.status = status;
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

}
