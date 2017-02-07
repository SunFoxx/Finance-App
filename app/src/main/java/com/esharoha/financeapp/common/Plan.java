package com.esharoha.financeapp.common;

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;

public class Plan implements Serializable {

    public static LinkedList<Plan> allPlans = new LinkedList<>();

    private HashMap<Category, Integer> plannedCounter;
    private HashMap<Category, Integer> actualCounter;
    private GregorianCalendar period;

    public Plan(GregorianCalendar period) {
        this.period = period;
        for (Category cat : Category.categories) {
            plannedCounter.put(cat, 0);

            int total = 0;
            for (Action act : Action.allActions) {
                if (actionMatches(act, cat)) {
                    total += act.getCount();
                }
            }
            actualCounter.put(cat, total);
        }
    }

    public void setPlannedCounter(HashMap<Category, Integer> plannedCounter) {
        this.plannedCounter = plannedCounter;
    }

    public void refresh() {
        //checking for new categories
        for (Category cat : Category.categories) {
            if (!plannedCounter.containsKey(cat)) {
                plannedCounter.put(cat, 0);
            }
            if (!actualCounter.containsKey(cat)) {
                int total = 0;
                for (Action act : Action.allActions) {
                    if (actionMatches(act, cat)) {
                        total += act.getCount();
                    }
                }
                actualCounter.put(cat, total);
            }
        }
        //removing old categories
        for (Category cat: plannedCounter.keySet()) {
            if (!Category.categories.contains(cat)) {
                plannedCounter.remove(cat);
            }
        }
        for (Category cat: actualCounter.keySet()) {
            if (!Category.categories.contains(cat)) {
                actualCounter.remove(cat);
            }
        }
    }

    /**
     * @return period in mm/yyyy format
     */
    public String getPeriod() {
        return period.get(GregorianCalendar.MONTH) + "/" + period.get(GregorianCalendar.YEAR);
    }

    /**
     * Checking if selected action matches required date and category
     * @param act - action to check
     * @param cat - required category for action
     * @return true if matches
     */
    private boolean actionMatches(Action act, Category cat) {
        if (act.getCategory().equals(cat)) {
            String planPeriod = getPeriod();
            String actionPeriod = act.getDate().get(GregorianCalendar.MONTH) + "/" + act.getDate().get(GregorianCalendar.YEAR);
            return (planPeriod.equals(actionPeriod));
        } else {
            return false;
        }
    }
}
