package com.example.madprojectnew;

public class Expense {
    private String id;
    private String category;
    private double amount;
    private String categoryImageUrl;

    public Expense() {

    }

    public Expense(String id, String category, double amount, String categoryImageUrl) {
        this.id = id;
        this.category = category;
        this.amount = amount;
        this.categoryImageUrl = categoryImageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategoryImageUrl() {
        return categoryImageUrl;
    }

    public void setCategoryImageUrl(String categoryImageUrl) {
        this.categoryImageUrl = categoryImageUrl;
    }
}
