package com.techkip.foodietapclient;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;
import com.techkip.foodietapclient.Database.Database;
import com.techkip.foodietapclient.common.Common;
import com.techkip.foodietapclient.model.Food;
import com.techkip.foodietapclient.model.Order;
import com.techkip.foodietapclient.model.Rating;

import java.util.Arrays;

public class FoodDetail extends AppCompatActivity implements RatingDialogListener {

    TextView txtFoodName, txtFoodPrice, txtFoodDescription, txtFoodOffer;
    ImageView ivFoodImage;

    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart, btnRating;
    ElegantNumberButton numberButton;
    RatingBar ratingBar;
    String foodId = "";

    FirebaseDatabase database;
    DatabaseReference foods;
    DatabaseReference ratingsTbl;

    Food currentFood;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Food");
        ratingsTbl = database.getReference("Ratings");

        //init view
        txtFoodName = findViewById(R.id.txt_foodName);
        txtFoodPrice = findViewById(R.id.txt_foodPrice);
        txtFoodDescription = findViewById(R.id.txt_foodDescription);
        txtFoodOffer = findViewById(R.id.txt_foodOffer);
        ivFoodImage = findViewById(R.id.iv_foodImage);
        btnCart = findViewById(R.id.fab_cart);
        numberButton = findViewById(R.id.number_button);
        btnRating = findViewById(R.id.fab_rate);
        ratingBar = findViewById(R.id.ratingBar);

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRatingDialog();
            }
        });


        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        //get id from intent FoodList aCTVITY

        if (getIntent() != null) {
            foodId = getIntent().getStringExtra("FoodId");

            if (!foodId.isEmpty() && foodId != null) {

                if (Common.isConnectedToInternet(this)) {
                    loadDetailFood(foodId);
                    getRatingFood(foodId);

                } else {
                    Toast.makeText(this, "check your internet connection", Toast.LENGTH_SHORT).show();
                    finish();

                }

            }
        }

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Database(getBaseContext()).addToCart(new Order(
                        foodId,
                        currentFood.getName(),
                        numberButton.getNumber(),
                        currentFood.getPrice(),
                        currentFood.getDiscount()
                ));
                Toast.makeText(FoodDetail.this, "Added to Cart", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getRatingFood(String foodId) {
        Query foodRating = ratingsTbl.orderByChild("foodId").equalTo(foodId);
        foodRating.addValueEventListener(new ValueEventListener() {
            int count= 0,sum=0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnap:dataSnapshot.getChildren())
                {
                    Rating item = postSnap.getValue(Rating.class);
                    sum+=Integer.parseInt(item.getRateValue());
                    count++;
                }

                if (count !=0){
                    float average = sum/count;
                    ratingBar.setRating(average);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("very bad", "Not Good", "Quite Okay", "Very Good", "Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this product")
                .setDescription("select a star and give feedback")
                .setTitleTextColor(R.color.colorAccent)
                .setCommentBackgroundColor(R.color.colorPrimaryLight)
                .setCommentTextColor(R.color.white)
                .setDescriptionTextColor(R.color.blue)
                .setHint("Your comment goes here...")
                .setHintTextColor(R.color.red)
               .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(FoodDetail.this)
                .show();
    }

    private void loadDetailFood(String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentFood = dataSnapshot.getValue(Food.class);
                Picasso.with(getBaseContext()).load(currentFood.getImage()).into(ivFoodImage);

                collapsingToolbarLayout.setTitle(currentFood.getName());

                txtFoodName.setText(currentFood.getName());
                txtFoodPrice.setText(currentFood.getPrice());
                txtFoodOffer.setText(currentFood.getDiscount());
                txtFoodDescription.setText(currentFood.getDescription());


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPositiveButtonClicked(int value, String comments) {
        final Rating rating = new Rating(Common.currentUser.getPhone(), foodId, String.valueOf(value), comments);
        ratingsTbl.child(Common.currentUser.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Common.currentUser.getPhone()).exists()) {
                    //remove old
                    ratingsTbl.child(Common.currentUser.getPhone()).removeValue();
                    //update new value
                    ratingsTbl.child(Common.currentUser.getPhone()).setValue(rating);
                } else {
                    ratingsTbl.child(Common.currentUser.getPhone()).setValue(rating);
                }
                Toast.makeText(FoodDetail.this, "Thanks for your ratings", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onNegativeButtonClicked() {

    }
}
