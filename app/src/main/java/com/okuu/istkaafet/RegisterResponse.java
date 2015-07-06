package com.okuu.istkaafet;


import com.google.gson.annotations.SerializedName;

/**
 * Created by Fatih on 2.7.2015.
 */
public class RegisterResponse {

    @SerializedName("access-token")
    private String access_token;

    private int doctor_id;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(int doctor_id) {
        this.doctor_id = doctor_id;
    }
}
