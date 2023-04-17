package my.edu.utar.attendancemanagementapplication;

public class AttendanceListDetails {
    private String userID, attendanceID, subjectCode, type, date, time, qrCode, location;

    public AttendanceListDetails(String attendanceID, String userID, String subjectCode, String type, String date, String time, String qrCode, String location){
        this.attendanceID = attendanceID;
        this.userID = userID;
        this.subjectCode = subjectCode;
        this.type = type;
        this.date = date;
        this.time = time;
        this.qrCode = qrCode;
        this.location = location;
    }

    public String getAttendanceID(){
        return attendanceID;
    }

    public String getUserID(){
        return userID;
    }

    public String getSubjectCode(){
        return subjectCode;
    }

    public String getType(){
        return type;
    }

    public String getDate(){
        return date;
    }

    public String getTime(){
        return time;
    }

    public String getQrCode(){
        return qrCode;
    }

    public String getLocation(){
        return location;
    }
}


