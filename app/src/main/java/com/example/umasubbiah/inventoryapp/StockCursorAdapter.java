/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.umasubbiah.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.umasubbiah.inventoryapp.data.StockContract.StockEntry;

/**
 * {@link StockCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of product data as its data source. This adapter knows
 * how to create list items for each row of product data in the {@link Cursor}.
 */
public class StockCursorAdapter extends CursorAdapter {


    private CatalogActivity activity = new CatalogActivity();

    /**
     * Constructs a new {@link StockCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public StockCursorAdapter(CatalogActivity context, Cursor c) {
        super(context, c, 0 /* flags */);
        this.activity = context;
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the product data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current product can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        ImageView stockSold = (ImageView) view.findViewById(R.id.record_sale_button);

        // Find the columns of product attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_NAME);
        int priceColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_PRICE);
        int quantColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_QUANTITY);
        int suppColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_SUPPLIER);
        int suppEmailColumIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_SUPPLIER_EMAIL);


        // Read the product attributes from the Cursor for the current product
        final String itemName = cursor.getString(nameColumnIndex);
        final long id = cursor.getLong(cursor.getColumnIndex(StockEntry._ID));
        final int itemPrice = cursor.getInt(priceColumnIndex);
        final int itemQuan = cursor.getInt(quantColumnIndex);
        final String itemSupp = cursor.getString(suppColumnIndex);
        final String itemSuppEmail = cursor.getString(suppEmailColumIndex);

        stockSold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        if(itemQuan>0) {
                            activity.updateQuantity(id, itemName, itemPrice, itemQuan, itemSupp, itemSuppEmail, 1);
                            Toast.makeText(activity,"One "+itemName+" sold! Now there are "+String.valueOf(itemQuan-1)+" left! ",Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(activity,"There aren't any left to sell...",Toast.LENGTH_LONG).show();
            }
        });

        nameTextView.setText(itemName);
        priceTextView.setText("$ " + itemPrice);
        quantityTextView.setText(String.valueOf(itemQuan) + " items");


    }
}
