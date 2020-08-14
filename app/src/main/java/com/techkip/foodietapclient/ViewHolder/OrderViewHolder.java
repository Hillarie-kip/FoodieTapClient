package com.techkip.foodietapclient.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.techkip.foodietapclient.Interface.ItemClickListener;
import com.techkip.foodietapclient.R;

/**
 * Created by hillarie on 29/05/2018.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtOrderId,txtOrderStatus,txtOrderPhone,txtOrderAddress;

    private ItemClickListener itemClickListener;

    public OrderViewHolder(View itemView) {
        super(itemView);
        txtOrderId = itemView.findViewById(R.id.txt_orderId) ;
        txtOrderStatus = itemView.findViewById(R.id.txt_orderStatus) ;
        txtOrderPhone = itemView.findViewById(R.id.txt_orderPhone) ;
        txtOrderAddress = itemView.findViewById(R.id.txt_orderAddress) ;
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
