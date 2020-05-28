package com.example.feedler;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;


public class AuthorizationActivity extends AppCompatActivity implements View.OnClickListener {
    private String[] scope = new String[]{VKScope.FRIENDS, VKScope.WALL};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        Button authButtonVK = findViewById(R.id.authButtonVK);
        Button authButtonTwitter=findViewById(R.id.authButtonTwitter);
        Button news_feed_button= findViewById(R.id.news_feed_button);

        authButtonVK.setOnClickListener(this);
        authButtonTwitter.setOnClickListener(this);
        news_feed_button.setOnClickListener(this);

        Bundle arguments = getIntent().getExtras();
        if (arguments!=null){
            boolean visibleNewsFeedButton= arguments.getBoolean(MainActivity.ACCESS_TO_FEED, false);
            if(visibleNewsFeedButton) {
                news_feed_button.setVisibility(View.VISIBLE);
            }
        }





    }
   @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.authButtonVK: {
                if (VKSdk.isLoggedIn()) {
                    Toast.makeText(getApplicationContext(), "Регистрация уже произведена", Toast.LENGTH_LONG).show();
                } else {
                    VKSdk.login(this, scope);
                }
                break;
            }
            case R.id.authButtonTwitter: {
                Toast.makeText(this, "Данная функция находится в разработке", Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.news_feed_button: {
                Intent intent =new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKCallback<VKAccessToken> callback = new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onError(VKError error) {

            }
        };

        if (!VKSdk.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


}
