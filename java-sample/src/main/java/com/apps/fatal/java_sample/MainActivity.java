package com.apps.fatal.java_sample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.hse.auth.AuthHelper;
import com.hse.auth.models.MeDataEntity;
import com.hse.auth.models.TokensModel;
import com.hse.auth.utils.AuthConstants;

public class MainActivity extends AppCompatActivity {
    private String TAG = "authHelperJavaDebug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AuthHelper.init(this);

        Button loginButton = findViewById(R.id.loginBtn);

        MainActivity ctx = this;
        loginButton.setOnClickListener(view -> AuthHelper.login(ctx, 1));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String accessToken = data.getStringExtra(AuthConstants.KEY_ACCESS_TOKEN);
            String refreshToken = data.getStringExtra(AuthConstants.KEY_REFRESH_TOKEN);

            Log.e(TAG, refreshToken);
            Log.e(TAG, String.valueOf(AuthHelper.accessTokenIsFresh(accessToken)));

            AuthHelper.refreshToken(refreshToken, new AuthHelper.OnTokenCallback() {
                @Override
                public void onResult(@NonNull TokensModel model) {
                    Log.d(TAG, model.getAccessToken());
                }

                @Override
                public void onError(@NonNull Exception e) {
                    // Обработка успешного результата
                }
            });

            AuthHelper.getMe(accessToken, new AuthHelper.OnMeCallback() {
                @Override
                public void onResult(@NonNull MeDataEntity model) {
                    Log.d(TAG, model.getFullName());
                }

                @Override
                public void onError(@NonNull Exception e) {
                    // Обработка успешного результата
                }
            });
        }
    }
}