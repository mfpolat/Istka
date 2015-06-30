package com.okuu.istkaafet;

/**
 * Created by fatih on 19.5.2015.
 */
public class Doctor {

    private int id;
    private int user_id;
    private int tc;
    private String ad;
    private String soyad;
    private int diploma_no;
    private String hastane;
    private String uzmanlik;
    private String son_konum;
    private String son_erisim_zamani;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getTc() {
        return tc;
    }

    public void setTc(int tc) {
        this.tc = tc;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getSoyad() {
        return soyad;
    }

    public void setSoyad(String soyad) {
        this.soyad = soyad;
    }

    public int getDiploma_no() {
        return diploma_no;
    }

    public void setDiploma_no(int diploma_no) {
        this.diploma_no = diploma_no;
    }

    public String getHastane() {
        return hastane;
    }

    public void setHastane(String hastane) {
        this.hastane = hastane;
    }

    public String getUzmanlik() {
        return uzmanlik;
    }

    public void setUzmanlik(String uzmanlik) {
        this.uzmanlik = uzmanlik;
    }

    public String getSon_konum() {
        return son_konum;
    }

    public void setSon_konum(String son_konum) {
        this.son_konum = son_konum;
    }

    public String getSon_erisim_zamani() {
        return son_erisim_zamani;
    }

    public void setSon_erisim_zamani(String son_erisim_zamani) {
        this.son_erisim_zamani = son_erisim_zamani;
    }
}
