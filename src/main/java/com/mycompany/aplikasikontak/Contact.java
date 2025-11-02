package com.mycompany.aplikasikontak;

public class Contact {
    private int id;
    private String name;
    private String phone;
    private String category;

    public Contact() {}

    public Contact(int id, String name, String phone, String category) {
        this.id = id; this.name = name; this.phone = phone; this.category = category;
    }
    public Contact(String name, String phone, String category) {
        this(0, name, phone, category);
    }

    // getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
