package com.example.orderandinventorysystem.ui.staff;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.orderandinventorysystem.R;

public class StaffManagementMainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_management_main_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Staff Management");

       Button button = findViewById(R.id.add_staff); /*Add Staff*/
       button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               openAddStaff();

           }
       });

//       Button button1 = findViewById(R.id.update_staff); /*Update Staff*/
//       button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openUpdateStaff();
//
//            }
//        });

       Button button2 = findViewById(R.id.view_staff); /*View Staff*/
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openViewStaff();

            }
        });

//        Button button3 = findViewById(R.id.delete_staff); /*View Staff*/
//        button3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openDeleteStaff();
//
//            }
//        });

        Button but= findViewById(R.id.angry_btn);
        but.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "You have logged out", Toast.LENGTH_SHORT).show();
                finish();
            }
        }));

    }

    @Override
    public void onBackPressed() {
        setResult(123);
        this.finish();

    }

    public void openAddStaff(){
        Intent intent = new Intent(this, addStaff.class);
        startActivity(intent);
    }

//    public void openUpdateStaff(){
//        Intent intent = new Intent(this,updateStaff.class);
//        startActivity(intent);
//    }

    public void openViewStaff(){
        Intent intent = new Intent(this, viewStaff.class);
        startActivity(intent);
    }

//    public void openDeleteStaff(){
//        Intent intent = new Intent(this,deleteStaff.class);
//        startActivity(intent);
//    }
}