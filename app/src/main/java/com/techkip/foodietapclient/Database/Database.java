package com.techkip.foodietapclient.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import com.techkip.foodietapclient.model.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hillarie on 29/05/2018.
 */

public class Database extends SQLiteAssetHelper {
    private static final String DB_NAME = "FoodieTapDB.db";
    private static final int DB_VER = 1;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    public List<Order> getCarts() {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String[] sqlSelect = {"ProductId", " ProductName", " Quantity", " Price", " Discount"};
        String sqlTable = "OrderDetail";
        queryBuilder.setTables(sqlTable);

        Cursor c = queryBuilder.query(db, sqlSelect, null, null, null, null, null);

        final List<Order> result = new ArrayList<>();
        if (c.moveToFirst()) {

            do {
                result.add(new Order(c.getString(c.getColumnIndex("ProductId")),
                        c.getString(c.getColumnIndex("ProductName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("Discount"))
                ));
            } while (c.moveToNext());
        }
        return result;
    }

    public void addToCart(Order order) {
        SQLiteDatabase db = getReadableDatabase();

        String query = String.format("INSERT INTO OrderDetail (ProductId, ProductName,Quantity,Price,Discount) VALUES('%s','%s','%s','%s','%s')",
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount());
        db.execSQL(query);


    }

    public void clearCart() {
        SQLiteDatabase db = getReadableDatabase();

        String query = String.format("DELETE FROM OrderDetail");
        db.execSQL(query);


    }

    //favorites
    public void addToFav(String foodId) {
        SQLiteDatabase db = getReadableDatabase();

        String query = String.format("INSERT INTO Favorites (FoodId) VALUES('%s');", foodId);

        db.execSQL(query);


    }

    //favorites
    public void removeFav(String foodId) {
        SQLiteDatabase db = getReadableDatabase();

        String query = String.format("DELETE  FROM Favorites WHERE FoodId ='%s'", foodId);
        db.execSQL(query);


    }

    public boolean isFav(String foodId) {
        SQLiteDatabase db = getReadableDatabase();

        String query = String.format("SELECT * FROM Favorites WHERE FoodId ='%s'", foodId);
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount()<=0)
        {
            cursor.close();
            return false;
        }
        cursor.close();
        return false;


    }
}
