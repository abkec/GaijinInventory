package com.example.orderandinventorysystem.ui.staff;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderandinventorysystem.ConnectionPhpMyAdmin;
import com.example.orderandinventorysystem.Model.Staff;
import com.example.orderandinventorysystem.R;
import com.example.orderandinventorysystem.ui.customer.CustomerMain;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class viewStaff extends AppCompatActivity implements StaffListAdapter.ItemClickListener  {
    StaffListAdapter adapter;
    ArrayList<Staff> staffList;
    RecyclerView recyclerView;
    TextView con;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_fragment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("View Staff");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ShowStaffList showStaffList = new ShowStaffList();
        showStaffList.execute("");
        staffList = new ArrayList<>();
        recyclerView = findViewById(R.id.staff_recycler_view);
        con = findViewById(R.id.connection);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StaffListAdapter(this, staffList);
        adapter.setClickListener(this);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 77) {

            Intent intent = new Intent(this, viewStaff.class);
            finish();
            startActivityForResult(intent,11);
        }

    }

    @Override
    public void onItemClick(View view, int position, String id, String name) {
        Intent intent = new Intent(this, StaffMain.class); /*Overall View of the Staff (CustomerMain.class)*/
        intent.putExtra("Staff", id);
        startActivityForResult(intent,20);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public class ShowStaffList extends AsyncTask<String, String, String> {


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

                    String query = " SELECT * FROM STAFF WHERE NOT department='Admin' AND NOT department='Admin2' ";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    while (rs.next()) {
                        staffList.add(new Staff(rs.getString(1), rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getString(5), rs.getString(6)));
                        Log.d("Success", rs.getString(1));
                    }

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
            recyclerView.setAdapter(adapter);

            if (staffList.size()==0) {

                con.setVisibility(View.VISIBLE);
            }
        }

    }
}