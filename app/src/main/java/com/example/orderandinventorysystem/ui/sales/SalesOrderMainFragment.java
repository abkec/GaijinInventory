package com.example.orderandinventorysystem.ui.sales;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.orderandinventorysystem.Adapter.SectionsPagerAdapter;
import com.example.orderandinventorysystem.ConnectionPhpMyAdmin;
import com.example.orderandinventorysystem.Model.Customer;
import com.example.orderandinventorysystem.Model.Invoice;
import com.example.orderandinventorysystem.Model.ItemOrder;
import com.example.orderandinventorysystem.Model.Sales;
import com.example.orderandinventorysystem.ui.invoice.InvoiceMainFragment;
import com.example.orderandinventorysystem.ui.invoice.add_new_invoice;
import com.example.orderandinventorysystem.ui.pack.PackageMain;
import com.example.orderandinventorysystem.ui.purchase.PurchaseMain;
import com.google.android.material.tabs.TabLayout;
import com.example.orderandinventorysystem.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SalesOrderMainFragment extends AppCompatActivity {

    Menu menu1;
    Customer cust;
    ViewPager viewPager;
    TabLayout tabLayout;
    ArrayList<ItemOrder> ioList;
    Sales sales;
    boolean packCheck=false, invoiceCheck=false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sales_order_main);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sales Order");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        String intentSalesID = intent.getStringExtra("Sales");
        SalesMainInfo salesMainInfo = new SalesMainInfo(intentSalesID);
        salesMainInfo.execute("");


        viewPager = findViewById(R.id.viewpager_all_2);
        tabLayout = findViewById(R.id.tabLayout_2);
        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sales_order_main_menu, menu);
        menu1 = menu;
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.download_sales: {

                pdfGenerate();
                return true;
            }

            case R.id.edit_sales: {
                Intent intent = new Intent(this, edit_sales_orders.class);
                intent.putExtra("SalesEdit", sales);
                startActivityForResult(intent, 10);
                return true;
            }

            case R.id.create_package: {

                AddPackage addPackage = new AddPackage();
                addPackage.execute("");
                return true;
            }

            case R.id.create_invoice: {

                Intent intent = new Intent(this, add_new_invoice.class);
                intent.putExtra("SalesEdit", sales);
                startActivityForResult(intent, 11);
                return true;
            }

            case R.id.delete: {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage("Confirm delete?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                DeleteSales deleteSales = new DeleteSales(sales.getSalesID());
                                deleteSales.execute("");
                                finish();
                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();


                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2) {
            finish();

        }

        else if (resultCode == 3) {

            Intent intent = new Intent(this, SalesOrderMainFragment.class);
            intent.putExtra("Sales", sales.getSalesID());
            finish();
            startActivity(intent);
        }

        else if (resultCode == 4) {

            Intent intent = new Intent(this, SalesOrderMainFragment.class);
            intent.putExtra("Sales", sales.getSalesID());
            finish();
            startActivity(intent);

            intent = new Intent(this, InvoiceMainFragment.class);
            intent.putExtra("Invoice", data.getExtras().getString("Invoice"));
            startActivityForResult(intent, 16);
        }
    }

    public void pdfGenerate() {

        Bitmap bmp, scaledbmp;
        int pageWidth = 1200, pageHeight = 1960;
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.gaijin);
        scaledbmp = Bitmap.createScaledBitmap(bmp, 250, 125, false);

        PdfDocument salesPdf = new PdfDocument();
        Paint paint = new Paint();

        PdfDocument.PageInfo pageInfo1 = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page1 = salesPdf.startPage(pageInfo1);
        Canvas canvas = page1.getCanvas();

        //draw Gaijin Logo Title
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        paint.setTextSize(45);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        canvas.drawText("Gaijin Company", 925, 325, paint);
        paint.setStyle(Paint.Style.FILL);

        //draw Gaijin Logo
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.inventory);
        scaledbmp = Bitmap.createScaledBitmap(bmp, 200,125,false);
        canvas.drawBitmap(scaledbmp,810,150, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(60);
        canvas.drawText("Sales Order", 925, 505, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(25);
        canvas.drawText("From : ", 100,185, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextSize(20);
        canvas.drawText("Gaijin Company", 100,225, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(20);
        canvas.drawText("No.4 Taman Nanas,", 100,265, paint);        // Remember change Y - axis

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(20);
        canvas.drawText("14535 Kedah, Malaysia", 100,305, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(20);
        canvas.drawText("Email : gaijin_888@gmail.com", 100,345, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(20);
        canvas.drawText("Contact : 012-4428888", 100,385, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(25);
        canvas.drawText("To : ", 100,465, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextSize(20);
        canvas.drawText(cust.getCompanyName(), 100,505, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(20);
        canvas.drawText(cust.getAddress(), 100,545, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(20);
        canvas.drawText(cust.getPostCode() + ", " + cust.getCity() + ", " + cust.getState(), 100,585, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(20);
        canvas.drawText("Email : " + cust.getEmail(), 100,625, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(20);
        canvas.drawText("Phone: " + cust.getPhone(), 100,665, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(20);
        canvas.drawText("Mobile: " + cust.getMobile(), 100,705, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(20);
        canvas.drawText("Order ID : ", 800,585, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(20);
        canvas.drawText("Order Date : ", 800,625, paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(20);
        canvas.drawText(sales.getSalesID(), 1050,585, paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(20);
        canvas.drawText(sales.getSalesDate(), 1050,625, paint);

        //Columns Name
        paint.setTextSize(20f);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawLine(20, 790, pageWidth-20, 790, paint);
        canvas.drawLine(20, 860, pageWidth-20, 860, paint);
        canvas.drawLine(20, 790, 20, 860, paint);
        canvas.drawLine(pageWidth-20, 790, pageWidth-20, 860, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText("No", 40, 830, paint);
        canvas.drawText("Item Name", 100, 830, paint);
        canvas.drawText("Unit Price (MYR)", 400, 830, paint);
        canvas.drawText("Quantity", 650, 830, paint);
        canvas.drawText("Discount (MYR)", 800, 830, paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Subtotal (MYR)", pageWidth-30, 830, paint);

        canvas.drawLine(80, 790, 80, 860, paint);
        canvas.drawLine(380, 790, 380, 860, paint);
        canvas.drawLine(630, 790, 630, 860, paint);
        canvas.drawLine(780, 790, 780, 860, paint);
        canvas.drawLine(1010, 790, 1010, 860, paint);

        //ItemOrder
        int num=1, yAxis=850;
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        for(int i=0; i<ioList.size(); i++) {

            double discount=0;
            yAxis += 50;

            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(Integer.toString(num), 40, yAxis, paint);
            canvas.drawText(ioList.get(i).getItemName(), 100, yAxis, paint);
            canvas.drawText(String.format("%.2f", ioList.get(i).getSellPrice()), 400, yAxis, paint);
            canvas.drawText(Integer.toString(ioList.get(i).getQuantity()), 650, yAxis, paint);
            discount = ioList.get(i).getTotal() * ioList.get(i).getDiscount() / 100;
            canvas.drawText(String.format("%.2f", discount), 800, yAxis, paint);
            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(String.format("%.2f", ioList.get(i).getTotal() - discount), pageWidth-30, yAxis, paint);
            canvas.drawLine(20, yAxis+15, pageWidth-20, yAxis+15, paint);
            num+=1;
        }

        yAxis += 15;
        canvas.drawLine(20, 860, 20, yAxis, paint);
        canvas.drawLine(80, 860, 80, yAxis, paint);
        canvas.drawLine(380, 860, 380, yAxis, paint);
        canvas.drawLine(630, 860, 630, yAxis, paint);
        canvas.drawLine(780, 860, 780, yAxis, paint);
        canvas.drawLine(1010, 860, 1010, yAxis, paint);
        canvas.drawLine(pageWidth-20, 860, pageWidth-20, yAxis, paint);
        canvas.drawLine(20, yAxis, pageWidth-20, yAxis, paint);


        //Total
        yAxis += 50;
        paint.setColor(Color.rgb(0,0,0));
        canvas.drawLine(780, yAxis-50, pageWidth-20, yAxis-50, paint);
        canvas.drawLine(780, yAxis-50, 780, yAxis+30, paint);

        canvas.drawLine(780, yAxis+30, pageWidth-20, yAxis+30, paint);
        canvas.drawLine(pageWidth-20, yAxis-50, pageWidth-20, yAxis+30, paint);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Grand Total (MYR): ", 800, yAxis, paint);

        canvas.drawLine(1010, yAxis-50, 1010, yAxis+30, paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(String.format("%.2f", sales.getSalesPrice()), pageWidth-30, yAxis, paint);
        yAxis += 200;

        //Footer
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(255,69,0));
        paint.setTextSize(30f);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("THANK YOU FOR YOUR BUSINESS ! ", pageWidth/2, yAxis, paint);


        salesPdf.finishPage(page1);

        File file = new File (getExternalFilesDir(null),"SalesOrder_" + sales.getSalesID() + ".pdf");


        try {
            salesPdf.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "Sales Order PDF has been saved into your device", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Please allow the storage permission for this application in your android settings",
                    Toast.LENGTH_LONG).show();
        }

        salesPdf.close();


    }

    private void setUpViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new Sales_Details(), "DETAILS");
        adapter.addFragment(new Sales_Packages(), "PACKAGE");
        adapter.addFragment(new Sales_Invoices(), "INVOICE");

        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        setResult(2);
        finish();
        return true;
    }

    public class SalesMainInfo extends AsyncTask<String,String,String> {

        String salesID;
        Sales salesDB;
        ArrayList <ItemOrder> ioListDB= new ArrayList<>();

        public Sales getSales() {
            return salesDB;
        }

        public ArrayList<ItemOrder> getIoList() {
            return ioList;
        }

        SalesMainInfo(String salesID) {
            this.salesID = salesID;
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


                    String query = " SELECT * FROM SALES WHERE salesID ='" + salesID + "'";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs.next()) {

                        salesDB = new Sales(rs.getString(1), rs.getString(2),
                                rs.getString(3), rs.getString(4),
                                rs.getDouble(6), rs.getString(5));
                    }

                    query = " SELECT * FROM CUSTOMER WHERE custID ='" + salesDB.getSalesCustID() + "'";
                    stmt = con.createStatement();
                    rs = stmt.executeQuery(query);
                    if (rs.next()) {

                        cust = new Customer(rs.getString(1), rs.getString(2), rs.getString(3),
                                rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7),
                                rs.getString(8), rs.getString(9), rs.getString(10),rs.getString(11),
                                rs.getString(12),rs.getString(13), rs.getString(14));
                    }

                    query = " SELECT * FROM INVOICE WHERE salesID ='" + salesID + "'";
                    stmt = con.createStatement();
                    rs = stmt.executeQuery(query);
                    if (rs.next()) {

                        invoiceCheck = true;

                    }

                    query = " SELECT * FROM ITEMORDER WHERE orderID ='" + salesID + "'";
                    stmt = con.createStatement();
                    rs = stmt.executeQuery(query);
                    while (rs.next()) {

                        ioListDB.add(new ItemOrder(rs.getString(1), rs.getString(2),
                                rs.getString(3), rs.getDouble(4),
                                rs.getDouble(5), rs.getInt(6), rs.getDouble(7)));
                    }

                    query = " SELECT * FROM PACKAGE WHERE salesID ='" + salesID + "'";
                    stmt = con.createStatement();
                    rs = stmt.executeQuery(query);
                    if (rs.next()) {

                        packCheck = true;
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

            sales = salesDB;
            ioList = ioListDB;

            TextView salesID = findViewById(R.id.sales_order_id);
            salesID.setText(sales.getSalesID());

            TextView custName = findViewById(R.id.cust_name);
            custName.setText(sales.getSaleCustName());

            TextView salesPrice = findViewById(R.id.sales_order_price);
            salesPrice.setText(String.format("MYR%.2f", sales.getSalesPrice()));

            TextView salesDate = findViewById(R.id.sales_order_date);
            salesDate.setText(sales.getSalesDate());

            TextView salesStatus = findViewById(R.id.sales_order_status);
            salesStatus.setText(sales.getSalesStatus());

            if (packCheck && invoiceCheck)
                salesStatus.setText("Closed");

            if(sales.getSalesStatus().equals("Removed")) {

                MenuItem menuItem = menu1.findItem(R.id.create_package);
                menuItem.setVisible(false);
                MenuItem menuItem2 = menu1.findItem(R.id.delete);
                menuItem2.setVisible(false);
                menuItem2 = menu1.findItem(R.id.edit_sales);
                menuItem2.setVisible(false);
                menuItem = menu1.findItem(R.id.create_invoice);
                menuItem.setVisible(false);
            }

            if(packCheck) {
                MenuItem menuItem = menu1.findItem(R.id.create_package);
                menuItem.setVisible(false);
                MenuItem menuItem2 = menu1.findItem(R.id.delete);
                menuItem2.setVisible(false);
                menuItem2 = menu1.findItem(R.id.edit_sales);
                menuItem2.setVisible(false);
            }

            if(invoiceCheck) {

                MenuItem menuItem = menu1.findItem(R.id.create_invoice);
                menuItem.setVisible(false);
                MenuItem menuItem2 = menu1.findItem(R.id.delete);
                menuItem2.setVisible(false);
                menuItem = menu1.findItem(R.id.edit_sales);
                menuItem.setVisible(false);
            }


        }
    }

    public class DeleteSales extends AsyncTask<String,String,String> {

        String salesID;

        DeleteSales(String salesID) {
            this.salesID = salesID;
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

                    String query = " UPDATE SALES SET SALESSTATUS='Removed' WHERE SALESID ='" + salesID + "'";
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
            Toast.makeText(SalesOrderMainFragment.this, "Sales Order deleted.", Toast.LENGTH_LONG).show();
        }
    }

    public class AddPackage extends AsyncTask<String,String,String> {

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

                    String query = "SELECT * FROM PACKAGE ORDER BY PACKID DESC LIMIT 1";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    String latestID;

                    if (rs.next()) {
                        latestID = rs.getString(1);
                        int numID = Integer.parseInt(latestID.substring(3,8)) + 1;
                        if (numID < 10)
                            latestID = "PK-0000" + Integer.toString(numID);
                        else if (numID < 100)
                            latestID = "PK-000" + Integer.toString(numID);
                        else if (numID < 1000)
                            latestID = "PK-00" + Integer.toString(numID);
                        else if (numID < 10000)
                            latestID = "PK-0" + Integer.toString(numID);
                        else if (numID < 100000)
                            latestID = "PK-" + Integer.toString(numID);

                    }

                    else {
                        latestID = "PK-00001";
                    }

                    String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                    query = " INSERT INTO PACKAGE VALUES('" + latestID + "','" + currentDate + "','"+ sales.getSalesID() + "','PACKED')" ;
                    stmt = con.createStatement();
                    stmt.executeUpdate(query);

                    for (int i=0; i < ioList.size(); i++) {

                        query = "UPDATE ITEM SET ITEMQUANTITYPHY= ITEMQUANTITYPHY - '" + ioList.get(i).getQuantity() + "' WHERE ITEMID='" + ioList.get(i).getItemID() + "'";
                        stmt = con.createStatement();
                        stmt.executeUpdate(query);

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
            Toast.makeText(SalesOrderMainFragment.this, "Package added.", Toast.LENGTH_LONG).show();
            finish();
            startActivity(getIntent());
        }
    }
}
