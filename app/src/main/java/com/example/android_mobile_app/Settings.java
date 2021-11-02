package com.example.android_mobile_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Settings extends AppCompatActivity {

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView uuidTextView = (TextView)findViewById(R.id.uuid);

        sharedPref = getSharedPreferences("settings", MODE_PRIVATE);

        String uuid = sharedPref.getString("UUID", "uuid not specified");

        uuidTextView.setText(uuid);
    }

    public void saveSettings(View view) {
        sharedPref.edit().putString("API-E4", "2ab3aae6f9784e1baa5ffa826aa65bb5").apply();
    }
}