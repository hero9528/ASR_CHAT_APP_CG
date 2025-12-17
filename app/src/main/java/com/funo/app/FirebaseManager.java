package com.funo.app;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseManager {
    private static FirebaseManager instance;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private FirebaseManager() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    public void createUser(String email, String password, String username, String avatar, OnUserCreatedListener listener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        UserModel user = new UserModel(firebaseUser.getUid(), username, email, avatar);
                        db.collection("users").document(firebaseUser.getUid()).set(user)
                                .addOnSuccessListener(aVoid -> listener.onUserCreated(user))
                                .addOnFailureListener(e -> listener.onError(e.getMessage()));
                    } else {
                        listener.onError(task.getException().getMessage());
                    }
                });
    }

    public void getUser(String uid, OnUserRetrievedListener listener) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserModel user = documentSnapshot.toObject(UserModel.class);
                        listener.onUserRetrieved(user);
                    } else {
                        listener.onUserNotFound();
                    }
                })
                .addOnFailureListener(e -> listener.onError(e.getMessage()));
    }

    public interface OnUserCreatedListener {
        void onUserCreated(UserModel user);
        void onError(String error);
    }

    public interface OnUserRetrievedListener {
        void onUserRetrieved(UserModel userModel);
        void onUserNotFound();
        void onError(String error);
    }
}