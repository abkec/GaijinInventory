package com.example.orderandinventorysystem.ui.staff;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class addStaff extends AppCompatActivity {
String latestID;
boolean check = false;

    //    private Spinner spnDpt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Staff");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        spnDpt = (Spinner) findViewById(R.id.spin_department);
//        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.staff_dpt, android.R.layout.simple_spinner_dropdown_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spnDpt.setAdapter(adapter);

        final EditText staffID = (EditText) findViewById(R.id.add_staff_id);
        final EditText staffName = (EditText) findViewById(R.id.add_staff_name);
        final EditText staffMobile = (EditText) findViewById(R.id.add_mobile_no);
        final EditText staffPay = (EditText) findViewById(R.id.add_staff_pay);
        final Spinner department = (Spinner) findViewById(R.id.spin_department);
        final EditText staffPw = (EditText) findViewById(R.id.staff_pw);
        final Button button = (Button) findViewById(R.id.submit_but);
        final Button button1 = (Button) findViewById(R.id.clear_button);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                staffID.setText("");
                staffName.setText("");
                staffMobile.setText("");
                staffPay.setText("");
                staffPw.setText("");
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                check = false;
                validate();
                if(validate())
                {
                    Staff staff = new Staff(staffID.getText().toString(),staffName.getText().toString(),staffMobile.getText().toString(),Double.parseDouble(staffPay.getText().toString()),department.getSelectedItem().toString(),staffPw.getText().toString());
                    String str_result="h";
                    try {
                        str_result= new AddStaff(staff).execute().get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(check){
                        Toast.makeText(getApplicationContext(), "Same ID detected!", Toast.LENGTH_LONG).show();
                    } else{
                        Intent intent = new Intent(addStaff.this, StaffManagementMainMenu.class);
                        intent.putExtra("Staff", staff);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(),"Successfully added to database!",Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public boolean validate(){

        final EditText staffID = (EditText) findViewById(R.id.add_staff_id);
        final EditText staffName = (EditText) findViewById(R.id.add_staff_name);
        final EditText staffMobile = (EditText) findViewById(R.id.add_mobile_no);
        final EditText staffPay = (EditText) findViewById(R.id.add_staff_pay);
        final EditText password = (EditText) findViewById(R.id.staff_pw);


        String sID = staffID.getText().toString();
        String sName = staffName.getText().toString();
        String sMobile = staffMobile.getText().toString();
        String pStaff = password.getText().toString();
//      double sPay = Double.parseDouble(staffPay.getText().toString());

        if(sName.isEmpty() || !sName.matches("[a-zA-Z ]+"))
        {
            Toast.makeText(getApplicationContext(), "Invalid name. Please enter a real name.", Toast.LENGTH_LONG).show();
            return false;
        }if (sID.isEmpty() || !sID.matches("-?\\d+(\\.\\d+)?") || sID.length() > 5 || sID.length() < 5)
        {
            Toast.makeText(getApplicationContext(), "Invalid ID. Please enter the 5 correct numbers.", Toast.LENGTH_LONG).show();
            return false;
        }if (sMobile.isEmpty() || sMobile.length() > 11 || sMobile.length() < 10)
        {
            Toast.makeText(getApplicationContext(), "Invalid Phone Number. Please enter the 10 or 11 digit of the phone number.", Toast.LENGTH_LONG).show();
            return false;
        }if (staffPay.getText().toString().isEmpty())
        {
            Toast.makeText(getApplicationContext(), "Invalid amount. Please enter the correct amount", Toast.LENGTH_LONG).show();
            return false;
        }if (pStaff.isEmpty() || pStaff.length() < 5 || pStaff.length() > 10)
        {
            Toast.makeText(getApplicationContext(), "Invalid password. Please enter 5-10 characters.", Toast.LENGTH_LONG).show();
            return false;
        }
            return true;

    }

    public class AddStaff extends AsyncTask<String,String,String> {

        Staff staff;
        final EditText staffID = (EditText) findViewById(R.id.add_staff_id);
        AddStaff(Staff staff) {

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
                    String query = "SELECT * FROM STAFF WHERE STAFFID = '" + staffID.getText().toString() + "'" ;
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    if (rs.next()) {
                        check = true;
                    } else {
                        query = "INSERT INTO STAFF VALUES('" + staff.getStaffID() + "', '" + staff.getStaffName() + "', '" +
                                staff.getStaffMobile() + "', '" + staff.getStaffPay() + "', '"  + staff.getDepartment() + "', '"  + staff.getPassword() + "')";

                        stmt = con.createStatement();
                        stmt.executeUpdate(query);
                        Log.d("Error2","aaaaaaaa");
                    }

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

        }
    }
}