package my.edu.utar.attendancemanagementapplication;

import android.graphics.Bitmap;

public class Class_Session {
    private String name;
    private String date;
    private String time;
    private Bitmap qrCode;

    public Class_Session(String name, String date, String time, Bitmap qrCode) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.qrCode = qrCode;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public Bitmap getQrCode() {
        return qrCode;
    }
}
