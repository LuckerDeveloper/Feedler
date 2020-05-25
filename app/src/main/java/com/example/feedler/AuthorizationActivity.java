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

        authButtonVK.setOnClickListener(this);



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
                Log.e("AuthActivity", "i am here");
                Toast.makeText(this, "Данная функция находится в разработке", Toast.LENGTH_LONG).show();
                break;
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

    public void onClickTwitter(View view) {
        Toast.makeText(this, "Данная функция находится в разработке", Toast.LENGTH_LONG).show();
    }
}
