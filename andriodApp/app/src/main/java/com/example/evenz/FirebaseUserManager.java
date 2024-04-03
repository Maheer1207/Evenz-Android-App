package com.example.evenz;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseUserManager {

    private final FirebaseFirestore db;
    private final CollectionReference ref;

    public FirebaseUserManager() {
        this.db = FirebaseFirestore.getInstance();
        this.ref = db.collection("users");
    }

    public FirebaseUserManager(FirebaseFirestore db) {
        this.db = db;
        this.ref = db.collection("users");
    }

    public void submitUser(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", user.getName());
        userMap.put("profilePicID", user.getProfilePicID());
        userMap.put("phone", user.getPhone());
        userMap.put("email", user.getEmail());
        userMap.put("userId", user.getUserId());
        userMap.put("userType", user.getUserType());
        userMap.put("eventsSignedUpFor", user.getEventsSignedUpFor());

        String documentId = ref.document().getId();
        db.collection("users").document(documentId).set(userMap)
                .addOnSuccessListener(aVoid -> Log.d("submitUser", "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w("submitUser", "Error writing document", e));
    }

    public Task<List<User>> getAllUsers() {
        final List<User> userList = new ArrayList<>();

        return db.collection("users").get().continueWith(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = new User(
                            document.getString("name"),
                            document.getString("profilePicID"),
                            document.getString("phone"),
                            document.getString("email"),
                            document.getString("userId"),
                            document.getString("userType")
                    );
                    // Initialize the eventsSignedUpFor ArrayList if it exists in the document
                    List<String> eventsSignedUpFor = (List<String>) document.get("eventsSignedUpFor");
                    if (eventsSignedUpFor != null) {
                        user.setEventsSignedUpFor(new ArrayList<>(eventsSignedUpFor));
                    }
                    userList.add(user);
                }
            }
            return userList;
        });
    }

    // Additional methods for user management can be added here...
}
