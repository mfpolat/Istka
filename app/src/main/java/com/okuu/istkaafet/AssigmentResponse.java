package com.okuu.istkaafet;

/**
 * Created by Fatih on 8.7.2015.
 */
public class AssigmentResponse {
    private Assigment assigment;
    private int hospital_id;

    public Assigment getAssigment() {
        return assigment;
    }

    public void setAssigment(Assigment assigment) {
        this.assigment = assigment;
    }

    public int getHospital_id() {
        return hospital_id;
    }

    public void setHospital_id(int hospital_id) {
        this.hospital_id = hospital_id;
    }
}
