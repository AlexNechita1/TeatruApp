package com.example.myapplication.Domian;

public class ApreciateItems {
    public String getTitlu() {
        return titlu;
    }

    public void setTitlu(String titlu) {
        this.titlu = titlu;
    }

    public String getGen() {
        return gen;
    }

    public void setGen(String gen) {
        this.gen = gen;
    }

    public String getDurata() {
        return durata;
    }

    public void setDurata(String durata) {
        this.durata = durata;
    }

    public ApreciateItems(String titlu, String gen, String durata,String imageUrl) {
        this.titlu = titlu;
        this.gen = gen;
        this.durata = durata;
        this.imageUrl=imageUrl;
    }

    private String titlu;
    private String gen;
    private String durata;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private String imageUrl;

}
