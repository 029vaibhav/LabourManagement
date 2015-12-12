package com.mobiweb.expense.manager.Models;

import org.joda.time.format.DateTimeFormat;

/**
 * Created by vaibhav on 12/12/15.
 */
public class PaymentData implements Comparable<PaymentData> {

    String date, modeOfPayment;
    int amount;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getModeOfPayment() {
        return modeOfPayment;
    }

    public void setModeOfPayment(String modeOfPayment) {
        this.modeOfPayment = modeOfPayment;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public int compareTo(PaymentData another) {

        return DateTimeFormat.forPattern("dd-MM-yy").parseDateTime(getDate()).compareTo(DateTimeFormat.forPattern("dd-MM-yy").parseDateTime(another.getDate()));
    }
}
