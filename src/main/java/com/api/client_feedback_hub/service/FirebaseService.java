package com.api.client_feedback_hub.service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.stereotype.Service;

@Service
public class FirebaseService {
    private DatabaseReference databaseReference;

    public FirebaseService() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
    }
}
