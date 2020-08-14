package com.techkip.foodietapclient.model;

/**
 * Created by hillarie on 28/05/2018.
 */

public class User {

    private String Name;
    private String Password;
    private String Phone;
    private String IsStaff;
    private String SecureCode;

    public User() {
    }

    public User(String name, String phone, String password,String secureCode) {
        Name = name;
        Phone = phone;
        Password = password;
        SecureCode = secureCode;
        IsStaff="false";
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }

    public String getSecureCode() {
        return SecureCode;
    }

    public void setSecureCode(String secureCode) {
        SecureCode = secureCode;
    }
}




