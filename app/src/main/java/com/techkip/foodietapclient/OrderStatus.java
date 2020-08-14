package com.techkip.foodietapclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.techkip.foodietapclient.ViewHolder.OrderViewHolder;
import com.techkip.foodietapclient.common.Common;
import com.techkip.foodietapclient.model.Request;


public class OrderStatus extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference requests;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request,OrderViewHolder> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status_list);

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        //load menu
        recyclerView = findViewById(R.id.lst_orders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //  Toast.makeText(this, ""+Common.currentUser.getPhone(), Toast.LENGTH_LONG).show();
        if (getIntent()==null)
            loadOrders(Common.currentUser.getPhone());
        else
            loadOrders(getIntent().getStringExtra("userPhone"));

    }

    private void loadOrders(String phone) {
        adapter  = new  FirebaseRecyclerAdapter<Request,OrderViewHolder>(Request.class,R.layout.model_order,OrderViewHolder.class,requests.orderByChild("phone").equalTo(phone))

        {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderPhone.setText(model.getPhone());

            }
        };
        recyclerView.setAdapter(adapter);

    }


}
