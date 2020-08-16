package com.example.orderandinventorysystem.Model;

import java.io.Serializable;

public class Staff implements Serializable {
    private String staffID, staffName,  staffMobile, department,password;
    private double staffPay;
    public Staff(String staffID, String staffName, String staffMobile, double staffPay, String department, String password)
    {
        this.staffID = staffID;
        this.staffName = staffName;
        this.staffMobile = staffMobile;
        this.staffPay = staffPay;
        this.department = department;
        this.password = password;
    }

    public String getStaffID() {return staffID;}

    public void setStaffID(String staffID) {this.staffID = staffID;}

    public String getStaffName(){return staffName;}

    public void setStaffName (String staffName){this.staffName = staffName;}

    public String getStaffMobile(){return staffMobile;}

    public void setStaffMobile(String staffMobile){this.staffMobile = staffMobile;}

    public double getStaffPay() {return staffPay;}

    public void setStaffPay (double staffPay) {this.staffPay = staffPay;}

    public String getDepartment(){return department;}

    public void setDepartment(String department){this.department = department;}

    public String getPassword(){return password;}

    public void setPassword(String password){this.password = password;}

}
