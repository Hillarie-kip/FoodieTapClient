package com.techkip.foodietapclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;
import com.techkip.foodietapclient.Database.Database;
import com.techkip.foodietapclient.Interface.ItemClickListener;
import com.techkip.foodietapclient.ViewHolder.FoodViewHolder;
import com.techkip.foodietapclient.common.Common;
import com.techkip.foodietapclient.model.Food;

import java.util.ArrayList;
import java.util.List;

public class FoodList extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference foodList;

    RecyclerView recyclerFood;
    RecyclerView.LayoutManager layoutManager;

    String categoryId = "";
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
    //search
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchadapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar searchBar;

    //FAV
    Database localDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        //Init FIREBASE
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Food");
        //localdb
        localDb = new Database(this);
        //load menu
        recyclerFood = findViewById(R.id.recycler_food);
        recyclerFood.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerFood.setLayoutManager(layoutManager);

        if (getIntent() != null) {
            categoryId = getIntent().getStringExtra("CategoryId");

            if (!categoryId.isEmpty() && categoryId != null) {
                if (Common.isConnectedToInternet(getBaseContext()))
                    loadListFood(categoryId);
                else {
                    Toast.makeText(this, "check your internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        //search
        searchBar = findViewById(R.id.search_bar);
        searchBar.setHint("search food");
        loadSuggest();
        searchBar.setLastSuggestions(suggestList);
        searchBar.setCardViewElevation(9);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //when text is type itl list suggest
                List<String> suggest = new ArrayList<>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                searchBar.setLastSuggestions(suggest);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //wen search is closed
                //restore original adapter
                if (!enabled)
                    recyclerFood.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //wen search finished
                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });


    }

    private void startSearch(CharSequence text) {
        searchadapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class, R.layout.model_food, FoodViewHolder.class, foodList.orderByChild("name").equalTo(text.toString())) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                viewHolder.txtFoodName.setText(model.getName());
                viewHolder.txtFoodPrice.setText(model.getPrice());
                viewHolder.txtFoodOffer.setText(model.getDiscount());
                viewHolder.txtFoodDescription.setText(model.getDescription());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.ivFoodImage);

                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {

                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //get
                        //  Toast.makeText(FoodList.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                        foodDetail.putExtra("FoodId", searchadapter.getRef(position).getKey());//sed food id to new activity
                        startActivity(foodDetail);

                    }
                });

            }
        };
        recyclerFood.setAdapter(searchadapter); //set adapter for recycler viw is sesarch results
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
            loadListFood(categoryId);
        }

        return super.onOptionsItemSelected(item);
    }


    private void loadSuggest() {
        foodList.orderByChild("menuId").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Food item = postSnapshot.getValue(Food.class);
                    suggestList.add(item.getName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void loadListFood(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class, R.layout.model_food, FoodViewHolder.class, foodList.orderByChild("menuId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(final FoodViewHolder viewHolder, final Food model, final int position) {
                viewHolder.txtFoodName.setText(model.getName());
                viewHolder.txtFoodPrice.setText(model.getPrice());
                viewHolder.txtFoodOffer.setText(model.getDiscount());
                viewHolder.txtFoodDescription.setText(model.getDescription());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.ivFoodImage);

                //ADD FAV
                if (localDb.isFav(adapter.getRef(position).getKey()))
                    viewHolder.ivFavmage.setImageResource(R.drawable.fav_fill);

                viewHolder.ivFavmage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!localDb.isFav(adapter.getRef(position).getKey()))
                        {
                            localDb.addToFav(adapter.getRef(position).getKey());
                            viewHolder.ivFavmage.setImageResource(R.drawable.fav_fill);
                            Toast.makeText(FoodList.this, ""+model.getName()+"added to favorites", Toast.LENGTH_SHORT).show();
                        }else {
                           /* localDb.removeFav(adapter.getRef(position).getKey());
                            viewHolder.ivFavmage.setImageResource(R.drawable.fav_border);*/
                            Toast.makeText(FoodList.this, ""+model.getName()+"removed from favorites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {

                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //get
                        //  Toast.makeText(FoodList.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                        foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());//sed food id to new activity
                        startActivity(foodDetail);

                    }
                });
            }
        };
        recyclerFood.setAdapter(adapter);

    }
}
