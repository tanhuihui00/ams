package my.edu.utar.attendancemanagementapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class GenerateScanQR extends AppCompatActivity {

    private Button generateQRBtn, scanQRBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_scan_qr);
        generateQRBtn = findViewById(R.id.idBtnGenerateQR);
        scanQRBtn = findViewById(R.id.idbtnScanQR);
        generateQRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GenerateScanQR.this,GenerateQRCodeActivity.class);
                startActivity(i);
            }
        });

        scanQRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GenerateScanQR.this,ScanQRCodeActivity.class);
                startActivity(i);
            }
        });
    }
}