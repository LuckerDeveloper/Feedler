package com.example.feedler;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String[] scope = new String[]{VKScope.FRIENDS, VKScope.WALL};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button authButtonVK = findViewById(R.id.authButtonVK);
        Button authButtonFB = findViewById(R.id.authButtonFB);
        Button listButton = findViewById(R.id.listButton);

        authButtonVK.setOnClickListener(this);
        authButtonFB.setOnClickListener(this);
        listButton.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.authButtonVK:
                if (VKSdk.isLoggedIn()) {
                    Toast.makeText(getApplicationContext(),"Регистрация уже произведена",Toast.LENGTH_LONG).show();
                } else {
                    VKSdk.login(this, scope);
                }
                break;
            case R.id.authButtonFB:
                //Регистрация в Facebook
                break;
            case R.id.listButton:
                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                startActivity(intent);
        }

    }
}
