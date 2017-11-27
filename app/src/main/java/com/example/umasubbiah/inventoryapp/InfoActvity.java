package com.example.umasubbiah.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.umasubbiah.inventoryapp.data.StockContract;

/**
 * Created by umasubbiah on 08/10/17.
 */

public class InfoActvity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Content URI for the existing product (null if it's a new product)
     */
    private Uri mCurrentStockUri;
    private TextView mNameText;
    private TextView mPriceText;
    private TextView mQuantityText;
    private TextView mSupplierNameText;
    private ImageView mProductImageView;
    private TextView mSupplierEmailText;
    private Button mPlaceOrder;
    private ImageButton mSell;
    private ImageButton mShip;
    private Button mDelete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setTitle("Product Info");

        mProductImageView = (ImageView) findViewById(R.id.stockImageI);
        mNameText = (TextView) findViewById(R.id.nameI);
        mPriceText = (TextView) findViewById(R.id.priceI);
        mQuantityText = (TextView) findViewById(R.id.quantityI);
        mSupplierNameText = (TextView) findViewById(R.id.suppnameI);
        mSupplierEmailText = (TextView) findViewById(R.id.suppemailI);

       //Setting the button functionality:
        mPlaceOrder = (Button) findViewById(R.id.place_order);
        mPlaceOrder.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                placeOrder();
            }
        });

        //Setting the button functionality:
        mDelete = (Button) findViewById(R.id.delete);
        mDelete.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                showDeleteConfirmationDialog();
            }
        });

        //Setting the button functionality:
        mSell = (ImageButton) findViewById(R.id.sellButton);
        mSell.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                sellMethod();
            }
        });

        mShip = (ImageButton) findViewById(R.id.shipButton);
        mShip.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                shipmentMethod();
            }
        });
        mCurrentStockUri = getIntent().getData();

        String[] projection =
                {
                        StockContract.StockEntry._ID,
                        StockContract.StockEntry.COLUMN_STOCK_NAME,
                        StockContract.StockEntry.COLUMN_STOCK_PRICE,
                        StockContract.StockEntry.COLUMN_STOCK_QUANTITY,
                        StockContract.StockEntry.COLUMN_STOCK_SUPPLIER,
                        StockContract.StockEntry.COLUMN_STOCK_SUPPLIER_EMAIL,
                        StockContract.StockEntry.COLUMN_STOCK_IMAGE
                };

        Cursor cursor = getContentResolver().query(mCurrentStockUri, projection, null, null, null);


        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of product attributes that we're interested in

            int nameColumnIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_QUANTITY);
            int suppColumnIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_SUPPLIER);
            int suppEmailColumnIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_SUPPLIER_EMAIL);
            int imageColumnIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_IMAGE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supp = cursor.getString(suppColumnIndex);
            String suppEmail = cursor.getString(suppEmailColumnIndex);

            byte image[] = cursor.getBlob(imageColumnIndex);

            // set Image
            if (image != null) {
                Bitmap im = BitmapFactory.decodeByteArray(image, 0, image.length);
                mProductImageView.setImageBitmap(im);
            } else {
                mProductImageView.setImageResource(R.drawable.no_image);
            }

            mNameText.setText(name);
            mPriceText.setText(String.valueOf(price));
            mQuantityText.setText(String.valueOf(quantity));
            mSupplierNameText.setText(supp);

            mSupplierEmailText.setText(suppEmail);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                showDeleteConfirmationDialog();
                return true;

            case R.id.sales:

                sellMethod();
                return true;

            case R.id.shipment:
                shipmentMethod();
                return true;


            case R.id.place_order:
                placeOrder();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                StockContract.StockEntry._ID,
                StockContract.StockEntry.COLUMN_STOCK_NAME,
                StockContract.StockEntry.COLUMN_STOCK_PRICE,
                StockContract.StockEntry.COLUMN_STOCK_QUANTITY,
                StockContract.StockEntry.COLUMN_STOCK_SUPPLIER,
                StockContract.StockEntry.COLUMN_STOCK_SUPPLIER_EMAIL,
                StockContract.StockEntry.COLUMN_STOCK_IMAGE
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                StockContract.StockEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of product attributes that we're interested in

            int nameColumnIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_QUANTITY);
            int suppColumnIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_SUPPLIER);
            int suppEmailColumnIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_SUPPLIER);
            int imageColumnIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_IMAGE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supp = cursor.getString(suppColumnIndex);
            String suppEmail = cursor.getString(suppEmailColumnIndex);

            byte image[] = cursor.getBlob(imageColumnIndex);

            // set Image
            if (image != null) {
                Bitmap im = BitmapFactory.decodeByteArray(image, 0, image.length);
                mProductImageView.setImageBitmap(im);
            } else {
                mProductImageView.setVisibility(View.GONE);
            }

            mNameText.setText(name);
            mPriceText.setText(price);
            mQuantityText.setText(quantity);
            mSupplierNameText.setText(supp);

            mSupplierEmailText.setText(suppEmail);


        }
        // set edit text values

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mProductImageView.setImageResource(0);
        mNameText.setText("");
        mPriceText.setText("");
        mQuantityText.setText("");
        mSupplierNameText.setText("");
        mSupplierEmailText.setText("");
    }

    public void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to remove this item from the stock list?");
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteStockItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void deleteStockItem() {
        // Only perform the delete if this is an existing product.
        if (mCurrentStockUri != null) {
            // Call the ContentResolver to delete the product at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentStockUri
            // content URI already identifies the product that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentStockUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    public static <T> void initLoader(final int loaderId, final Bundle args, final LoaderManager.LoaderCallbacks<T> callbacks,
                                      final LoaderManager loaderManager) {
        final Loader<T> loader = loaderManager.getLoader(loaderId);
        if (loader != null && loader.isReset()) {
            loaderManager.restartLoader(loaderId, args, callbacks);
        } else {
            loaderManager.initLoader(loaderId, args, callbacks);
        }
    }

    public void sellMethod(){
        AlertDialog.Builder dialogBox = new AlertDialog.Builder(this);
        final EditText getInput = new EditText(this);
        getInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        dialogBox.setMessage("Enter the number of items sold");
        dialogBox.setView(getInput);

        dialogBox.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int numSold = Integer.parseInt(getInput.getText().toString().trim());

                String[] projection =
                        {
                                StockContract.StockEntry._ID,
                                StockContract.StockEntry.COLUMN_STOCK_NAME,
                                StockContract.StockEntry.COLUMN_STOCK_PRICE,
                                StockContract.StockEntry.COLUMN_STOCK_QUANTITY,
                                StockContract.StockEntry.COLUMN_STOCK_SUPPLIER,
                                StockContract.StockEntry.COLUMN_STOCK_SUPPLIER_EMAIL,
                                StockContract.StockEntry.COLUMN_STOCK_IMAGE
                        };

                Cursor cursor = getContentResolver().query(mCurrentStockUri, projection, null, null, null);

                if (cursor.moveToFirst()) {


                    int nameColumnIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_NAME);
                    int priceColumnIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_PRICE);
                    int quantColumnIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_QUANTITY);
                    int suppColumnIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_SUPPLIER);
                    int suppEmailColumIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_SUPPLIER_EMAIL);


                    // Read the product attributes from the Cursor for the current product
                    String itemName = cursor.getString(nameColumnIndex);
                    long id = cursor.getLong(cursor.getColumnIndex(StockContract.StockEntry._ID));
                    int itemPrice = cursor.getInt(priceColumnIndex);
                    int itemQuan = cursor.getInt(quantColumnIndex);
                    String itemSupp = cursor.getString(suppColumnIndex);
                    String itemSuppEmail = cursor.getString(suppEmailColumIndex);

                    if(numSold>itemQuan) {
                        Toast.makeText(InfoActvity.this, "There aren't that many left...", Toast.LENGTH_LONG).show();
                        return;
                    }

                    int newQuant = itemQuan - numSold;
                    ContentValues values = new ContentValues();
                    values.put(StockContract.StockEntry.COLUMN_STOCK_NAME, itemName);
                    values.put(StockContract.StockEntry.COLUMN_STOCK_PRICE, itemPrice);
                    values.put(StockContract.StockEntry.COLUMN_STOCK_QUANTITY, newQuant);
                    values.put(StockContract.StockEntry.COLUMN_STOCK_SUPPLIER, itemSupp);
                    values.put(StockContract.StockEntry.COLUMN_STOCK_SUPPLIER_EMAIL, itemSuppEmail);

                    mQuantityText.setText(String.valueOf(newQuant));

                    int rowsAffected = getContentResolver().update(mCurrentStockUri, values, null, null);

                }
            }
        });


        AlertDialog alertDialog = dialogBox.create();
        alertDialog.show();

    }

    public void shipmentMethod(){
        AlertDialog.Builder dialogBox2 = new AlertDialog.Builder(this);
        final EditText getInput2 = new EditText(this);
        getInput2.setInputType(InputType.TYPE_CLASS_NUMBER);
        dialogBox2.setMessage("Enter the number of items shipped in");
        dialogBox2.setView(getInput2);

        dialogBox2.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int numShip = Integer.parseInt(getInput2.getText().toString().trim());

                String[] projection =
                        {
                                StockContract.StockEntry._ID,
                                StockContract.StockEntry.COLUMN_STOCK_NAME,
                                StockContract.StockEntry.COLUMN_STOCK_PRICE,
                                StockContract.StockEntry.COLUMN_STOCK_QUANTITY,
                                StockContract.StockEntry.COLUMN_STOCK_SUPPLIER,
                                StockContract.StockEntry.COLUMN_STOCK_SUPPLIER_EMAIL,
                                StockContract.StockEntry.COLUMN_STOCK_IMAGE
                        };

                Cursor cursor = getContentResolver().query(mCurrentStockUri, projection, null, null, null);

                if (cursor.moveToFirst()) {


                    int nameColumnIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_NAME);
                    int priceColumnIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_PRICE);
                    int quantColumnIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_QUANTITY);
                    int suppColumnIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_SUPPLIER);
                    int suppEmailColumIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_SUPPLIER_EMAIL);


                    // Read the product attributes from the Cursor for the current product
                    String itemName = cursor.getString(nameColumnIndex);
                    long id = cursor.getLong(cursor.getColumnIndex(StockContract.StockEntry._ID));
                    int itemPrice = cursor.getInt(priceColumnIndex);
                    int itemQuan = cursor.getInt(quantColumnIndex);
                    String itemSupp = cursor.getString(suppColumnIndex);
                    String itemSuppEmail = cursor.getString(suppEmailColumIndex);

                    int newQuant = itemQuan + numShip;
                    ContentValues values = new ContentValues();
                    values.put(StockContract.StockEntry.COLUMN_STOCK_NAME, itemName);
                    values.put(StockContract.StockEntry.COLUMN_STOCK_PRICE, itemPrice);
                    values.put(StockContract.StockEntry.COLUMN_STOCK_QUANTITY, newQuant);
                    values.put(StockContract.StockEntry.COLUMN_STOCK_SUPPLIER, itemSupp);
                    values.put(StockContract.StockEntry.COLUMN_STOCK_SUPPLIER_EMAIL, itemSuppEmail);

                    mQuantityText.setText(String.valueOf(newQuant));

                    int rowsAffected = getContentResolver().update(mCurrentStockUri, values, null, null);

                }
            }
        });


        AlertDialog alertDialog2 = dialogBox2.create();
        alertDialog2.show();
    }

    public void placeOrder(){
        String itemSuppEmail = null;
        String itemName = null;
        String[] projection =
                {
                        StockContract.StockEntry._ID,
                        StockContract.StockEntry.COLUMN_STOCK_NAME,
                        StockContract.StockEntry.COLUMN_STOCK_PRICE,
                        StockContract.StockEntry.COLUMN_STOCK_QUANTITY,
                        StockContract.StockEntry.COLUMN_STOCK_SUPPLIER,
                        StockContract.StockEntry.COLUMN_STOCK_SUPPLIER_EMAIL,
                        StockContract.StockEntry.COLUMN_STOCK_IMAGE
                };

        Cursor cursor = getContentResolver().query(mCurrentStockUri, projection, null, null, null);

        if (cursor.moveToFirst()) {


            int nameColumnIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_NAME);
            int suppEmailColumIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_SUPPLIER_EMAIL);


            // Read the product attributes from the Cursor for the current product
            itemName = cursor.getString(nameColumnIndex);
            itemSuppEmail = cursor.getString(suppEmailColumIndex);

        }

        //https://stackoverflow.com/questions/2197741/how-can-i-send-emails-from-my-android-application:
        //And the JustJava app from lesson one :

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("message/rfc822");
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT,
                "Placing an order");

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{itemSuppEmail});
        String message = "Hello Supplier! \n I would like to order more of your amazing " + itemName + " !!!\n Thank you!";
        intent.putExtra(Intent.EXTRA_TEXT, message);


        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


}
