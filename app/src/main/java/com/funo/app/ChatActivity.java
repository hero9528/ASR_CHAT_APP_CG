package com.funo.app;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.funo.app.entities.Message;
import com.funo.app.websocket.WebSocketManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements WebSocketManager.MessageListener {

    private WebSocketManager webSocketManager;
    private RecyclerView messagesRecyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messageList = new ArrayList<>();
    private EditText messageInput;
    private ImageButton sendButton;
    private long lastMessageTime = 0;
    private static final long MESSAGE_COOLDOWN = 3000; // 3 seconds

    private ImageView blurredBackground;
    private de.hdodenhof.circleimageview.CircleImageView floatingAvatar;

    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(getApplicationContext());

        blurredBackground = findViewById(R.id.blurred_background);
        floatingAvatar = findViewById(R.id.floating_avatar);

        String userAvatarUrl = sessionManager.getUserAvatar();

        Glide.with(this)
                .load(userAvatarUrl)
                .transform(new CircleCrop())
                .into(floatingAvatar);

        Glide.with(this)
                .load(userAvatarUrl)
                .into(blurredBackground);

        messagesRecyclerView = findViewById(R.id.messages_recycler_view);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);

        setupRecyclerView();

        webSocketManager = new WebSocketManager(this);
        joinRandomRoom();

        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void joinRandomRoom() {
        String userId = sessionManager.getUserId();
        apiService.joinRandomRoom(new JoinRoomRequest(userId)).enqueue(new Callback<RoomResponse>() {
            @Override
            public void onResponse(Call<RoomResponse> call, Response<RoomResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String roomId = response.body().getRoomId();
                    webSocketManager.connect(roomId);
                } else {
                    Toast.makeText(ChatActivity.this, "Failed to join a room.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RoomResponse> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter(messageList, this);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(chatAdapter);
    }

    private void sendMessage() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMessageTime < MESSAGE_COOLDOWN) {
            Toast.makeText(this, "You must wait longer between chat message posts", Toast.LENGTH_SHORT).show();
            return;
        }

        String messageText = messageInput.getText().toString().trim();
        if (!messageText.isEmpty()) {
            webSocketManager.sendMessage(messageText);
            messageInput.setText("");
            lastMessageTime = currentTime;
        }
    }

    @Override
    public void onMessage(Message message) {
        runOnUiThread(() -> {
            messageList.add(message);
            chatAdapter.notifyItemInserted(messageList.size() - 1);
            messagesRecyclerView.scrollToPosition(messageList.size() - 1);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webSocketManager.disconnect();
    }
}