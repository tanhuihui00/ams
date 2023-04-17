package my.edu.utar.attendancemanagementapplication;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setContentView(R.layout.activity_main);

        Button mapBtn = findViewById(R.id.mapButton);
        Button QRBtn = findViewById(R.id.QRButton);
        Button ScanQRBtn = findViewById(R.id.ScanQRButton);

        mapBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(TestActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        QRBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(TestActivity.this, QRCodeGenerator.class);
                startActivity(intent);
            }
        });

        ScanQRBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(TestActivity.this, QRCodeScanner.class);
                startActivity(intent);
            }
        });*/
    }
}