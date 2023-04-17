package my.edu.utar.attendancemanagementapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    Button viewattendance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        viewattendance=findViewById(R.id.view_attendance_button);
        viewattendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity2.this, StudentViewAttendance.class);
                startActivity(intent);
            }
        });

        Button scanAttendanceBtn = findViewById(R.id.scan_attendance_button);
        scanAttendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, QRCodeScanner.class);
                startActivity(intent);
            }
        });

    }
}