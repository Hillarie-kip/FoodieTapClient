package com.techkip.foodietapclient.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.techkip.foodietapclient.Interface.ItemClickListener;
import com.techkip.foodietapclient.R;
import com.techkip.foodietapclient.common.Common;
import com.techkip.foodietapclient.model.Order;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by hillarie on 29/05/2018.
 */
class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  ,View.OnCreateContextMenuListener {
    public TextView txtOrderItemName;
    public TextView txtOrderItemPrice;
    public ImageView ivOrderItemCount;

    private ItemClickListener itemClickListener;

    public void setTxtOrderName(TextView txtOrderName) {
        this.txtOrderItemName = txtOrderName;
    }



    public CartViewHolder(View itemView) {
        super(itemView);
        txtOrderItemName = itemView.findViewById(R.id.txt_orderItemName);
        txtOrderItemPrice = itemView.findViewById(R.id.txt_orderItemPrice);
        ivOrderItemCount = itemView.findViewById(R.id.iv_orderItemCount);
        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);

    }
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }


    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select Action");
        contextMenu.add(0,0,getAdapterPosition(), Common.DELETE);
    }
}


public class CartAdapter extends RecyclerView.Adapter<CartViewHolder>{
    private List<Order> listData = new ArrayList<>();
    private Context context;

    public CartAdapter(List<Order> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }



    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.model_cart,parent ,false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        TextDrawable drawable = TextDrawable.builder().buildRound(""+listData.get(position).getQuantity(), Color.RED);
        holder.ivOrderItemCount.setImageDrawable(drawable);

        Locale locale = new
                Locale("en","KE");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(listData.get(position).getPrice()))*(Integer.parseInt(listData.get(position).getQuantity()));
        holder.txtOrderItemPrice.setText(fmt.format(price));
        holder.txtOrderItemName.setText(listData.get(position).getProductName());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
