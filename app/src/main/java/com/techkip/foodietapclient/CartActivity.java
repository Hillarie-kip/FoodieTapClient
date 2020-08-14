package com.techkip.foodietapclient;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.techkip.foodietapclient.Database.Database;
import com.techkip.foodietapclient.Remote.APIService;
import com.techkip.foodietapclient.ViewHolder.CartAdapter;
import com.techkip.foodietapclient.common.Common;
import com.techkip.foodietapclient.model.MyResponse;
import com.techkip.foodietapclient.model.Notification;
import com.techkip.foodietapclient.model.Order;
import com.techkip.foodietapclient.model.Request;
import com.techkip.foodietapclient.model.Sender;
import com.techkip.foodietapclient.model.Token;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    TextView txtTotalCash;
    FButton btnPlaceOrder;

    List<Order> carts = new ArrayList<>();
    CartAdapter adapter;
    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);
        mService=Common.getFCMService();

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        //load menu
        recyclerView = findViewById(R.id.list_cart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtTotalCash = findViewById(R.id.total_cash);
        btnPlaceOrder = findViewById(R.id.btn_placeOrder);


        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (carts.size() > 0)
                    showAlert();
                else
                    Toast.makeText(CartActivity.this, "Cart is empty", Toast.LENGTH_SHORT).show();

            }
        });
        LoadListOrder();
    }

    private void showAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CartActivity.this);
        alertDialog.setTitle("Last Step");
        alertDialog.setMessage("Enter Your Address");

        LayoutInflater layoutInflater =this.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.order_address_comment,null);

       final MaterialEditText etAddress = view.findViewById(R.id.et_address);
       final MaterialEditText etComment = view.findViewById(R.id.et_comment);



        alertDialog.setView(view);
        alertDialog.setIcon(R.mipmap.ic_cart);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Request request = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        etAddress.getText().toString(),
                        txtTotalCash.getText().toString(),
                        "0",
                        etComment.getText().toString(),carts);


                //submit to firebase
                //we will use system,curee milis to key;
                String order_num=String.valueOf(System.currentTimeMillis());
                requests.child(order_num).setValue(request);

                //delete cart
                new Database(getBaseContext()).clearCart();

                sendNotification(order_num);
                Toast.makeText(CartActivity.this, "Thank You for your Order", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        alertDialog.show();
    }

    private void sendNotification(final String order_num) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("isServerToken").equalTo(true); //get all node with isServerToken true
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot:dataSnapshot.getChildren()){
                    Token serverToken = postSnapShot.getValue(Token .class);

                    //create raw payload to send
                    Notification notification = new Notification("FoodieTap","You have new order"+order_num);
                    Sender content = new Sender(serverToken.getToken(),notification);

                    mService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success == 1) {
                                            Toast.makeText(CartActivity.this, "Thank You for your Order", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(CartActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                    Log.e("Error",t.getMessage());

                                }
                            });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void LoadListOrder() {
        carts = new Database(this).getCarts();
        adapter = new CartAdapter(carts, this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        //calc total price
        int total = 0;
        for (Order order : carts)
            total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));

        Locale locale = new Locale("en", "KE");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        txtTotalCash.setText(fmt.format(total));

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());

        return true;
    }

    private void deleteCart(int position) {
        carts.remove(position);
        new Database(this).clearCart();

        for (Order item : carts)
            new Database(this).addToCart(item);
        //refresh
        LoadListOrder();
    }
}
