package com.market.backend_market.models;

public class Offer {
    private Long id;
    private String title;
    private String description;
    private Double price;
    //private String imageUrl;          chcemy przechowywać zdjęcia ogłoszeń?
    private String kategory;
    private Long user_id;


    public Offer(){

    }

    public Offer(Long id, String title, String description, Double price, String kategory, Long user_id) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.kategory = kategory;
        this.user_id = user_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getKategory() {
        return kategory;
    }

    public void setKategory(String kategory) {
        this.kategory = kategory;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }
}

