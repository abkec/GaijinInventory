package com.example.orderandinventorysystem.ui.dashboard;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.example.orderandinventorysystem.ConnectionPhpMyAdmin;
import com.example.orderandinventorysystem.Model.Customer;
import com.example.orderandinventorysystem.R;
import com.example.orderandinventorysystem.ui.bill.add_new_bill;
import com.example.orderandinventorysystem.ui.invoice.add_new_invoice;
import com.example.orderandinventorysystem.ui.pack.PackageFragment;
import com.example.orderandinventorysystem.ui.pack.packages_all;
import com.example.orderandinventorysystem.ui.purchase.add_new_purchase;
import com.example.orderandinventorysystem.ui.sales.add_sales_orders;
import com.example.orderandinventorysystem.ui.staff.loginStaff;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DashboardFragment extends Fragment {

    int packNum=0, shipNum=0, deliverNum=0, invNum=0;
    View root;

    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        DashboardList dashboardList = new DashboardList();
        dashboardList.execute("");
        FloatingActionButton fab = root.findViewById(R.id.purchFB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), add_new_purchase.class));
            }
        });

        FloatingActionButton fab3 = root.findViewById(R.id.salesFB);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), add_sales_orders.class));
            }
        });

        ImageButton packButton = root.findViewById(R.id.pack_button);
        packButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.nav_sales);
            }
        });

        ImageButton shipButton = root.findViewById(R.id.ship_button);
        shipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.nav_package);
            }
        });

        ImageButton del_button = root.findViewById(R.id.del_button);
        del_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.nav_package);
            }
        });

        ImageButton inv_but = root.findViewById(R.id.inv_but);
        inv_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.nav_sales);
            }
        });

        ImageButton report = root.findViewById(R.id.report);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), sales_report.class));
            }
        });

        return root;
    }

    public class DashboardList extends AsyncTask<String,String,String> {

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

                    packNum=0;
                    shipNum=0;
                    deliverNum=0; invNum=0;

                    String query = " SELECT * FROM SALES WHERE SALESSTATUS='Confirmed'";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    while (rs.next()) {

                        boolean check = false;
                        query = " SELECT * FROM PACKAGE WHERE PACKSTATUS='PACKED' AND SALESID='"+ rs.getString(1) + "'";
                        stmt = con.createStatement();
                        ResultSet rs2 = stmt.executeQuery(query);

                        if (rs2.next()) {

                            shipNum++;
                            check=true;
                        }

                        query = " SELECT * FROM PACKAGE WHERE PACKSTATUS='SHIPPED' AND SALESID='"+ rs.getString(1) + "'";
                        stmt = con.createStatement();
                        rs2 = stmt.executeQuery(query);

                        if (rs2.next()) {

                            deliverNum++;
                            check=true;
                        }

                        query = " SELECT * FROM INVOICE WHERE SALESID='"+ rs.getString(1) + "'";
                        stmt = con.createStatement();
                        rs2 = stmt.executeQuery(query);

                        if (!rs2.next()) {

                            invNum++;
                            check=true;
                        }

                        if(!check)
                            packNum++;

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

            TextView tx1 = root.findViewById(R.id.pack_text_one);
            tx1.setText(Integer.toString(packNum));
            tx1 = root.findViewById(R.id.ship_text_one);
            tx1.setText(Integer.toString(shipNum));
            tx1 = root.findViewById(R.id.del_text_one);
            tx1.setText(Integer.toString(deliverNum));
            tx1 = root.findViewById(R.id.inv_text_one);
            tx1.setText(Integer.toString(invNum));
        }
    }
}