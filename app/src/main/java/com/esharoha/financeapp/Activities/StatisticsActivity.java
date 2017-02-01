package com.esharoha.financeapp.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.esharoha.financeapp.R;
import com.esharoha.financeapp.common.Action;
import com.esharoha.financeapp.common.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static com.esharoha.financeapp.common.Action.allActions;
import static com.esharoha.financeapp.common.Category.categories;

public class StatisticsActivity extends AppCompatActivity {

    private Spinner spinnerYear;
    private Spinner spinnerMonth;
    private LinearLayout table;
    private TextView totalField;

    private ArrayList<String> yearList;
    private ArrayList<String> monthList;
    private Map<LinearLayout, Category> categoryMap;

    private LinkedList<Action> filteredActions;

    private AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            clearTable();
            filterActions();
            fillTable();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        spinnerYear = (Spinner) findViewById(R.id.spinnerYear);
        spinnerMonth = (Spinner) findViewById(R.id.spinnerMonth);
        table = (LinearLayout) findViewById(R.id.statisticsListLayout);
        totalField = (TextView) findViewById(R.id.textTotal);

        yearList = new ArrayList<>();
        monthList = new ArrayList<>();
        categoryMap = new HashMap<>();
        filteredActions = allActions;

        fillLists();
        fillSpinners();

        filterActions();
        fillTable();
    }

    private void filterActions() {
        filteredActions = new LinkedList<Action>();
        String selectedYear = spinnerYear.getSelectedItem().toString();
        String selectedMonth = spinnerMonth.getSelectedItem().toString();

        for (Action act: allActions) {

            String actionMonth = Integer.toString(1 + act.getDate().get(GregorianCalendar.MONTH));
            if (actionMonth.length() == 1)
                actionMonth = "0" + actionMonth;
            String actionYear = Integer.toString(act.getDate().get(GregorianCalendar.YEAR));

            //All years selected
            if (selectedYear.equals(getString(R.string.all_years_spinner))) {
                //All years & all months
                if (selectedMonth.equals(getString(R.string.all_months_spinner))) {
                    filteredActions.add(act);
                }
                //All years & specific month
                else {
                    if (actionMonth.equals(selectedMonth))
                        filteredActions.add(act);
                }
            }
            //Specific year selected
            else {
                //Specific year & all months
                if (selectedMonth.equals(getString(R.string.all_months_spinner))) {
                    if (actionYear.equals(selectedYear))
                        filteredActions.add(act);
                }
                //Specific year & specific month
                else {
                    if (selectedMonth.equals(actionMonth) && selectedYear.equals(actionYear))
                        filteredActions.add(act);
                }
            }
        }
    }

    private void fillLists() {

        for (Action act: allActions) {
            String year = Integer.toString(act.getDate().get(GregorianCalendar.YEAR));
            if (!yearList.contains(year)) {
                yearList.add(year);
            }
        }
        Collections.sort(yearList);
        yearList.add(0, getString(R.string.all_years_spinner));

        for (int i = 1; i < 13; i++) {
            String month = Integer.toString(i);
            if (month.length() == 1) {
                month = "0" + month;
            }
            monthList.add(month);
        }
        monthList.add(0, getString(R.string.all_months_spinner));

    }

    private void fillSpinners() {
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, yearList);
        spinnerYear.setAdapter(yearAdapter);
        spinnerYear.setOnItemSelectedListener(spinnerListener);
        String currentYear = Integer.toString(new GregorianCalendar().get(GregorianCalendar.YEAR));
        if (yearList.contains(currentYear))
            spinnerYear.setSelection(yearAdapter.getPosition(currentYear));

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, monthList);
        spinnerMonth.setAdapter(monthAdapter);
        spinnerMonth.setOnItemSelectedListener(spinnerListener);
        String currentMonth = Integer.toString(new GregorianCalendar().get(GregorianCalendar.MONTH) + 1);
        if (currentMonth.length() == 1)
            currentMonth = "0" + currentMonth;
        spinnerMonth.setSelection(monthAdapter.getPosition(currentMonth));
    }

    private void fillTable() {
        //Searching for used categories inside filtered list
        ArrayList<Category> usedCategories = new ArrayList<>();
        for (Action act : filteredActions) {
            Category currentCategory = act.getCategory();
            if (!usedCategories.contains(currentCategory)) {
                usedCategories.add(currentCategory);
            }
        }

        Map<Category, Integer> categoryCounts = new HashMap<>();
        int total = 0;

        //Counting total cash in each selected category
        for (Category cat : usedCategories) {
            int count = 0;
            for (Action act : filteredActions) {
                if (cat.equals(act.getCategory())) {
                    count += act.getCount();
                }
            }
            categoryCounts.put(cat, count);
            total += count;
        }

        totalField.setText(Integer.toString(total));

        //Creating table elements
        for (Category cat : usedCategories) {
            TextView categoryField = new TextView(this);
            categoryField.setText(cat.getName());
            categoryField.setTextAppearance(this, R.style.listDescription);
            categoryField.setPadding(15, 0, 0, 0);
            categoryField.setGravity(Gravity.CENTER_VERTICAL);
            categoryField.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
            ));

            TextView countField = new TextView(this);
            countField.setText(Integer.toString(categoryCounts.get(cat)));
            countField.setTextAppearance(this, R.style.listCount);
            countField.setGravity(Gravity.END);
            countField.setPadding(0, 0, 15, 0);
            countField.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
            ));

            LinearLayout categoryBox = new LinearLayout(this);
            categoryBox.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams categoryBoxParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            categoryBoxParams.setMargins(0, 10, 0, 0);
            categoryBox.setLayoutParams(categoryBoxParams);
            categoryBox.setBackground(getResources().getDrawable(R.drawable.list_background));
            //categoryBox.setOnClickListener(categoryItemListener);

            categoryBox.addView(categoryField);
            categoryBox.addView(countField);

            table.addView(categoryBox);
            categoryMap.put(categoryBox, cat);
        }

    }

    private void clearTable() {
        table.removeAllViews();
        totalField.setText("0");
    }
}
