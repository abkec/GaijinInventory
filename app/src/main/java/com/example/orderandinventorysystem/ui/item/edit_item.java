package com.example.orderandinventorysystem.ui.item;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.orderandinventorysystem.ConnectionPhpMyAdmin;
import com.example.orderandinventorysystem.Model.Item;
import com.example.orderandinventorysystem.R;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class edit_item extends AppCompatActivity {

    EditText itemName;
    EditText itemUnit;
    EditText itemDesc;
    EditText sellPrice;
    EditText costPrice;
    EditText quantity;
    EditText quantity2;
    String itemID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        Intent intent = getIntent();
        String intentItemID = intent.getStringExtra("itemID");
        itemID = intentItemID;

       // itemName = findViewById(R.id.text_item_name_input);
       // itemUnit = findViewById(R.id.text_item_unit_input);
       // itemDesc = findViewById(R.id.text_item_description_input);
       // sellPrice = findViewById(R.id.text_selling_price_input);
       // costPrice = findViewById(R.id.text_purchase_price_input);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Item");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        RetrieveItem retrieveItem = new RetrieveItem(itemID);
        retrieveItem.execute("");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save: {

                if(quantity.getText().toString().isEmpty() || itemName.getText().toString().isEmpty() || itemUnit.getText().toString().isEmpty() || itemDesc.getText().toString().isEmpty() || sellPrice.getText().toString().isEmpty() || costPrice.getText().toString().isEmpty()){

                    if(itemName.getText().toString().isEmpty())
                        itemName.setError("Please enter this field");

                    if(quantity.getText().toString().isEmpty())
                        quantity.setError("Please enter this field");

                    if(itemUnit.getText().toString().isEmpty())
                        itemUnit.setError("Please enter this field");

                    if(itemDesc.getText().toString().isEmpty())
                        itemDesc.setError("Please enter this field");

                    if(sellPrice.getText().toString().isEmpty())
                        sellPrice.setError("Please enter this field");

                    if(costPrice.getText().toString().isEmpty())
                        costPrice.setError("Please enter this field");

                    Toast toast = Toast.makeText(getApplicationContext(),"Please fill up the error field",Toast.LENGTH_SHORT);

                    toast.show();

                }else if(!itemUnit.getText().toString().matches("[a-zA-Z ]+")){

                    itemUnit.setError("Only A-Z allow");

                    Toast toast = Toast.makeText(getApplicationContext(),"Please fill up the error field",Toast.LENGTH_SHORT);

                    toast.show();

                }else if(!sellPrice.getText().toString().matches("[-+]?[0-9]*\\.?[0-9]+")){

                    sellPrice.setError("Enter 10 or 10.00");

                    Toast toast = Toast.makeText(getApplicationContext(),"Please fill up the error field",Toast.LENGTH_SHORT);

                    toast.show();

                } else if(!costPrice.getText().toString().matches("[-+]?[0-9]*\\.?[0-9]+")){

                    costPrice.setError("Enter 10 or 10.00");

                    Toast toast = Toast.makeText(getApplicationContext(),"Please fill up the error field",Toast.LENGTH_SHORT);

                    toast.show();

                }else {

                    //constructor
                    Item item1 = new Item("0", itemName.getText().toString(), itemUnit.getText().toString(), itemDesc.getText().toString(),Integer.parseInt(quantity2.getText().toString()), Integer.parseInt(quantity.getText().toString()), Double.parseDouble(sellPrice.getText().toString()), Double.parseDouble(costPrice.getText().toString()), "Available");
                    UpdateItem updateItem = new UpdateItem(item1);
                    updateItem.execute("");
                    setResult(3);
                    this.finish();


                    //Log.d("HAHA",itemName.getText().toString() + itemUnit.getText().toString() + itemDesc.getText().toString());
                    return true;
                }
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_main_menu, menu);
        return true;
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public class RetrieveItem extends AsyncTask<String,String,String> {

        Item item;
        String id;

        RetrieveItem(String id) {

            this.id = id;
        }

        String checkConnection = "";
        boolean isSuccess = false;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {

            ConnectionPhpMyAdmin connectionClass = new ConnectionPhpMyAdmin();
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    checkConnection = "Please check your internet connection.";
                } else {

                    String query = " SELECT * FROM ITEM WHERE itemID ='" + itemID + "'";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    if(rs.next()){
                        item = new Item(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getInt(6),rs.getDouble(7), rs.getDouble(8), rs.getString(9));
                    }

                    Log.d("Success", "Done");
                    checkConnection = "Done";
                    isSuccess = true;

                }
            } catch (Exception ex) {
                Log.d("Error", ex.toString());
                isSuccess = false;
                checkConnection = "Exceptions" + ex;
            }

            return checkConnection;
        }

        @Override
        protected void onPostExecute(String s) {

            //Log.d("hahaha", item.getItemName());

            quantity = findViewById(R.id.quantity_input);
            quantity2 = findViewById(R.id.quantity_input_2);
            itemName = findViewById(R.id.text_item_name_input);
            itemUnit = findViewById(R.id.text_item_unit_input);
            itemDesc = findViewById(R.id.text_item_description_input);
            sellPrice = findViewById(R.id.text_selling_price_input);
            costPrice = findViewById(R.id.text_purchase_price_input);

            quantity2.setText(Integer.toString(item.getQuantity()));
            quantity.setText(Integer.toString(item.getQuantityPHY()));
            itemName.setText(item.getItemName());
            itemUnit.setText(item.getItemUnit());
            itemDesc.setText(item.getItemDesc());
            sellPrice.setText(String.format("%.2f",item.getSellPrice()));
            costPrice.setText(String.format("%.2f",item.getCostPrice()));

        }
    }

    public class UpdateItem extends AsyncTask<String,String,String> {

        Item item;

        UpdateItem(Item item) {

            this.item = item;
        }

        String checkConnection = "";
        boolean isSuccess = false;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {

            ConnectionPhpMyAdmin connectionClass = new ConnectionPhpMyAdmin();
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    checkConnection = "Please check your internet connection.";
                } else {

                    String query = " UPDATE ITEM SET itemName = '" + item.getItemName() + "'," +
                            " itemUnit = '" + item.getItemUnit()  + "'," +
                            " itemDesc = '" + item.getItemDesc() + "'," +
                            " sellingPrice = '" + item.getSellPrice() + "'," +
                            " purchasePrice = '" + item.getCostPrice() + "'," +
                            " itemQuantity = '" + item.getQuantity() + "'," +
                            " itemQuantityPHY = '" + item.getQuantityPHY() + "'"+
                            " WHERE itemID ='" + itemID + "'";

                    Statement stmt = con.createStatement();
                    stmt.executeUpdate(query);
                    Log.d("Success", "Done");
                    checkConnection = "Done";
                    isSuccess = true;

                }
            } catch (Exception ex) {
                Log.d("Error", ex.toString());
                isSuccess = false;
                checkConnection = "Exceptions" + ex;
            }

            return checkConnection;
        }

        @Override
        protected void onPostExecute(String s) {

            Toast.makeText(edit_item.this, "Item edited.", Toast.LENGTH_LONG).show();
        }
    }

}
