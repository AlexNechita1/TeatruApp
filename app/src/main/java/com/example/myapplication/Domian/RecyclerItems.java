package com.example.myapplication.Domian;

public class RecyclerItems {
    private String title;
    private String imageUrl;

    public RecyclerItems(String title, String ImageUrl) {
        this.title = title;
        this.imageUrl = ImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

}
