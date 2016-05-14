package com.example.toshiba.natureguards;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SendActivity extends AppCompatActivity {

    private static final String TAG = SendActivity.class.getSimpleName();
    @Bind(R.id.second_r_gr)
    RadioGroup second;
    @Bind(R.id.first_r_gr)
    RadioGroup first;
    @Bind(R.id.edt_txt_location)
    EditText edtTxtLocation;
    @Bind(R.id.edt_txt_description)
    EditText edtTxtDescription;
    @Bind(R.id.btn_send)
    Button btnSend;
    @Bind(R.id.img_send)
    ImageView imgSend;
    @Bind(R.id.cbox_animals_protected)
    RadioButton cBoxAnimalProtected;
    @Bind(R.id.cbox_gorskoto)
    RadioButton cBoxGorskoto;
    @Bind(R.id.cbox_grajdanska)
    RadioButton cBoxgrajdanska;
    @Bind(R.id.cbox_okolona_sreda)
    RadioButton cBoxOkolnaSreda;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Bitmap imgBitmap;
    List<Events> event = new ArrayList<>();
    String getCheckBox;


    Firebase ref;

    boolean isChecking = true;
    int mCheckedId = R.id.cbox_gorskoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        Firebase.setAndroidContext(this);

        ButterKnife.bind(this);

        ref = new Firebase(Config.FIREBASE_URL);

        setCheckedBox();
        checkPermission();
        getCameraIntent();

        btnSend.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {

                                           if (isNetworkAvailable() == false) {
                                               Toast.makeText(getApplicationContext(), "No Network Connection", Toast.LENGTH_LONG).show();
                                           } else if (checkData() == true) {

                                               if (imgBitmap != null) {
                                                   makeFirebase();
                                               }

                                               Intent i = new Intent(SendActivity.this, NavigationDrawerActivity.class);
                                               startActivity(i);

                                               delaySendEmail();
                                           }
                                       }
                                   }

        );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            imgBitmap = (Bitmap) data.getExtras().get("data");
            imgSend.setImageBitmap(imgBitmap);
        }
    }

    public boolean checkData() {
        boolean fl = true;

        if (edtTxtLocation.getText().toString().length() == 0) {
            edtTxtLocation.setError("location is required");
            fl = false;
        }
        if (edtTxtDescription.getText().toString().length() == 0) {
            edtTxtDescription.setError("description is required");
            fl = false;
        }
        if (!cBoxGorskoto.isChecked() &&
                !cBoxgrajdanska.isChecked() &&
                !cBoxAnimalProtected.isChecked() &&
                !cBoxOkolnaSreda.isChecked()) {
            fl = false;
        }

        return fl;
    }

    public String getCheckBox() {
        if (cBoxgrajdanska.isChecked()) {
            getCheckBox = cBoxgrajdanska.getText().toString();
        }
        if (cBoxOkolnaSreda.isChecked()) {
            getCheckBox = cBoxOkolnaSreda.getText().toString();
        }
        if (cBoxAnimalProtected.isChecked()) {
            getCheckBox = cBoxAnimalProtected.getText().toString();
        }
        if (cBoxGorskoto.isChecked()) {
            getCheckBox = cBoxGorskoto.getText().toString();
        }
        return getCheckBox;
    }

    public void setCheckedBox() {
        first.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1 && isChecking) {
                    isChecking = false;
                    second.clearCheck();
                    mCheckedId = checkedId;
                }
                isChecking = true;
            }
        });

        second.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1 && isChecking) {
                    isChecking = false;
                    first.clearCheck();
                    mCheckedId = checkedId;
                }
                isChecking = true;
            }
        });
    }

    public void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        String recipient = "";
        if (cBoxGorskoto.isChecked()) {
            recipient = recipient + " lillymihailova@abv.bg,";
        }
        if (cBoxAnimalProtected.isChecked()) {
            recipient = recipient + " pkatrankiev@gmail.com,";
        }
        if (cBoxOkolnaSreda.isChecked()) {
            recipient = recipient + " nikolay.nikolov92@gmail.com,";
        }
        if (cBoxgrajdanska.isChecked()) {
            recipient = recipient + " sity_teh@abv.bg,";
        }


        emailIntent.setData(Uri.parse("mailto:" + recipient));
//                   emailIntent.putExtra(Intent.EXTRA_STREAM, );
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "imate mail");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "dsfsdfsdf");
        Toast.makeText(SendActivity.this, "alredy send", Toast.LENGTH_SHORT).show();
        Log.e("try", "before try");
        try {
            startActivity(Intent.createChooser(emailIntent, "Choose an Email client :"));
            Log.e("try", "inside");
        } catch (ActivityNotFoundException ex) {
            Log.e("try", "catch");
            Toast.makeText(getApplicationContext(), "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void getCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public void makeFirebase() {
        Log.d(TAG, "Bitmap is not null !");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        imgBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] data = bos.toByteArray();
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);
        final Events events = new Events();
        events.setImg(base64);
        events.setLocation(edtTxtLocation.getText().toString());
        events.setDescription(edtTxtDescription.getText().toString());
        events.setOffice(getCheckBox());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());

        ref.child(currentDateandTime).setValue(events);
    }

    public void delaySendEmail() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendEmail();
            }
        }, 500);

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean checkPermission() {
        int permissionCheck =
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (permissionCheck == PackageManager.PERMISSION_DENIED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                Toast.makeText(SendActivity.this,
                        "Meet Camera permission", Toast.LENGTH_SHORT).show();
            } else {
                requestCameraPermission();
            }
            return false;
        } else {
            return true;
        }
    }

    private void requestCameraPermission() {
        ActivityCompat
                .requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                        1);
    }

}
