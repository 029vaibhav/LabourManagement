package com.mobiweb.expense.manager.Models;

import java.util.List;

/**
 * Created by vaibhav on 12/12/15.
 */
public class Employee {

    String id;
    String name, address, phone, date, workType;
    List<PaymentData> paymentHistory;
    int totalPayment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public List<PaymentData> getPaymentHistory() {
        return paymentHistory;
    }

    public void setPaymentHistory(List<PaymentData> paymentHistory) {
        this.paymentHistory = paymentHistory;
    }

    public int getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(int totalPayment) {
        this.totalPayment = totalPayment;
    }
}
