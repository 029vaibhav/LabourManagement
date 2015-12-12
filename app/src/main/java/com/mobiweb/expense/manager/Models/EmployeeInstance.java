package com.mobiweb.expense.manager.Models;

/**
 * Created by vaibhav on 12/12/15.
 */
public enum EmployeeInstance {

    DATA;

    Employee employee;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
