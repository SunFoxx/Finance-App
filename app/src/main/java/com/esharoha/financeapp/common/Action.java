package com.esharoha.financeapp.common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.LinkedList;

public class Action implements Serializable {
    private int count;
    private String description = "";
    private Category category;
    private GregorianCalendar date;
    public static LinkedList<Action> allActions = new LinkedList<>();

    public Action(int count, Category category) {
        this.count = count;
        this.category = category;
        this.date = new GregorianCalendar();
    }

    public Action(int count, String description, Category category) {
        this.count = count;
        this.description = description;
        this.category = category;
        this.date = new GregorianCalendar();
    }

    public Action(int count, Category category, GregorianCalendar date) {
        this.count = count;
        this.category = category;
        this.date = date;
    }

    public Action(int count, Category category, String description, GregorianCalendar date) {
        this.count = count;
        this.category = category;
        this.date = date;
        this.description = description;
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

    public GregorianCalendar getDate() {
        return date;
    }
}
