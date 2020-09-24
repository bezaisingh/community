package com.example.community;

public class UserProfile {
    private String Fname;
    private String Mname;
    private String Lname;
    private String DOB;
    private String Gender;
    private String Address;
    private String State;
    private String Pin;
    private String District;
    private String Country;
    private String MobileNumber;
    private String emailId;
    private String UPIid;
    private String Kycid;



    public UserProfile(){

    }


    public UserProfile(
                       String userFname,
                       String userMname,
                       String userLname,
                       String userDOB,
                       String gender,
                       String mobileNumber,
                       String userEmail,
                       String userAddress,
                       String userState,
                       String userPin,
                       String userDistrict,
                       String userCountry,
                       String userUPIid
                       ){
                                  Fname = userFname;
                                  Mname = userMname;
                                  Lname = userLname;
                                  DOB = userDOB;
                                  Gender = gender;
                                  MobileNumber = mobileNumber;
                                  Address = userAddress;
                                  State = userState;
                                  Pin = userPin;
                                  District = userDistrict;
                                  Country = userCountry;
                                  emailId = userEmail;
                                  UPIid= userUPIid;


                         }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String userEmail) {
        emailId = userEmail;
    }

    public String getPin() {
        return Pin;
    }

    public void setPin(String pin) {
        Pin = pin;
    }

    public String getFname() {
        return Fname;
    }

    public void setFname(String fname) {
        Fname = fname;
    }

    public String getMname() {
        return Mname;
    }

    public void setMname(String mname) {
        Mname = mname;
    }

    public String getLname() {
        return Lname;
    }

    public void setLname(String lname) {
        Lname = lname;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getMobileNumber() {
        return MobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        MobileNumber = mobileNumber;
    }

    public String getUPIid() {
        return UPIid;
    }

    public void setUPIid(String UPIid) {
        this.UPIid = UPIid;
    }

}