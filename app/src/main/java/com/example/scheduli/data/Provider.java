package com.example.scheduli.data;

import java.util.ArrayList;

public class Provider extends User {


    String uid;
    String companyName;
    String profession;
    ArrayList<Service> services;

    public Provider(String userName, String fullName, String email, String companyName, String profession) {
        super(userName, fullName, email);
        // TODO: get user Uid
        //  this.uid = super.getUserId;
        this.companyName = companyName;
        this.profession = profession;
        this.services = new ArrayList<Service>();
    }

    public Provider(String companyName, String profession) {
        // TODO: get user Uid
        //  this.uid = super.getUserId;
        this.companyName = companyName;
        this.profession = profession;
        this.services = new ArrayList<Service>();
    }

    public Provider(){

    }



    public String getCompanyName() {
        return companyName;
    }

    public String getProfession() {
        return profession;
    }

    public ArrayList<Service> getServices() {
        return services;
    }


    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public void setServices(ArrayList<Service> services) {
        this.services = services;
    }
}
