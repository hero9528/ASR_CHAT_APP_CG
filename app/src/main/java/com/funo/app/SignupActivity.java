package com.funo.app;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private static final String PREF_NAME = "signup_pref";
    private static final String KEY_EMAIL = "email";
    private static final int REQUEST_CODE_PICK_ACCOUNT = 1;

    private EditText mEmailInput, mPasswordInput, mUsernameInput;
    private ImageView mEmailStatusIcon, mPasswordStatusIcon, mUsernameStatusIcon;
    private Button mSignupButton;
    private ApiService apiService;
    private SessionManager sessionManager;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(getApplicationContext());
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        mEmailInput = findViewById(R.id.email_input);
        mPasswordInput = findViewById(R.id.password_input);
        mUsernameInput = findViewById(R.id.username_input);
        mSignupButton = findViewById(R.id.signup_button);
        mEmailStatusIcon = findViewById(R.id.email_status_icon);
        mPasswordStatusIcon = findViewById(R.id.password_status_icon);
        mUsernameStatusIcon = findViewById(R.id.username_status_icon);

        // Load saved email
        String savedEmail = sharedPreferences.getString(KEY_EMAIL, null);
        if (savedEmail != null) {
            mEmailInput.setText(savedEmail);
        }

        mEmailInput.setOnClickListener(v -> chooseAccount());

        mEmailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    mEmailStatusIcon.setImageResource(R.drawable.ic_edit);
                } else if (isEmailValid(s.toString())) {
                    mEmailStatusIcon.setImageResource(R.drawable.ic_check);
                } else {
                    mEmailStatusIcon.setImageResource(R.drawable.ic_cross);
                }
                updateSignupButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isPasswordValid(s.toString())) {
                    mPasswordStatusIcon.setImageResource(R.drawable.ic_check);
                } else {
                    mPasswordStatusIcon.setImageResource(R.drawable.ic_cross);
                }
                updateSignupButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mUsernameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUsernameValid(s.toString())) {
                    mUsernameStatusIcon.setImageResource(R.drawable.ic_check);
                } else {
                    mUsernameStatusIcon.setImageResource(R.drawable.ic_cross);
                }
                updateSignupButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mSignupButton.setOnClickListener(v -> signupUser());
    }

    private void chooseAccount() {
        Intent intent = AccountManager.newChooseAccountIntent(null, null, new String[]{"com.google"}, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            if (resultCode == RESULT_OK && data != null) {
                String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                if (accountName != null) {
                    mEmailInput.setText(accountName);
                    sharedPreferences.edit().putString(KEY_EMAIL, accountName).apply();
                }
            } else {
                Toast.makeText(this, "No account selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showAccountPicker() {
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccountsByType("com.google");
        Set<String> emailSet = new HashSet<>();
        for (Account account : accounts) {
            if (Patterns.EMAIL_ADDRESS.matcher(account.name).matches()) {
                emailSet.add(account.name);
            }
        }

        if (emailSet.isEmpty()) {
            Toast.makeText(this, "No Google accounts found on this device.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> emails = new ArrayList<>(emailSet);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, emails);

        new AlertDialog.Builder(this)
                .setTitle("Choose an email")
                .setAdapter(adapter, (dialog, which) -> {
                    String email = emails.get(which);
                    mEmailInput.setText(email);
                    // Save the selected email
                    sharedPreferences.edit().putString(KEY_EMAIL, email).apply();
                })
                .create()
                .show();
    }

    private void updateSignupButtonState() {
        boolean isValid = isEmailValid(mEmailInput.getText().toString()) &&
                isPasswordValid(mPasswordInput.getText().toString()) &&
                isUsernameValid(mUsernameInput.getText().toString());
        mSignupButton.setEnabled(isValid);
        if (isValid) {
            mSignupButton.setVisibility(View.VISIBLE);
        } else {
            mSignupButton.setVisibility(View.GONE);
        }
    }

    private boolean isEmailValid(String email) {
        if (email.isEmpty()) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    private boolean isUsernameValid(String username) {
        return username.length() >= 4 && username.length() <= 16 && !username.contains(" ") && username.equals(username.toLowerCase());
    }

    private void signupUser() {
        String username = mUsernameInput.getText().toString();
        String email = mEmailInput.getText().toString();
        String password = mPasswordInput.getText().toString();

        SignUpRequest signUpRequest = new SignUpRequest(username, email, password);
        apiService.signup(signUpRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sessionManager.saveAuthToken(response.body().getAccessToken());
                    Toast.makeText(SignupActivity.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignupActivity.this, ChatActivity.class));
                    finish();
                } else {
                    Toast.makeText(SignupActivity.this, "Signup failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(SignupActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}