package com.techkip.foodietapclient.Service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.techkip.foodietapclient.common.Common;
import com.techkip.foodietapclient.model.Token;

public class MyFirebaseIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String tokenRefresh= FirebaseInstanceId.getInstance().getToken();
        if (Common.currentUser !=null)
        updateTokenToFirebase(tokenRefresh);
    }

    private void updateTokenToFirebase(String tokenRefresh) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens= db.getReference("Tokens");
        Token token = new Token(tokenRefresh,false); //false reason token send fropm client app
        tokens.child(Common.currentUser.getPhone()).setValue(token);
    }
}
