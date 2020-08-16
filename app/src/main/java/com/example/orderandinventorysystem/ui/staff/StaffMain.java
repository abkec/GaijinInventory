package com.example.orderandinventorysystem.ui.staff;

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
import com.example.orderandinventorysystem.Model.Staff;
import com.example.orderandinventorysystem.R;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class StaffMain extends AppCompatActivity {
    Staff staff;
    String staffID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_main);
        Intent intent = getIntent();
        String intentStaffID = intent.getStringExtra("Staff");
        staffID = intentStaffID;

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        RetrieveStaff retrieveStaff = new RetrieveStaff(staffID);
        retrieveStaff.execute("");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.staff_main_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 78) {
            setResult(77);
            Intent intent = new Intent(this, StaffMain.class);
            intent.putExtra("Staff", staffID);
            finish();
            startActivityForResult(intent,200);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
// Handle item selection
        switch (item.getItemId()) {
            case R.id.delete: {
              DeleteStaff deleteStaff = new DeleteStaff (staffID);
              deleteStaff.execute("");
              this.finish();

                return true;
            }case R.id.edit_staff: {
                Intent intent = new Intent(this, updateStaff.class);
                intent.putExtra("Staff", staffID);
                startActivityForResult(intent, 20);

                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    public class RetrieveStaff extends AsyncTask<String,String,String>
    {
        String id;

        RetrieveStaff(String id)
        {
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

                    String query = " SELECT * FROM STAFF WHERE staffID ='" + staffID + "'";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    if(rs.next()){
                        staff = new Staff(rs.getString(1), rs.getString(2), rs.getString(3), Double.parseDouble(rs.getString(4)), rs.getString(5), rs.getString(6));
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

        TextView staffId = (TextView) findViewById(R.id.staff_id_number);
        staffId.setText(staff.getStaffID());
        TextView staffName = (TextView) findViewById(R.id.staff_name);
        staffName.setText(staff.getStaffName());
        TextView staffMobile = (TextView) findViewById(R.id.staff_phone);
        staffMobile.setText(staff.getStaffMobile());
        TextView staffPay = (TextView) findViewById(R.id.staff_pay);
        staffPay.setText(Double.toString(staff.getStaffPay()));
        TextView department = (TextView) findViewById(R.id.staff_dpt);
        department.setText(staff.getDepartment());
        }
    }

    public class DeleteStaff extends AsyncTask<String,String,String> {
        String id;

        DeleteStaff(String id) {

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

                    String query = " DELETE FROM STAFF WHERE staffID ='" + staffID + "'";
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
            Toast.makeText(StaffMain.this, "Staff Deleted.", Toast.LENGTH_SHORT).show();
        }
    }
}
