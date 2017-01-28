package com.esharoha.financeapp.common;

import java.io.Serializable;
import java.util.ArrayList;

public class Category implements Serializable {

    public static ArrayList<Category> categories = new ArrayList<>();

    private String name;

    static {
        categories.add(new Category("Food"));
        categories.add(new Category("Home"));
        categories.add(new Category("Health"));
        categories.add(new Category("Pet"));
        categories.add(new Category("E-commerce"));
        categories.add(new Category("Car"));
        categories.add(new Category("Others"));
    }

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
