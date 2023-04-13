package my.edu.utar.attendancemanagementapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
//import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;
//import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.EnumMap;
import java.util.Map;

public class QRCodeGenerator extends AppCompatActivity {
    EditText etQRSource;
    private ImageView ivQRCode;
    private Button btnGenerateQR;
    private Button scanQRButton;
    private String attendanceCode = "YOUR_ATTENDANCE_CODE_HERE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_generator);

        btnGenerateQR = findViewById(R.id.btn_generateQR);
        ivQRCode = findViewById(R.id.iv_qrcode);
        scanQRButton = findViewById(R.id.scan_qr_button);


        btnGenerateQR.setOnClickListener(view -> {
            QRCodeWriter writer = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            try{
                BitMatrix bitMatrix = writer.encode(attendanceCode, BarcodeFormat.QR_CODE, 512, 512, hints);
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                for(int x =  0; x < width; x++){
                    for(int y = 0; y < height; y++){
                        bitmap.setPixel(x, y, bitMatrix.get(x, y) ? getResources().getColor(R.color.black) : getResources().getColor(R.color.white));
                    }
                }
                ivQRCode.setImageBitmap(bitmap);
            } catch(WriterException e){
                e.printStackTrace();
            }
        });

        scanQRButton.setOnClickListener(view -> {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setPrompt("Scan the QR code for attendance");
            integrator.setOrientationLocked(false);
            integrator.initiateScan();
        });
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents() == null){
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show();
            }else{
                if(result.getContents().equals(attendanceCode)){
                    //code for marking attendance in Supabase
                    Toast.makeText(this, "Attendance recorded", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Invalid QR Code", Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}


