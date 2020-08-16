package com.example.orderandinventorysystem.ui.vendor;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.orderandinventorysystem.ConnectionPhpMyAdmin;
import com.example.orderandinventorysystem.Model.Customer;
import com.example.orderandinventorysystem.Model.Vendor;
import com.example.orderandinventorysystem.R;
import com.example.orderandinventorysystem.ui.customer.CustomerMain;
import com.example.orderandinventorysystem.ui.customer.edit_customer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


public class VendorMain extends AppCompatActivity{

    Vendor vendor;
    String venID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_main);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        Intent intent = getIntent();
        String intentVendorID = intent.getStringExtra("VendorID");
        venID = intentVendorID;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        RetrieveVend retrieveVend = new RetrieveVend(venID);
        retrieveVend.execute("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.vendor_main_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 6) {

            Intent intent = new Intent(this, VendorMain.class);
            intent.putExtra("VendorID", venID);
            finish();
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
// Handle item selection
        switch (item.getItemId()) {
            case R.id.delete: {
                DeleteVend deleteVend = new DeleteVend(venID);
                deleteVend.execute("");
                this.finish();
                return true;
            }
            case R.id.edit_sales: {
                Intent intent = new Intent(this, edit_vendor.class);
                intent.putExtra("VendorID", venID);
                startActivityForResult(intent, 100);

                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }

    public class RetrieveVend extends AsyncTask<String,String,String> {

        String id;

        RetrieveVend(String id) {

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

                    String query = " SELECT * FROM VENDOR WHERE venID ='" + venID + "'";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    if(rs.next()){
                        vendor = new Vendor(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12));
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

            getSupportActionBar().setTitle(vendor.getVenName());

            TextView compName = (TextView)findViewById(R.id.vcomp_name);
            compName.setText(vendor.getCompanyName());
            TextView ic = (TextView)findViewById(R.id.vendor);
            ic.setText(vendor.getVenIC());
            TextView phone = (TextView)findViewById(R.id.vphone);
            phone.setText(vendor.getPhone());
            TextView mobile = (TextView)findViewById(R.id.vmobile);
            mobile.setText(vendor.getMobile());
            TextView address = (TextView)findViewById(R.id.address);
            address.setText(vendor.getAddress() + "\n" + vendor.getPostcode() + " " + vendor.getCity() + " " + vendor.getState());
            TextView email = (TextView)findViewById(R.id.email);
            email.setText(vendor.getEmail());
        }
    }

    public class DeleteVend extends AsyncTask<String,String,String> {
        String id;

        DeleteVend(String id) {

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

                    String query = " UPDATE VENDOR SET status='Removed' WHERE venID ='" + venID + "'";
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
            Toast.makeText(VendorMain.this, "Vendor deleted.", Toast.LENGTH_LONG).show();
        }
    }


}
