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

        Button viewAttendanceBtn = findViewById(R.id.view_attendance_button);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) { // Same as the request code used while starting the activity
            // Handle the result here
            if (resultCode == RESULT_OK) {
                // User has turned on the location services
                // Get the user's location here
            } else {
                Toast.makeText(MainActivity2.this,"You have to enabled the location service to take the attendance.",Toast.LENGTH_LONG);
            }
        }
    }
}