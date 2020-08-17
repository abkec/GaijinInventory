package com.example.orderandinventorysystem.ui.purchase;

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
import com.example.orderandinventorysystem.Model.ItemOrder;
import com.example.orderandinventorysystem.Model.Purchase;
import com.example.orderandinventorysystem.Model.Sales;
import com.example.orderandinventorysystem.Model.Vendor;
import com.example.orderandinventorysystem.R;
import com.example.orderandinventorysystem.ui.invoice.InvoiceMainFragment;
import com.example.orderandinventorysystem.ui.invoice.add_new_invoice;
import com.example.orderandinventorysystem.ui.item.ItemMain;
import com.example.orderandinventorysystem.ui.pack.PackageMain;
import com.example.orderandinventorysystem.ui.sales.SalesOrderMainFragment;
import com.example.orderandinventorysystem.ui.sales.Sales_Details;
import com.example.orderandinventorysystem.ui.sales.Sales_Invoices;
import com.example.orderandinventorysystem.ui.sales.Sales_Packages;
import com.example.orderandinventorysystem.ui.sales.edit_sales_orders;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class PurchaseMain extends AppCompatActivity{

    Vendor vendor;
    Menu menu1;
    ViewPager viewPager;
    TabLayout tabLayout;
    ArrayList<ItemOrder> ioList;
    Purchase purchase;
    boolean payCheck=false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchase_main);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Purchase Order");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        String intentSalesID = intent.getStringExtra("Purchase");
        PurchaseMainInfo purchaseMainInfo= new PurchaseMainInfo(intentSalesID);
        purchaseMainInfo.execute("");

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
        inflater.inflate(R.menu.purchase_main_menu, menu);
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

            case R.id.paid:{

                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage("Once you mark as paid, you cannot revert your action. Confirm?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                AddBill addBill = new AddBill(purchase.getpID());
                                addBill.execute("");
                                Intent intent = new Intent(PurchaseMain.this, PurchaseMain.class);
                                intent.putExtra("Purchase", purchase.getpID());
                                finish();
                                startActivity(intent);

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

            case R.id.edit_sales: {
                Intent intent = new Intent(this, edit_purchase_orders.class);
                intent.putExtra("PurchaseEdit", purchase);
                startActivityForResult(intent, 10);
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

                                DeletePurchase deletePurchase = new DeletePurchase(purchase.getpID());
                                deletePurchase.execute("");
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
        canvas.drawText("To : ", 100,185, paint);

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
        canvas.drawText("From : ", 100,465, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextSize(20);
        canvas.drawText(vendor.getCompanyName(), 100,505, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(20);
        canvas.drawText(vendor.getAddress(), 100,545, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(20);
        canvas.drawText(vendor.getPostcode() + ", " + vendor.getCity() + ", " + vendor.getState(), 100,585, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(20);
        canvas.drawText("Email : " + vendor.getEmail(), 100,625, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(20);
        canvas.drawText("Phone: " + vendor.getPhone(), 100,665, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(20);
        canvas.drawText("Mobile: " + vendor.getMobile(), 100,705, paint);

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
        canvas.drawText(purchase.getpID(), 1050,585, paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(20);
        canvas.drawText(purchase.getdDate(), 1050,625, paint);

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
        canvas.drawText(String.format("%.2f", purchase.getpAmount()), pageWidth-30, yAxis, paint);
        yAxis += 200;

        //Footer
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(255,69,0));
        paint.setTextSize(30f);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("THANK YOU FOR YOUR BUSINESS ! ", pageWidth/2, yAxis, paint);


        salesPdf.finishPage(page1);

        File file = new File (getExternalFilesDir(null),"PurchaseOrder_" + purchase.getpID() + ".pdf");


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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        setResult(2);
        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2) {
            finish();

        }

        else if (resultCode == 3) {

            Intent intent = new Intent(this, PurchaseMain.class);
            intent.putExtra("Purchase", purchase.getpID());
            finish();
            startActivity(intent);
        }
    }

    private void setUpViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new Purchase_Details(), "DETAILS");
        adapter.addFragment(new Purchase_Payments(), "PAYMENT");

        viewPager.setAdapter(adapter);
    }

    public class PurchaseMainInfo extends AsyncTask<String,String,String> {

        String purID;
        Purchase purchaseDB;
        ArrayList <ItemOrder> ioListDB= new ArrayList<>();

        public Purchase getSales() {
            return purchaseDB;
        }

        public ArrayList<ItemOrder> getIoList() {
            return ioList;
        }

        PurchaseMainInfo(String purID) {
            this.purID = purID;
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


                    String query = " SELECT * FROM PURCHASE WHERE pID ='" + purID + "'";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs.next()) {

                        purchaseDB = new Purchase(rs.getString(1), rs.getString(2),
                                rs.getString(3), rs.getString(4),
                               rs.getString(5), rs.getDouble(6));
                    }

                    query = " SELECT * FROM VENDOR WHERE venID ='" + purchaseDB.getVenID() + "'";
                    stmt = con.createStatement();
                    rs = stmt.executeQuery(query);
                    if (rs.next()) {

                        vendor = new Vendor(rs.getString(1), rs.getString(2), rs.getString(3),
                                rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7),
                                rs.getString(8), rs.getString(9), rs.getString(10),rs.getString(11),
                                rs.getString(12));
                    }

                    query = " SELECT * FROM BILL WHERE pID ='" + purID + "'";
                    stmt = con.createStatement();
                    rs = stmt.executeQuery(query);
                    if (rs.next()) {

                        payCheck = true;
                    }

                    query = " SELECT * FROM ITEMORDER WHERE orderID ='" + purID + "'";
                    stmt = con.createStatement();
                    rs = stmt.executeQuery(query);
                    while (rs.next()) {

                        ioListDB.add(new ItemOrder(rs.getString(1), rs.getString(2),
                                rs.getString(3), rs.getDouble(4),
                                rs.getDouble(5), rs.getInt(6), rs.getDouble(7)));
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

            purchase = purchaseDB;
            ioList = ioListDB;

            TextView p_Order = findViewById(R.id.p_Order);
            p_Order.setText(purchase.getpID());

            TextView vendor = findViewById(R.id.vendor);
            vendor.setText(purchase.getVenName());

            TextView salesPrice = findViewById(R.id.sales_order_price);
            salesPrice.setText(String.format("MYR%.2f", purchase.getpAmount()));

            TextView salesDate = findViewById(R.id.sales_order_date);
            salesDate.setText(purchase.getdDate());

            TextView salesStatus = findViewById(R.id.sales_order_status);
            salesStatus.setText(purchase.getpStatus());

            if (payCheck) {

                salesStatus = findViewById(R.id.sales_order_status);
                salesStatus.setText("Closed");
            }

            if(purchase.getpStatus().equals("Removed") || payCheck) {

                MenuItem menuItem2 = menu1.findItem(R.id.delete);
                menuItem2.setVisible(false);
                menuItem2 = menu1.findItem(R.id.edit_sales);
                menuItem2.setVisible(false);
                menuItem2 = menu1.findItem(R.id.paid);
                menuItem2.setVisible(false);
            }
        }
    }

    public class DeletePurchase extends AsyncTask<String,String,String> {

        String salesID;

        DeletePurchase(String salesID) {
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

                    String query = " UPDATE PURCHASE SET PSTATUS='Removed' WHERE PID ='" + salesID + "'";
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
            Toast.makeText(PurchaseMain.this, "Purchase Order deleted.", Toast.LENGTH_LONG).show();
        }
    }

    public class AddBill extends AsyncTask<String,String,String> {

        String invoiceID;

        AddBill(String invoiceID) {
            this.invoiceID = invoiceID;
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
                    String query = "SELECT * FROM BILL ORDER BY billID DESC LIMIT 1";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    String latestID="";

                    if (rs.next()) {
                        latestID = rs.getString(1);
                        int numID = Integer.parseInt(latestID.substring(3,8)) + 1;
                        if (numID < 10)
                            latestID = "BL-0000" + Integer.toString(numID);
                        else if (numID < 100)
                            latestID = "BL-000" + Integer.toString(numID);
                        else if (numID < 1000)
                            latestID = "BL-00" + Integer.toString(numID);
                        else if (numID < 10000)
                            latestID = "BL-0" + Integer.toString(numID);
                        else if (numID < 100000)
                            latestID = "BL-" + Integer.toString(numID);

                        Log.d("ID", latestID);
                    }

                    else {
                        latestID = "BL-00001";
                        Log.d("ID", latestID);
                    }

                    String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                    query = "INSERT INTO BILL VALUES('" + latestID + "', '" + purchase.getpID() + "', '" +
                            purchase.getVenName() + "', '" + currentDate + "', '" + purchase.getpAmount() + "')";

                    stmt = con.createStatement();
                    stmt.executeUpdate(query);

                    for (int i=0; i < ioList.size(); i++) {

                        query = "UPDATE ITEM SET ITEMQUANTITY= ITEMQUANTITY + '" + ioList.get(i).getQuantity() + "', ITEMQUANTITYPHY= ITEMQUANTITYPHY + '" + ioList.get(i).getQuantity() + "' WHERE ITEMID='" + ioList.get(i).getItemID() + "'";
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
            Toast.makeText(PurchaseMain.this, "Bill added.", Toast.LENGTH_LONG).show();
        }
    }
}
