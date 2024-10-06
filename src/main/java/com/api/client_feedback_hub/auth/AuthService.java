package com.api.client_feedback_hub.auth;

import com.api.client_feedback_hub.dto.UserRequestDto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public String registerUser(UserRequestDto userRequestDto) throws FirebaseAuthException {
        // Create new user in Firebase auth
        UserRecord.CreateRequest userRecordArgs = new UserRecord.CreateRequest()
                .setEmail(userRequestDto.getEmail())
                .setPassword(userRequestDto.getPassword())
                .setDisplayName(userRequestDto.getName())
                .setPhoneNumber(userRequestDto.getPhoneNumber());

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(userRecordArgs);
        return userRecord.getUid();
    }
}