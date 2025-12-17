package com.funo.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashRouterActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_router);

        sessionManager = new SessionManager(getApplicationContext());

        new Handler().postDelayed(() -> {
            if (sessionManager.getAuthToken() != null) {
                // User is logged in, auto-join a room
                autoJoinRoom();
            } else {
                // User is not logged in, go to login screen
                startActivity(new Intent(SplashRouterActivity.this, LoginActivity.class));
                finish();
            }
        }, 1000); // 1 second delay
    }

    private void autoJoinRoom() {
        ApiService apiService = ApiClient.getApiService();
        String authToken = "Bearer " + sessionManager.getAuthToken();

        apiService.autoJoin(authToken).enqueue(new retrofit2.Callback<AutoJoinResponse>() {
            @Override
            public void onResponse(retrofit2.Call<AutoJoinResponse> call, retrofit2.Response<AutoJoinResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AutoJoinResponse autoJoinResponse = response.body();
                    Intent intent = new Intent(SplashRouterActivity.this, ChatActivity.class);
                    intent.putExtra("room_id", autoJoinResponse.getRoomId());
                    intent.putExtra("room_name", autoJoinResponse.getRoomName());
                    startActivity(intent);
                    finish();
                } else {
                    // Handle error, e.g., token expired
                    sessionManager.logoutUser();
                    startActivity(new Intent(SplashRouterActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<AutoJoinResponse> call, Throwable t) {
                // Handle failure
                startActivity(new Intent(SplashRouterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}