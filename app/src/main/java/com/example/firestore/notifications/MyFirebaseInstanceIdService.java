package com.example.firestore.notifications;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService{

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        String tokenRefresh = FirebaseInstanceId.getInstance().getToken();

        Log.d(TAG, "onTokenRefresh: Refreshed token: " + tokenRefresh);

        //send the token to the database
        updateToken(tokenRefresh);
    }

    private void updateToken(String tokenRefresh){
        Log.d(TAG, "sendRegistrationToServer: sending token to server: " + tokenRefresh);

        CollectionReference collection = FirebaseFirestore.getInstance().collection("Tokens");
        Token token = new Token(tokenRefresh);
        collection.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(token);

    }
}


