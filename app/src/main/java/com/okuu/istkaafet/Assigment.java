package com.okuu.istkaafet;

/**
 * Created by Fatih on 8.7.2015.
 */
public class Assigment {
    private int id;
    private int doctor_id;
    private int clinical_id;
    private int scenario_id;
    private String time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(int doctor_id) {
        this.doctor_id = doctor_id;
    }

    public int getClinical_id() {
        return clinical_id;
    }

    public void setClinical_id(int clinical_id) {
        this.clinical_id = clinical_id;
    }

    public int getScenario_id() {
        return scenario_id;
    }

    public void setScenario_id(int scenario_id) {
        this.scenario_id = scenario_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
