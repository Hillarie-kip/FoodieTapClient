package com.techkip.foodietapclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.techkip.foodietapclient.common.Common;
import com.techkip.foodietapclient.model.User;

import dmax.dialog.SpotsDialog;
import info.hoang8f.widget.FButton;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    FButton btnSignUp,btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        btnSignUp=findViewById(R.id.btn_signUp);
        btnSignIn=findViewById(R.id.btn_signIn);
        Paper.init(this);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sign = new Intent(MainActivity.this,SigninActivity.class);
                startActivity(sign);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUp = new Intent(MainActivity.this,SignupActivity.class);
                startActivity(signUp);
            }
        });

        //check remember

        String user =Paper.book().read(Common.USER_KEY);
        String pass =Paper.book().read(Common.PWD_KEY);

        if (user!=null && pass !=null){
            if (!user.isEmpty() && !pass.isEmpty())
                login(user,pass);
        }
    }

    private void login(final String phone, final String pass) {

        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        final DatabaseReference table_users = db.getReference("Users");
        if (Common.isConnectedToInternet(getBaseContext())) {


            final android.app.AlertDialog pd = new SpotsDialog(MainActivity.this);
            pd.show();
            pd.setMessage("authenticating....");
            pd.setCancelable(false);

            table_users.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(phone).exists()) {

                        pd.dismiss();
                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone);

                        if (user.getPassword().equals(pass)) {

                            Intent main = new Intent(MainActivity.this, Home.class);
                            Common.currentUser = user;
                            startActivity(main);
                            finish();
                            Toast.makeText(MainActivity.this, "successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "unable to sign in", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        pd.dismiss();
                        Toast.makeText(MainActivity.this, "user not found", Toast.LENGTH_SHORT).show();
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(MainActivity.this, "check your internet connection", Toast.LENGTH_SHORT).show();
            return;
        }
    }


}
