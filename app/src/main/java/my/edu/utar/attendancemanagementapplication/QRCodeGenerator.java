package my.edu.utar.attendancemanagementapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class QRCodeGenerator extends AppCompatActivity {

    private ImageView qrCodeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_generator);

        qrCodeImageView = findViewById(R.id.qr_code_image_view);

        String attendanceId = getIntent().getStringExtra("retrievedAttendanceID");

        // generate the QR code
        Bitmap qrCode = generateQrCode(attendanceId);

        // display the QR code in the ImageView
        qrCodeImageView.setImageBitmap(qrCode);

        // show success dialog
        showSuccessDialog(attendanceId);


    }

    private Bitmap generateQrCode(String attendanceId) {
        // create a QR code writer
        QRCodeWriter writer = new QRCodeWriter();

        try {
            // encode the attendance ID as a bit matrix
            BitMatrix bitMatrix = writer.encode(attendanceId, BarcodeFormat.QR_CODE, 512, 512);

            // convert the bit matrix to a bitmap
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showSuccessDialog(String attendanceId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("QR code for attendance ID " + attendanceId + " has been generated successfully!")
                .setTitle("QR code generated")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // dismiss the dialog
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}



