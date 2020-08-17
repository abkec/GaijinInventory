package com.example.orderandinventorysystem.ui.pack;

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
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderandinventorysystem.ConnectionPhpMyAdmin;
import com.example.orderandinventorysystem.Model.Customer;
import com.example.orderandinventorysystem.Model.Item;
import com.example.orderandinventorysystem.Model.ItemOrder;
import com.example.orderandinventorysystem.Model.Pack;
import com.example.orderandinventorysystem.Model.Sales;
import com.example.orderandinventorysystem.Model.Shipment;
import com.example.orderandinventorysystem.R;
import com.example.orderandinventorysystem.ui.item.ItemMain;
import com.example.orderandinventorysystem.ui.sales.ItemOrderListAdapter;
import com.example.orderandinventorysystem.ui.sales.SalesOrderMainFragment;
import com.example.orderandinventorysystem.ui.sales.add_sales_orders;
import com.example.orderandinventorysystem.ui.sales.edit_sales_orders;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class PackageMain extends AppCompatActivity {

    Customer cust;
    Menu menu1;
    String latestShipID;
    int totalQuantity;
    Pack pack;
    ArrayList <Item> itemList;
    ArrayList <ItemOrder> ioList;
    Shipment shipment;
    String intentPackageID;
    boolean shipCheck=false;
    RecyclerView recyclerView;
    ItemPackListAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.package_main);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Package");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        intentPackageID = intent.getStringExtra("Package");
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.execute("");

        ioList = new ArrayList<>();
        itemList = new ArrayList<>();
        recyclerView = findViewById(R.id.itemLine_2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ItemPackListAdapter(this, ioList, itemList);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.package_main_menu, menu);
        menu1 = menu;
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 4) {
            setResult(3);
            finish();
            Intent intent = new Intent(this, PackageMain.class);
            intent.putExtra("Package", intentPackageID);
            startActivityForResult(intent, 3);
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
        canvas.drawText("Delivery Order", 925, 505, paint);

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
        canvas.drawText(pack.getPackID(), 1050,585, paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(20);
        canvas.drawText(pack.getPackDate(), 1050,625, paint);


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
        canvas.drawText("Quantity", 800, 830, paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Item Unit", pageWidth-30, 830, paint);

        canvas.drawLine(80, 790, 80, 860, paint);
        canvas.drawLine(780, 790, 780, 860, paint);
        canvas.drawLine(1010, 790, 1010, 860, paint);

        //ItemOrder
        int num=1, yAxis=850;
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        for(int i=0; i<ioList.size(); i++) {
            yAxis += 50;
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(ioList.get(i).getItemName(), 100, yAxis, paint);
            canvas.drawText(Integer.toString(num), 40, yAxis, paint);
            canvas.drawText(Integer.toString(ioList.get(i).getQuantity()), 800, yAxis, paint);
            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(itemList.get(i).getItemUnit(), pageWidth-30, yAxis, paint);
            canvas.drawLine(20, yAxis+15, pageWidth-20, yAxis+15, paint);
            num+=1;
        }

        yAxis += 15;
        canvas.drawLine(20, 860, 20, yAxis, paint);
        canvas.drawLine(80, 860, 80, yAxis, paint);
        canvas.drawLine(780, 860, 780, yAxis, paint);
        canvas.drawLine(1010, 860, 1010, yAxis, paint);
        canvas.drawLine(pageWidth-20, 860, pageWidth-20, yAxis, paint);
        canvas.drawLine(20, yAxis, pageWidth-20, yAxis, paint);


        yAxis = pageHeight - 600;

        //Draw Line
        yAxis += 200;
        paint.setColor(Color.rgb(0,0,0));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        canvas.drawLine(100, yAxis, 450, yAxis, paint);
        canvas.drawLine(700, yAxis, 1050, yAxis, paint);

        yAxis +=30;
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(25);
        canvas.drawText("Authorized By", 273,yAxis, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(25);
        canvas.drawText("Received By", 880,yAxis, paint);

        yAxis += 200;

        //Footer
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(255,69,0));
        paint.setTextSize(30f);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("THANK YOU FOR YOUR BUSINESS ! ", pageWidth/2, yAxis, paint);


        salesPdf.finishPage(page1);

        File file = new File (getExternalFilesDir(null),"DeliveryOrder" + pack.getPackID() + ".pdf");


        try {
            salesPdf.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "Delivery Order PDF has been saved into your device", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Please allow the storage permission for this application in your android settings",
                    Toast.LENGTH_LONG).show();
        }

        salesPdf.close();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.download_sales: {

                pdfGenerate();
                return true;
            }

            case R.id.addShipment: {
                Intent intent = new Intent(this, add_shipment.class);
                intent.putExtra("ShipID", latestShipID);
                intent.putExtra("PackID", intentPackageID);
                startActivityForResult(intent, 10);
                return true;
            }

            case R.id.delivered: {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage("Once you mark as delivered, you cannot revert your action. Confirm?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                MarkDelivered markDelivered = new MarkDelivered();
                                markDelivered.execute("");
                                setResult(3);
                                finish();
                                Intent intent = new Intent(PackageMain.this, PackageMain.class);
                                intent.putExtra("Package", intentPackageID);
                                startActivityForResult(intent, 3);

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

            case R.id.delete: {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage("Confirm delete?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (shipCheck) {
                                    DeleteShipment deleteShipment = new DeleteShipment();
                                    deleteShipment.execute("");
                                    setResult(3);
                                    finish();
                                    Intent intent = new Intent(PackageMain.this, PackageMain.class);
                                    intent.putExtra("Package", intentPackageID);
                                    startActivityForResult(intent, 3);
                                }

                                else {

                                    DeletePack deletePack = new DeletePack();
                                    deletePack.execute("");
                                    setResult(3);
                                    finish();

                                }
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
    public void onBackPressed() {
        setResult(3);
        this.finish();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public class PackageInfo extends AsyncTask<String,String,String> {

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

                    Log.d("HAHA", intentPackageID);
                    String query = " SELECT * FROM PACKAGE WHERE packID ='" + intentPackageID + "'";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs.next()) {

                        pack = new Pack(rs.getString(1), rs.getString(2),
                                rs.getString(3), rs.getString(4));
                    }

                    String custID="";
                    query = " SELECT * FROM SALES WHERE salesID ='" + pack.getSalesID() + "'";
                    stmt = con.createStatement();
                    rs = stmt.executeQuery(query);
                    if (rs.next()) {

                        custID = rs.getString(2);
                    }

                    query = " SELECT * FROM CUSTOMER WHERE custID ='" + custID + "'";
                    stmt = con.createStatement();
                    rs = stmt.executeQuery(query);
                    if (rs.next()) {

                        cust = new Customer(rs.getString(1), rs.getString(2), rs.getString(3),
                                rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7),
                                rs.getString(8), rs.getString(9), rs.getString(10),rs.getString(11),
                                rs.getString(12),rs.getString(13), rs.getString(14));
                    }


                    query = " SELECT * FROM ITEMORDER WHERE orderID ='" + pack.getSalesID() + "'";
                    stmt = con.createStatement();
                    rs = stmt.executeQuery(query);
                    while (rs.next()) {

                        ioList.add(new ItemOrder(rs.getString(1), rs.getString(2),
                                rs.getString(3), rs.getDouble(4),
                                rs.getDouble(5), rs.getInt(6), rs.getDouble(7)));
                        totalQuantity += rs.getInt(6);

                        query = " SELECT * FROM ITEM WHERE itemID ='" + rs.getString(2) + "'";
                        stmt = con.createStatement();
                        ResultSet rs2 = stmt.executeQuery(query);

                        if (rs2.next()) {
                            itemList.add(new Item(rs2.getString(1), rs2.getString(2), rs2.getString(3), rs2.getString(4)
                                    , rs2.getInt(5), rs2.getInt(6), rs2.getDouble(7), rs2.getDouble(8), rs2.getString(9)));

                        }

                    }

                    query = " SELECT * FROM SHIPMENT WHERE packID ='" + intentPackageID + "'";
                    stmt = con.createStatement();
                    rs = stmt.executeQuery(query);
                    if (rs.next()) {

                        shipment = new Shipment(rs.getString(1), rs.getString(2),
                                rs.getString(3), rs.getString(4));
                        shipCheck=true;
                    }

                    query = "SELECT * FROM SHIPMENT ORDER BY SHIPID DESC LIMIT 1";
                    stmt = con.createStatement();
                    rs = stmt.executeQuery(query);
                    String latestID;

                    if (rs.next()) {
                        latestID = rs.getString(1);
                        int numID = Integer.parseInt(latestID.substring(3,8)) + 1;
                        if (numID < 10)
                            latestID = "SH-0000" + Integer.toString(numID);
                        else if (numID < 100)
                            latestID = "SH-000" + Integer.toString(numID);
                        else if (numID < 1000)
                            latestID = "SH-00" + Integer.toString(numID);
                        else if (numID < 10000)
                            latestID = "SH-0" + Integer.toString(numID);
                        else if (numID < 100000)
                            latestID = "SH-" + Integer.toString(numID);

                    }

                    else {
                        latestID = "SH-00001";
                    }

                    latestShipID = latestID;

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

            TextView packID = findViewById(R.id.package_name);
            packID.setText(pack.getPackID());

            TextView packStatus = findViewById(R.id.package_status);
            packStatus.setText(pack.getPackStatus());

            TextView salesDetails = findViewById(R.id.salesDetails);
            salesDetails.setText("Sales order created: " + pack.getSalesID());

            TextView packDetails = findViewById(R.id.packDetails);
            packDetails.setText("Package created: " + pack.getPackID());

            TextView package_date = findViewById(R.id.package_date);
            package_date.setText(pack.getPackDate());

            TextView quantity = findViewById(R.id.quantity);
            quantity.setText(Integer.toString(totalQuantity));

            if (shipCheck) {

                MenuItem menuItem = menu1.findItem(R.id.addShipment);
                menuItem.setVisible(false);
                MenuItem menuItem2 = menu1.findItem(R.id.delete);
                menuItem2.setTitle("Delete Shipment");
                MenuItem menuItem3 = menu1.findItem(R.id.delivered);
                menuItem3.setVisible(true);

                TextView shipDetails = findViewById(R.id.shipDetails);
                shipDetails.setText("Shipped through " + shipment.getCarrier());
                TextView ship_date = findViewById(R.id.ship_date);
                ship_date.setText(shipment.getShipDate());
                TableRow carrierRow = findViewById(R.id.carrierRow);
                carrierRow.setVisibility(View.VISIBLE);
                TableRow shipDateRow = findViewById(R.id.shipDateRow);
                shipDateRow.setVisibility(View.VISIBLE);
                TextView ship_id = findViewById(R.id.ship_id);
                ship_id.setText(shipment.getShipID());
                ship_id.setVisibility(View.VISIBLE);
                TextView shipIDText = findViewById(R.id.shipIDText);
                shipIDText.setVisibility(View.VISIBLE);

            }

            if (pack.getPackStatus().equals("DELIVERED")) {

                TableRow tbd = findViewById(R.id.deliveredRow);
                tbd.setVisibility(View.VISIBLE);
                MenuItem menui = menu1.findItem(R.id.delivered);
                menui.setVisible(false);
                MenuItem menuItem = menu1.findItem(R.id.addShipment);
                menuItem.setVisible(false);
                MenuItem menuItem2 = menu1.findItem(R.id.delete);
                menuItem2.setVisible(false);
            }

            recyclerView.setAdapter(adapter);
        }
    }

    public class DeletePack extends AsyncTask<String,String,String> {

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

                    String query = " DELETE FROM PACKAGE WHERE PACKID ='" + intentPackageID + "'";
                    Statement stmt = con.createStatement();
                    stmt.executeUpdate(query);

                    for (int i=0; i < ioList.size(); i++) {

                        query = "UPDATE ITEM SET ITEMQUANTITYPHY= ITEMQUANTITYPHY + '" + ioList.get(i).getQuantity() + "' WHERE ITEMID='" + ioList.get(i).getItemID() + "'";
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
            Toast.makeText(PackageMain.this, "Package deleted.", Toast.LENGTH_LONG).show();
        }
    }

    public class DeleteShipment extends AsyncTask<String,String,String> {

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

                    String query = " DELETE FROM Shipment WHERE PACKID ='" + intentPackageID + "'";
                    Statement stmt = con.createStatement();
                    stmt.executeUpdate(query);

                    query = " UPDATE PACKAGE SET PACKSTATUS='PACKED' WHERE PACKID ='" + intentPackageID + "'";
                    stmt = con.createStatement();
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
            Toast.makeText(PackageMain.this, "Shipment deleted.", Toast.LENGTH_LONG).show();
        }
    }

    public class MarkDelivered extends AsyncTask<String,String,String> {

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

                    String query = " UPDATE PACKAGE SET PACKSTATUS='DELIVERED' WHERE PACKID ='" + intentPackageID + "'";
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
            Toast.makeText(PackageMain.this, "Package has been delivered.", Toast.LENGTH_LONG).show();
        }
    }
}
