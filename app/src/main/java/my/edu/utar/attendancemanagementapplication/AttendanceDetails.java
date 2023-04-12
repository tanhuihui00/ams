package my.edu.utar.attendancemanagementapplication;

import android.app.ProgressDialog;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import my.edu.utar.attendancemanagementapplication.databinding.ActivityAttendanceDetailsBinding;

public class AttendanceDetails extends AppCompatActivity {
    ActivityAttendanceDetailsBinding binding;
    Handler handler=new Handler();
    String classcode,id,type;
    ListAdapterAttendance listAdapter;
    ArrayList<AttendanceClass> attendances=new ArrayList<>();
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_details);
    }
}