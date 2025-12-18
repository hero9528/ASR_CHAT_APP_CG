package com.funo.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AvatarActivity extends AppCompatActivity {

    private RecyclerView avatarRecyclerView;
    private AvatarAdapter avatarAdapter;
    private Button continueButton;
    private List<Integer> avatarList;
    private int selectedAvatar = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);

        avatarRecyclerView = findViewById(R.id.avatar_recycler_view);
        continueButton = findViewById(R.id.continue_button);

        avatarList = new ArrayList<>();
        avatarList.add(R.drawable.ic_avatar1);
        avatarList.add(R.drawable.ic_avatar2);
        avatarList.add(R.drawable.ic_avatar3);

        avatarAdapter = new AvatarAdapter(this, avatarList);
        avatarRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        avatarRecyclerView.setAdapter(avatarAdapter);

        avatarAdapter.setOnItemClickListener(position -> {
            selectedAvatar = avatarList.get(position);
            // Highlight the selected avatar
        });

        continueButton.setOnClickListener(v -> {
            if (selectedAvatar != -1) {
                // Save the selected avatar and proceed to ChatActivity
                Intent intent = new Intent(AvatarActivity.this, ChatActivity.class);
                intent.putExtra("selected_avatar", selectedAvatar);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(AvatarActivity.this, "Please select an avatar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}