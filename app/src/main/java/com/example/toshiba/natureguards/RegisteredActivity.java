package com.example.toshiba.natureguards;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

public class RegisteredActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);

        DatabaseHandler db = new DatabaseHandler(getApplicationContext());

        HashMap user = new HashMap();
        user = db.getUserDetails();

        /**
         * Displays the registration details in Text view
         **/

        final TextView fname = (TextView) findViewById(R.id.fname);
        final TextView lname = (TextView) findViewById(R.id.lname);
        final TextView uname = (TextView) findViewById(R.id.uname);
        final TextView email = (TextView) findViewById(R.id.email);
        final TextView created_at = (TextView) findViewById(R.id.regat);
        fname.setText((Integer) user.get("fname"));
        lname.setText((Integer) user.get("lname"));
        uname.setText((Integer) user.get("uname"));
        email.setText((Integer) user.get("email"));
        created_at.setText((Integer) user.get("created_at"));

        Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), LoginActivity.class);
                startActivityForResult(myIntent, 0);
                finish();
            }

        });

    }
}
