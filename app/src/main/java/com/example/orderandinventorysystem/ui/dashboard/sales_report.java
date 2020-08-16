package com.example.orderandinventorysystem.ui.dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
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
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.orderandinventorysystem.ConnectionPhpMyAdmin;
import com.example.orderandinventorysystem.Model.Invoice;
import com.example.orderandinventorysystem.R;
import com.example.orderandinventorysystem.ui.invoice.add_new_invoice;
import com.example.orderandinventorysystem.ui.purchase.add_new_purchase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class sales_report extends AppCompatActivity {

    EditText startDate, endDate;
    String currentDate;
    Calendar myCalendar = Calendar.getInstance();
    Date today;
    ArrayList<Invoice> invList;
    Date dateEnter, dateEnter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_report);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Generate Sales Report");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView text_invoice_date_input = findViewById(R.id.text_invoice_date_input);
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        text_invoice_date_input.setText(currentDate);

        invList = new ArrayList<>();

        startDate = findViewById(R.id.text_start_input);
        endDate = findViewById(R.id.text_end_input);

        myCalendar.set(Calendar.HOUR_OF_DAY, 0);
        myCalendar.set(Calendar.MINUTE, 0);
        myCalendar.set(Calendar.SECOND, 0);
        myCalendar.set(Calendar.MILLISECOND, 0);
        today = myCalendar.getTime();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR,year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                dateEnter = myCalendar.getTime();

                if(dateEnter.after(today)){
                    startDate.setError("You cannot choose an upcoming date");
                    Toast toast = Toast.makeText(getApplicationContext(),"You cannot choose an upcoming date, please reselect a valid date", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else{
                    updateLabel();
                }
            }
        };

        final DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR,year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                dateEnter2 = myCalendar.getTime();

                if(dateEnter2.after(today)){
                    endDate.setError("You cannot choose an upcoming date");
                    Toast toast = Toast.makeText(getApplicationContext(),"You cannot choose an upcoming date, please reselect a valid date", Toast.LENGTH_SHORT);
                    toast.show();
                } else if(!startDate.getText().toString().isEmpty()) {
                    if (dateEnter2.before(dateEnter)) {

                        endDate.setError("You cannot choose a past date from start date");
                        Toast toast = Toast.makeText(getApplicationContext(),"You cannot choose a past date from start date, please reselect a valid date", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else{
                        updateLabel2();
                    }
                }

                else{
                    updateLabel2();
                }

            }
        };

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(sales_report.this,date,myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(sales_report.this,date2,myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!startDate.getText().toString().isEmpty() && !endDate.getText().toString().isEmpty()){

                    if (dateEnter2.before(dateEnter)) {

                        endDate.setError("You cannot choose a past date from start date");
                        Toast toast = Toast.makeText(getApplicationContext(),"You cannot choose a past date from start date, please reselect a valid date", Toast.LENGTH_SHORT);
                        toast.show();
                    }

                    else {
                        GenerateSalesReport generateSalesReport = new GenerateSalesReport(startDate.getText().toString(), endDate.getText().toString());
                        generateSalesReport.execute("");
                    }

                }else{

                    if(startDate.getText().toString().isEmpty())
                        startDate.setError("Please enter a valid date !");
                    if (endDate.getText().toString().isEmpty())
                        endDate.setError("Please enter a valid date !");

                }


            }
        });
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
        canvas.drawText("Gaijin Company", 945, 245, paint);
        paint.setStyle(Paint.Style.FILL);

        //draw Gaijin Logo
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.inventory);
        scaledbmp = Bitmap.createScaledBitmap(bmp, 200,125,false);
        canvas.drawBitmap(scaledbmp,830,70, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(60);
        canvas.drawText("Sales Report", pageWidth/2, 100, paint);

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
        paint.setTextSize(20);
        canvas.drawText("Start Date : ", 800,345, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(20);
        canvas.drawText("End Date : ", 800,385, paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(20);
        canvas.drawText(startDate.getText().toString(), 1050,345, paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(20);
        canvas.drawText(endDate.getText().toString(), 1050,385, paint);

        //Columns Name
        paint.setTextSize(20f);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawLine(20, 500, pageWidth-20, 500, paint);
        canvas.drawLine(20, 570, pageWidth-20, 570, paint);
        canvas.drawLine(20, 500, 20, 570, paint);
        canvas.drawLine(pageWidth-20, 500, pageWidth-20, 570, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText("Date", 40, 540, paint);
        canvas.drawText("Due Date", 200, 540, paint);
        canvas.drawText("Invoice Number", 360, 540, paint);
        canvas.drawText("Paid (MYR)", 650, 540, paint);
        canvas.drawText("Unpaid (MYR)", 915, 540, paint);

        canvas.drawLine(180, 500, 180, 570, paint);
        canvas.drawLine(340, 500, 340, 570, paint);
        canvas.drawLine(630, 500, 630, 570, paint);
        canvas.drawLine(895, 500, 895, 570, paint);

        //Invoice
        int num=1, yAxis=560;
        double unpaid=0, paid=0, total=0;
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        for(int i=0; i<invList.size(); i++) {

            double discount=0;
            yAxis += 50;
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(invList.get(i).getInvDate(), 40, yAxis, paint);
            canvas.drawText(invList.get(i).getInvDueDate(), 200, yAxis, paint);
            canvas.drawText(invList.get(i).getInvID(), 360, yAxis, paint);

            paint.setTextAlign(Paint.Align.RIGHT);

            if (invList.get(i).getInvStatus().equals("Confirmed")) {

                canvas.drawText("-", 885, yAxis, paint);
                canvas.drawText(String.format("%.2f", invList.get(i).getInvPrice()), pageWidth-30, yAxis, paint);
                unpaid+=invList.get(i).getInvPrice();
            }

            else {

                canvas.drawText("-", pageWidth-30, yAxis, paint);
                canvas.drawText(String.format("%.2f", invList.get(i).getInvPrice()), 885, yAxis, paint);
                paid+=invList.get(i).getInvPrice();
            }
            canvas.drawLine(20, yAxis+15, pageWidth-20, yAxis+15, paint);
            num+=1;
        }

        yAxis += 15;
        canvas.drawLine(20, 570, 20, yAxis, paint);
        canvas.drawLine(180, 570, 180, yAxis, paint);
        canvas.drawLine(340, 570, 340, yAxis, paint);
        canvas.drawLine(630, 570, 630, yAxis, paint);
        canvas.drawLine(895, 570, 895, yAxis, paint);
        canvas.drawLine(pageWidth-20, 570, pageWidth-20, yAxis, paint);
        canvas.drawLine(20, yAxis, pageWidth-20, yAxis, paint);

        //Total
        yAxis += 50;
        canvas.drawLine(630, yAxis-50, pageWidth-20, yAxis-50, paint);
        canvas.drawLine(630, yAxis-50, 630, yAxis+30, paint);
        canvas.drawLine(630, yAxis+30, pageWidth-20, yAxis+30, paint);
        canvas.drawLine(pageWidth-20, yAxis-50, pageWidth-20, yAxis+30, paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Total (MYR): ", 620, yAxis, paint);

        canvas.drawLine(895, yAxis-50, 895, yAxis+30, paint);
        canvas.drawText(String.format("%.2f", unpaid), pageWidth-30, yAxis, paint);
        canvas.drawText(String.format("%.2f", paid), 885, yAxis, paint);

        salesPdf.finishPage(page1);
        File file = new File (getExternalFilesDir(null),"SalesReport" + startDate.getText().toString() + "-" + endDate.getText().toString() + ".pdf");

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

    private void updateLabel() {
        String myFormat = "dd-MM-YYYY";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        startDate.setError(null);
        startDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateLabel2() {
        String myFormat = "dd-MM-YYYY";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        endDate.setError(null);
        endDate.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }

    public class GenerateSalesReport extends AsyncTask<String,String,String> {

        String checkConnection = "";
        boolean isSuccess = false;
        String start, end;

        GenerateSalesReport(String start, String end) {

            this.start=start;
            this.end = end;
        }

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

                    String query =  "SELECT * FROM INVOICE";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    Date date2=new SimpleDateFormat("dd-MM-yyyy").parse(start);
                    Date date3=new SimpleDateFormat("dd-MM-yyyy").parse(end);

                    while(rs.next()) {

                        Date date1=new SimpleDateFormat("dd-MM-yyyy").parse(rs.getString(4));
                        if (date1.compareTo(date2) >= 0 && date1.compareTo(date3) <= 0) {

                            invList.add(new Invoice(rs.getString(1), rs.getString(2),
                                    rs.getString(3), rs.getString(4),
                                    rs.getString(5), rs.getDouble(6), rs.getString(7)));

                            Log.d("Haa", rs.getString(4));
                        }
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
            if(invList.size()==0) {

                Toast toast = Toast.makeText(getApplicationContext(),"No invoices found within the selected dates.", Toast.LENGTH_SHORT);
                toast.show();
            }
            else
                pdfGenerate();
        }
    }
}