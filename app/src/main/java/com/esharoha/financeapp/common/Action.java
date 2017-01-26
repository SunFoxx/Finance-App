package com.esharoha.financeapp.common;


import java.util.LinkedList;
import java.util.List;

public class Action {
    private int count;
    private String description = "";
    public static List<Action> allActions = new LinkedList<>();

    public Action(int count, String description) {
        this.count = count;
        this.description = description;
    }

    public int getCount() {
        return count;
    }

    public String getDescription() {
        return description;
    }

}
