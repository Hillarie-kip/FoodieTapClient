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

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtMenuName;
    public ImageView ivMenuImage;

    private ItemClickListener itemClickListener;
    public MenuViewHolder(View itemView) {
        super(itemView);
        txtMenuName = itemView.findViewById(R.id.txt_menuName) ;
        ivMenuImage = itemView.findViewById(R.id.iv_menuImage) ;
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
