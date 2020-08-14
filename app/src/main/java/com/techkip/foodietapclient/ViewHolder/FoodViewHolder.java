package com.techkip.foodietapclient.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.techkip.foodietapclient.Interface.ItemClickListener;
import com.techkip.foodietapclient.R;

/**
 * Created by hillarie on 28/05/2018.
 */

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtFoodName;
    public TextView txtFoodPrice;
    public TextView txtFoodDescription;
    public TextView txtFoodOffer;
    public ImageView ivFoodImage;
    public ImageView ivFavmage;
    private ItemClickListener itemClickListener;

    public FoodViewHolder(View itemView) {
        super(itemView);
        txtFoodName = itemView.findViewById(R.id.txt_foodName) ;
       txtFoodPrice = itemView.findViewById(R.id.txt_foodPrice) ;
        txtFoodOffer = itemView.findViewById(R.id.txt_foodOffer) ;
        txtFoodDescription = itemView.findViewById(R.id.txt_foodDescription) ;
        ivFoodImage = itemView.findViewById(R.id.iv_foodImage) ;
        ivFavmage = itemView.findViewById(R.id.iv_fav) ;
        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }


}
