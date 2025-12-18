package com.funo.app;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {

    @POST("/api/auth/signup")
    Call<AuthResponse> signup(@Body SignUpRequest signUpRequest);

    @POST("/api/auth/login")
    Call<AuthResponse> login(@Body LoginRequest loginRequest);

    @POST("/api/room/auto-join")
    Call<AutoJoinResponse> autoJoin(@Header("Authorization") String authToken);

    @POST("/api/join-random-room")
    Call<RoomResponse> joinRandomRoom(@Body JoinRoomRequest request);
}

// DTOs for requests and responses

class SignUpRequest {
    private String username;
    private String email;
    private String password;

    public SignUpRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}

class LoginRequest {
    private String username;
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

class AuthResponse {
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }
}

class JoinRoomRequest {
    private String userId;

    public JoinRoomRequest(String userId) {
        this.userId = userId;
    }
}

class RoomResponse {
    private String roomId;

    public String getRoomId() {
        return roomId;
    }
}