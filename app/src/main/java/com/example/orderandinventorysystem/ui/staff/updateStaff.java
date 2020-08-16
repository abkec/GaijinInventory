package com.example.orderandinventorysystem.ui.staff;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.concurrent.ExecutionException;

public class updateStaff extends AppCompatActivity {

    TextView sID;
    EditText staffName;
    EditText staffMobile;
    EditText staffPay;
    Spinner department;
    EditText password;

    String staffID;
    boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_staff);

        Intent intent = getIntent();
        String intentItemID = intent.getStringExtra("Staff");
        staffID = intentItemID;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Update Staff");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final Button button = (Button) findViewById(R.id.update_but);
        final Button button1 = (Button) findViewById(R.id.update_cancel_button);

        RetrieveStaff retrieveStaff = new RetrieveStaff(staffID);
        retrieveStaff.execute("");

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                check = false;
                validate();
                if(validate())
                {
                    Staff staff = new Staff(sID.getText().toString(),staffName.getText().toString(),staffMobile.getText().toString(),Double.parseDouble(staffPay.getText().toString()),department.getSelectedItem().toString(),password.getText().toString());
                    String str_result="h";
                    try {
                        str_result= new UpdateStaff (staff).execute().get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(check){
                        Toast.makeText(getApplicationContext(), "Same ID detected!", Toast.LENGTH_LONG).show();
                    } else{
                        setResult(78);
                        finish();
                    }
                }
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public boolean validate(){


        final EditText staffName = (EditText) findViewById(R.id.update_staff_name);
        final EditText staffMobile = (EditText) findViewById(R.id.update_mobile_no);
        final EditText staffPay = (EditText) findViewById(R.id.update_pay);
        final EditText password = (EditText) findViewById(R.id.edit_staff_pw);

        String sPass = password.getText().toString();
        String sName = staffName.getText().toString();
        String sMobile = staffMobile.getText().toString();
//      double sPay = Double.parseDouble(staffPay.getText().toString());

        if(sName.isEmpty() || !sName.matches("[a-zA-Z ]+"))
        {
            Toast.makeText(getApplicationContext(), "Invalid name. Please enter a real name.", Toast.LENGTH_LONG).show();
            return false;
        }if (sMobile.isEmpty() || sMobile.length() > 11 || sMobile.length() < 10)
        {
            Toast.makeText(getApplicationContext(), "Invalid Phone Number. Please enter the 10 or 11 digit of the phone number.", Toast.LENGTH_LONG).show();
            return false;
        }if (staffPay.getText().toString().isEmpty())
        {
            Toast.makeText(getApplicationContext(), "Invalid amount. Please enter the correct amount", Toast.LENGTH_LONG).show();
            return false;
        }if(sPass.isEmpty() || sPass.length() < 5 || sPass.length() > 10)
        {
            Toast.makeText(getApplicationContext(), "Invalid password. Please enter 5-10 characters.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;

    }

    public class RetrieveStaff extends AsyncTask<String, String, String> {

        Staff staff;
        String id;
        RetrieveStaff(String id) {
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

            sID = findViewById(R.id.update_add_staff_id);
            sID.setText(staff.getStaffID());
            staffName = findViewById(R.id.update_staff_name);
            staffName.setText(staff.getStaffName());
            staffMobile = findViewById(R.id.update_mobile_no);
            staffMobile.setText(staff.getStaffMobile());
            staffPay = findViewById(R.id.update_pay);
            staffPay.setText(Double.toString(staff.getStaffPay()));
            department = findViewById(R.id.update_spin_department);
            department.setSelection(((ArrayAdapter<String>)department.getAdapter()).getPosition(staff.getDepartment())); //not sure correct or not for now.
            password = findViewById(R.id.edit_staff_pw);
            password.setText(staff.getPassword());
        }
    }

    public class UpdateStaff extends AsyncTask<String, String, String>{

        Staff staff;

        UpdateStaff(Staff staff)
        {
            this.staff = staff;
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
                    checkConnection = "No";
                } else {
                    String query = " UPDATE STAFF SET staffName ='" + staff.getStaffName() + "'," +
                            "staffMobile = '" + staff.getStaffMobile() + "'," +
                            "staffPay = '" + staff.getStaffPay() + "'," +
                            "department = '" + staff.getDepartment() + "'," +
                            "password = '" + staff.getPassword() + "'" +
                            "WHERE staffID = '" + staffID + "'";

                    Statement stmt = con.createStatement();
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

        protected void onPostExecute(String s)
        {
            Toast.makeText(updateStaff.this,"Staff Edited",Toast.LENGTH_LONG).show();
        }
    }
}