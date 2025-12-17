package com.funo.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RandomRoomActivity extends AppCompatActivity {

    private Button findRandomRoomButton;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_room);

        sessionManager = new SessionManager(this);



    }

    private void findRandomRoom() {
        String authToken = "Bearer " + sessionManager.getAuthToken();
        String userId = sessionManager.getUserId();

        JoinRoomRequest request = new JoinRoomRequest(userId);

        ApiClient.getApiService().joinRandomRoom(request).enqueue(new Callback<RoomResponse>() {
            @Override
            public void onResponse(Call<RoomResponse> call, Response<RoomResponse> response) {
                if (response.isSuccessful()) {
                    String roomId = response.body().getRoomId();
                    Intent intent = new Intent(RandomRoomActivity.this, ChatActivity.class);
                    intent.putExtra("ROOM_ID", roomId);
                    startActivity(intent);
                } else {
                    Toast.makeText(RandomRoomActivity.this, "Failed to find a room", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RoomResponse> call, Throwable t) {
                Toast.makeText(RandomRoomActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}