package com.example.orderandinventorysystem.ui.item;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import java.util.concurrent.ExecutionException;

public class add_item extends AppCompatActivity {

    EditText itemName;
    EditText itemUnit;
    EditText itemDesc;
    EditText sellPrice;
    EditText costPrice;
    EditText quantity;
    EditText quantity2;
    String latestID2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Item");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        itemName = findViewById(R.id.text_item_name_input);
        itemUnit = findViewById(R.id.text_item_unit_input);
        itemDesc = findViewById(R.id.text_item_description_input);
        sellPrice = findViewById(R.id.text_selling_price_input);
        costPrice = findViewById(R.id.text_purchase_price_input);
        quantity = findViewById(R.id.quantity_input);
        quantity2 = findViewById(R.id.quantity_input_2);

        itemUnit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    EditText txtUnit = (EditText)v;
                    String itemUnit = txtUnit.getText().toString();
                    if(!itemUnit.matches("[a-zA-Z ]+")){
                        txtUnit.setError("Only A-Z is allow");
                    }
                }
            }
        });

        itemDesc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    EditText txtItemDesc = (EditText)v;
                    String itemDesc = txtItemDesc.getText().toString();
                    if(itemDesc.length() > 100){
                        txtItemDesc.setError("Not exceed 50 character");
                    }
                }
            }
        });

        sellPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    EditText txtSellPrice = (EditText)v;
                    String sellPrice = txtSellPrice.getText().toString();

                    if(!sellPrice.matches("[-+]?[0-9]*\\.?[0-9]+")){
                        txtSellPrice.setError("Only enter 10 or 10.00");
                    }
                }
            }
        });

        costPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    EditText txtCostPrice = (EditText)v;
                    String costPrice = txtCostPrice.getText().toString();

                    if(!costPrice.matches("[-+]?[0-9]*\\.?[0-9]+")){
                        txtCostPrice.setError("Only enter 10 or 10.00");
                    }
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save: {

                Toast toast = Toast.makeText(getApplicationContext(),"Please fill up the error field",Toast.LENGTH_SHORT);

                if(quantity2.getText().toString().isEmpty()||quantity.getText().toString().isEmpty()|| itemName.getText().toString().isEmpty() || itemUnit.getText().toString().isEmpty() || itemDesc.getText().toString().isEmpty() || sellPrice.getText().toString().isEmpty() || costPrice.getText().toString().isEmpty()){

                    if(itemName.getText().toString().isEmpty())
                        itemName.setError("Please enter this field");

                    if(quantity.getText().toString().isEmpty())
                        quantity.setError("Please enter this field");

                    if(quantity2.getText().toString().isEmpty())
                        quantity2.setError("Please enter this field");

                    if(itemUnit.getText().toString().isEmpty())
                        itemUnit.setError("Please enter this field");

                    if(itemDesc.getText().toString().isEmpty())
                    itemDesc.setError("Please enter this field");

                    if(sellPrice.getText().toString().isEmpty())
                        sellPrice.setError("Please enter this field");

                    if(costPrice.getText().toString().isEmpty())
                        costPrice.setError("Please enter this field");

                    toast.show();

                }else if(!itemUnit.getText().toString().matches("[a-zA-Z ]+")){
                    itemUnit.setError("Only A-Z allow");
                    toast.show();

                }else if(!sellPrice.getText().toString().matches("[-+]?[0-9]*\\.?[0-9]+")){
                    sellPrice.setError("Enter 10 or 10.00");
                    toast.show();

                } else if(!costPrice.getText().toString().matches("[-+]?[0-9]*\\.?[0-9]+")){
                    costPrice.setError("Enter 10 or 10.00");
                    toast.show();

                }else{

                    //constructor
                    Item item1 = new Item("0",itemName.getText().toString(), itemUnit.getText().toString(), itemDesc.getText().toString(), Integer.parseInt(quantity2.getText().toString()), Integer.parseInt(quantity.getText().toString()) ,Double.parseDouble(sellPrice.getText().toString()), Double.parseDouble(costPrice.getText().toString()), "Available");
                    AddItem addItem = new AddItem(item1);
                    addItem.execute("");

                    String str_result="h";
                    try {
                        str_result= new RetrieveItemID().execute().get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    this.finish();

                    RetrieveItemID retrieveItemID = new RetrieveItemID();
                    retrieveItemID.execute("");

                    Intent intent = new Intent(this, ItemMain.class);
                    intent.putExtra("itemID", latestID2);
                    startActivity(intent);
                }

                //Log.d("HAHA",itemName.getText().toString() + itemUnit.getText().toString() + itemDesc.getText().toString());
                return true;
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

    public class RetrieveItemID extends AsyncTask<String,String,String> {

        RetrieveItemID() {
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

                    String query = " SELECT itemID FROM ITEM ORDER BY itemID DESC LIMIT 1";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    if(rs.next()){
                        latestID2 = rs.getString(1);
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
        }
    }

    public class AddItem extends AsyncTask<String,String,String> {

        Item item;

        AddItem(Item item) {

            this.item = item;
        }

        String checkConnection = "";
        boolean isSuccess = false;

        @Override
        protected void onPreExecute() {

        }

        protected String doInBackground(String... params) {

            ConnectionPhpMyAdmin connectionClass = new ConnectionPhpMyAdmin();
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    checkConnection = "No";
                } else {
                    String query = "SELECT * FROM ITEM ORDER BY itemID DESC LIMIT 1";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    String latestID;

                    if (rs.next()) {
                        latestID = rs.getString(1);
                        int numID = Integer.parseInt(latestID.substring(2, 7)) + 1;
                        if (numID < 10)
                            latestID = "I-0000" + Integer.toString(numID);
                        else if (numID < 100)
                            latestID = "I-000" + Integer.toString(numID);
                        else if (numID < 1000)
                            latestID = "I-00" + Integer.toString(numID);
                        else if (numID < 10000)
                            latestID = "I-0" + Integer.toString(numID);
                        else if (numID < 100000)
                            latestID = "I-" + Integer.toString(numID);

                        Log.d("ID", latestID);
                    } else {
                        latestID = "I-00001";
                        Log.d("ID", latestID);
                    }

                    query = "INSERT INTO ITEM VALUES('" + latestID + "', '" + item.getItemName() + "', '" +
                            item.getItemUnit() + "', '" + item.getItemDesc() + "', '" + item.getQuantity() + "', '" +
                            item.getQuantityPHY() + "' , '" + item.getSellPrice() + "', '" + item.getCostPrice() + "', 'Available')";

                    stmt = con.createStatement();
                    stmt.executeUpdate(query);

                    checkConnection = "Yes";
                    isSuccess = true;

                }
            } catch (Exception ex) {
                Log.d("Error", ex.toString());
                isSuccess = false;
                checkConnection = "No";
            }

            return checkConnection;
        }

        @Override
        protected void onPostExecute(String s) {

            Toast.makeText(add_item.this, "Item added.", Toast.LENGTH_LONG).show();
        }
    }
}