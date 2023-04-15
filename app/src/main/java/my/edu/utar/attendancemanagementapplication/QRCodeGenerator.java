package my.edu.utar.attendancemanagementapplication;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRCodeGenerator extends AppCompatActivity {
    private EditText sessionNameEditText;
    private EditText sessionDateEditText;
    private EditText sessionTimeEditText;
    private Button createSessionButton;
    private ImageView qrCodeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_generator);
        sessionNameEditText = findViewById(R.id.session_name_edittext);
        sessionDateEditText = findViewById(R.id.session_date_edittext);
        sessionTimeEditText = findViewById(R.id.session_time_edittext);
        createSessionButton = findViewById(R.id.create_session_button);
        qrCodeImageView = findViewById(R.id.qr_code_imageview);


        createSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = sessionNameEditText.getText().toString();
                String date = sessionDateEditText.getText().toString();
                String time = sessionTimeEditText.getText().toString();

                // generate the QR code
                Bitmap qrCode = generateQrCode(name, date, time);

                // create a new Session object
                Class_Session session = new Class_Session(name, date, time, qrCode);

                // TODO: store the session in the database

                // show an alert dialog to indicate that the session was created successfully
                AlertDialog.Builder builder = new AlertDialog.Builder(QRCodeGenerator.this);
                builder.setTitle("Success")
                        .setMessage("Class session created successfully.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO: handle OK button click
                            }
                        })
                        .show();

                // display the QR code in the ImageView
                qrCodeImageView.setImageBitmap(qrCode);
            }
        });
    }

    private Bitmap generateQrCode(String name, String date, String time) {
        // concatenate the session details into a single string
        //String sessionDetails = name + ", " + date + ", " + time;
        String sessionId = "123456";
        // create a QR code writer
        QRCodeWriter writer = new QRCodeWriter();

        try {
            // encode the session details as a bit matrix
            BitMatrix bitMatrix = writer.encode(sessionId, BarcodeFormat.QR_CODE, 512, 512);

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
}

