package com.funo.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AvatarPickerActivity extends AppCompatActivity {

    private ImageView avatar1, avatar2, avatar3;
    private TextView monthText, dayText, yearText;
    private Button monthUp, dayUp, yearUp, monthDown, dayDown, yearDown, signupButton;

    private String selectedAvatar = "ic_avatar1"; // Default avatar
    private int month = 1, day = 1, year = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_picker);

        avatar1 = findViewById(R.id.avatar1);
        avatar2 = findViewById(R.id.avatar2);
        avatar3 = findViewById(R.id.avatar3);

        monthText = findViewById(R.id.monthText);
        dayText = findViewById(R.id.dayText);
        yearText = findViewById(R.id.yearText);

        monthUp = findViewById(R.id.monthUp);
        dayUp = findViewById(R.id.dayUp);
        yearUp = findViewById(R.id.yearUp);
        monthDown = findViewById(R.id.monthDown);
        dayDown = findViewById(R.id.dayDown);
        yearDown = findViewById(R.id.yearDown);

        signupButton = findViewById(R.id.signupButton);

        avatar1.setOnClickListener(v -> selectAvatar("ic_avatar1"));
        avatar2.setOnClickListener(v -> selectAvatar("ic_avatar2"));
        avatar3.setOnClickListener(v -> selectAvatar("ic_avatar3"));

        monthUp.setOnClickListener(v -> updateDate(Calendar.MONTH, 1));
        dayUp.setOnClickListener(v -> updateDate(Calendar.DAY_OF_MONTH, 1));
        yearUp.setOnClickListener(v -> updateDate(Calendar.YEAR, 1));
        monthDown.setOnClickListener(v -> updateDate(Calendar.MONTH, -1));
        dayDown.setOnClickListener(v -> updateDate(Calendar.DAY_OF_MONTH, -1));
        yearDown.setOnClickListener(v -> updateDate(Calendar.YEAR, -1));

        signupButton.setOnClickListener(v -> {
            if (isUser17OrOlder()) {
                String username = getIntent().getStringExtra("username");
                String email = getIntent().getStringExtra("email");
                String password = getIntent().getStringExtra("password");

                FirebaseManager.getInstance().createUser(email, password, username, selectedAvatar, new FirebaseManager.OnUserCreatedListener() {
                    @Override
                    public void onUserCreated(UserModel user) {
                        Intent intent = new Intent(AvatarPickerActivity.this, RandomRoomActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(AvatarPickerActivity.this, "Signup Failed: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(AvatarPickerActivity.this, "You must be 17 or older to sign up", Toast.LENGTH_SHORT).show();
            }
        });

        updateDateViews();
    }

    private void selectAvatar(String avatar) {
        selectedAvatar = avatar;
        avatar1.setBackgroundResource(avatar.equals("ic_avatar1") ? R.drawable.avatar_selected_background : 0);
        avatar2.setBackgroundResource(avatar.equals("ic_avatar2") ? R.drawable.avatar_selected_background : 0);
        avatar3.setBackgroundResource(avatar.equals("ic_avatar3") ? R.drawable.avatar_selected_background : 0);
    }

    private void updateDate(int field, int amount) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        cal.add(field, amount);

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        day = cal.get(Calendar.DAY_OF_MONTH);

        updateDateViews();
    }

    private void updateDateViews() {
        monthText.setText(getMonthName(month));
        dayText.setText(String.valueOf(day));
        yearText.setText(String.valueOf(year));
    }

    private String getMonthName(int month) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return months[month - 1];
    }

    private boolean isUser17OrOlder() {
        Calendar today = Calendar.getInstance();
        Calendar dob = Calendar.getInstance();
        dob.set(year, month - 1, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age >= 17;
    }
}