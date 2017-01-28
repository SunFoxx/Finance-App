package com.esharoha.financeapp.common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.LinkedList;

public class Action implements Serializable {
    private int count;
    private String description = "";
    private Category category;
    public static LinkedList<Action> allActions = new LinkedList<>();

    public Action(int count, Category category) {
        this.count = count;
        this.category = category;
    }

    public Action(int count, String description, Category category) {
        this.count = count;
        this.description = description;
        this.category = category;
    }

    public int getCount() {
        return count;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

}
