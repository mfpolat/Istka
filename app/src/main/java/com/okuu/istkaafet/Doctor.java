package com.okuu.istkaafet;

/**
 * Created by fatih on 19.5.2015.
 */
public class Doctor {

    private int id;
    private String citizen_id;
    private String name;
    private String lastname;
    private String home_address;
    private String job_address;
    private String gsm_phone_number;
    private String phone_number;
    private String diploma_number;
    private String diploma_date;
    private int years_of_spec;
    private double latitude;
    private double longitude;
    private String last_access;
    private int clinical_id;
    private int users_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCitizen_id() {
        return citizen_id;
    }

    public void setCitizen_id(String citizen_id) {
        this.citizen_id = citizen_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getHome_address() {
        return home_address;
    }

    public void setHome_address(String home_address) {
        this.home_address = home_address;
    }

    public String getJob_address() {
        return job_address;
    }

    public void setJob_address(String job_address) {
        this.job_address = job_address;
    }

    public String getGsm_phone_number() {
        return gsm_phone_number;
    }

    public void setGsm_phone_number(String gsm_phone_number) {
        this.gsm_phone_number = gsm_phone_number;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getDiploma_number() {
        return diploma_number;
    }

    public void setDiploma_number(String diploma_number) {
        this.diploma_number = diploma_number;
    }

    public String getDiploma_date() {
        return diploma_date;
    }

    public void setDiploma_date(String diploma_date) {
        this.diploma_date = diploma_date;
    }

    public int getYears_of_spec() {
        return years_of_spec;
    }

    public void setYears_of_spec(int years_of_spec) {
        this.years_of_spec = years_of_spec;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLast_access() {
        return last_access;
    }

    public void setLast_access(String last_access) {
        this.last_access = last_access;
    }

    public int getClinical_id() {
        return clinical_id;
    }

    public void setClinical_id(int clinical_id) {
        this.clinical_id = clinical_id;
    }

    public int getUsers_id() {
        return users_id;
    }

    public void setUsers_id(int users_id) {
        this.users_id = users_id;
    }
}
