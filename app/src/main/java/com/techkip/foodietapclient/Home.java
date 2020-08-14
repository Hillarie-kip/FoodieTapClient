package com.techkip.foodietapclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;
import com.techkip.foodietapclient.Interface.ItemClickListener;
import com.techkip.foodietapclient.ViewHolder.MenuViewHolder;
import com.techkip.foodietapclient.common.Common;
import com.techkip.foodietapclient.model.Category;
import com.techkip.foodietapclient.model.Token;

import io.paperdb.Paper;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference category;

    TextView FullName;
    RecyclerView recyclerMenu;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Category,MenuViewHolder> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//Init FIREBASE
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Categories");

        Paper.init(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main = new Intent(Home.this,CartActivity.class);
                startActivity(main);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //display name
        View header = navigationView.getHeaderView(0);
        FullName = header.findViewById(R.id.txtFullName);
        FullName.setText(Common.currentUser.getName());

        //load menu
        recyclerMenu = findViewById(R.id.recycler_menu);
        recyclerMenu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerMenu.setLayoutManager(layoutManager);
        if (Common.isConnectedToInternet(this))
            loadMenu();
        else {
            Toast.makeText(this, "check your internet connection", Toast.LENGTH_SHORT).show();
            return;
        }
updateToken(FirebaseInstanceId.getInstance().getToken());



    }

    private void updateToken(String token) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens= db.getReference("Tokens");
        Token data = new Token(token,false); //false reason token send fropm client app
        tokens.child(Common.currentUser.getPhone()).setValue(data);
    }

    private void loadMenu() {
        adapter  = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class,R.layout.model_menu,MenuViewHolder.class,category) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Category model, int position) {
                viewHolder.txtMenuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.ivMenuImage);

                final Category clickItem = model;
                viewHolder.setItemClickListener(new ItemClickListener(){

                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //get categoryid
                        //Toast.makeText(Home.this, ""+clickItem.getName(), Toast.LENGTH_SHORT).show();
                     Intent foodList = new Intent(Home.this,FoodList.class);
                        foodList.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(foodList);
                      //  Toast.makeText(Home.this, ""+adapter.getRef(position).getKey(), Toast.LENGTH_SHORT).show();
                       // Toast.makeText(Home.this, ""+adapter.getRef(position).getKey(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
    recyclerMenu.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            loadMenu();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Handle the camera action
        } else if (id == R.id.nav_menu) {

        } else if (id == R.id.nav_cart) {
            Intent cart = new Intent(Home.this,CartActivity.class);
            startActivity(cart);
        } else if (id == R.id.nav_orders) {
            Intent Orde = new Intent(Home.this,OrderStatusList.class);
            startActivity(Orde);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {
            Paper.book().destroy();
            Intent signin = new Intent(Home.this,SigninActivity.class);
            signin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity(signin);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
