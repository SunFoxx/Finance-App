package com.esharoha.financeapp.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esharoha.financeapp.R;
import com.esharoha.financeapp.common.Action;
import com.esharoha.financeapp.common.Category;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;

import static com.esharoha.financeapp.common.Action.allActions;
import static com.esharoha.financeapp.common.Category.categories;

public class MainActivity extends AppCompatActivity {

    public final static String SAVE_FILE = "content";
    private HashMap<LinearLayout, Action> actionMap;
    private EditText number;
    private EditText text;
    private LinearLayout table;
    private Button categoryButton;
    private Button dateButton;
    private InputMethodManager imm;
    private static final int REQUEST_FOR_CAT = 1;
    private static final int REQUEST_FOR_DATE = 2;


    private View.OnClickListener actionItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            v.setBackground(getResources().getDrawable(R.drawable.list_active));
            showPopUpMenu(v);
        }
    };

    private void showPopUpMenu(final View v) {
        PopupMenu actionMenu = new PopupMenu(this, v);
        actionMenu.inflate(R.menu.action_popup);

        actionMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.remove) {
                    Action.allActions.remove(actionMap.get((LinearLayout)v));
                    actionMap.remove(v);
                    table.removeView(v);
                    return true;
                } else {
                    return false;
                }
            }
        });

        actionMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                v.setBackground(getResources().getDrawable(R.drawable.list_background));
            }
        });

        actionMenu.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionMap = new HashMap<>();
        number = (EditText) findViewById(R.id.moneySpent);
        text = (EditText) findViewById(R.id.description);
        table = (LinearLayout) findViewById(R.id.listLayout);
        categoryButton = (Button) findViewById(R.id.SelectCategory);
        dateButton = (Button) findViewById(R.id.SelectDate);

        loadData();
        fillTable();

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
        clearTable();
        fillTable();
    }

    public void onSubmitClick(View view) {
        String countFromField = number.getText().toString().trim();
        String descriptionFromField = text.getText().toString().trim();
        String categoryFromField = categoryButton.getText().toString().trim();
        String dateFromField = dateButton.getText().toString().trim();

        //Check if fields are not filled
        if (countFromField.isEmpty() || categoryFromField.equals(getResources().getString(R.string.select_category))) {
            Toast err = Toast.makeText(this, "Please, fill at least count and category", Toast.LENGTH_SHORT);
            err.setGravity(Gravity.CENTER, 0, -250);
            err.show();
            return;
        }

        int realCount = Integer.parseInt(countFromField);

        //assigning category class for new action
        Category realCategory = new Category("Other");
        for (Category cat : Category.categories) {
            if (cat.getName().equals(categoryFromField)) {
                realCategory = cat;
                break;
            }
        }

        Action newAct;
        boolean descriptionEmpty = descriptionFromField.isEmpty();
        boolean dateEmpty = dateFromField.equals(getResources().getString(R.string.set_date));

        GregorianCalendar realDate = null;
        if (!dateEmpty) {
            String[] dateToArr = dateFromField.split("\\.");
            int yr = Integer.parseInt(dateToArr[2]);
            int mn = Integer.parseInt(dateToArr[1]) - 1;
            int dy = Integer.parseInt(dateToArr[0]);
            realDate = new GregorianCalendar(yr, mn, dy);
        }

        if (!descriptionEmpty && !dateEmpty) {
            newAct = new Action(realCount, realCategory, descriptionFromField, realDate);
        } else if (descriptionEmpty && !dateEmpty) {
            newAct = new Action(realCount, realCategory, realDate);
        } else if (!descriptionEmpty) {
            newAct = new Action(realCount, descriptionFromField, realCategory);
        } else {
            newAct = new Action(realCount, realCategory);
        }

        allActions.add(newAct);

        //refreshing list
        clearTable();
        clearFields();

        //filling list
        fillTable();

    }

    public void onSelectCategoryClick(View view) {
        Intent askForCategory = new Intent(this, CategorySelection.class);
        startActivityForResult(askForCategory, REQUEST_FOR_CAT);
    }

    /**
     * Getting date from other Activities
     * @param requestCode request ID
     * @param resultCode result from other Activity
     * @param data data from other Activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FOR_CAT ) {
            if (resultCode == RESULT_OK) {
                String cat = data.getStringExtra(CategorySelection.ANSWER_KEY);
                categoryButton.setText(cat);
            }
        } else if (requestCode == REQUEST_FOR_DATE) {
            if (resultCode == RESULT_OK) {
                String date = data.getStringExtra(DateSelection.ANSWER_KEY);
                dateButton.setText(date);
            }
        }
    }

    /**
     * Remove all items from table and fields
     */
    private void clearTable() {
        table.removeAllViews();
    }

    private void clearFields() {
        number.setText("");
        text.setText("");
        categoryButton.setText(getResources().getString(R.string.select_category));
        dateButton.setText(getResources().getString(R.string.set_date));
    }

    /**
     * Reloading our Table
     */
    private void fillTable() {
        for (Action action : allActions) {
            //generating Textviews
            TextView countField = new TextView(this);
            countField.setText(Integer.toString(action.getCount()));
            countField.setTextAppearance(this, R.style.listCount);
            countField.setGravity(Gravity.END);
            countField.setPadding(0, 0, 15, 0);
            countField.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
            ));

            TextView descField = null;
            if (!action.getDescription().equals("")) {
                descField = new TextView(this);
                descField.setText(action.getDescription());
                descField.setTextAppearance(this, R.style.listCategory);
                descField.setPadding(15, 0, 0, 0);
                descField.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
                ));
            }

            TextView categoryField = new TextView(this);
            categoryField.setText(action.getCategory().getName());
            categoryField.setTextAppearance(this, R.style.listDescription);
            categoryField.setPadding(15, 0, 0, 0);
            categoryField.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
            ));

            TextView dateField = new TextView(this);
            String dateStr = action.getDate().get(GregorianCalendar.DAY_OF_MONTH) + "." + (action.getDate().get(GregorianCalendar.MONTH) + 1);
            dateField.setText(dateStr);
            dateField.setGravity(Gravity.END);
            dateField.setPadding(0, 0, 15, 0);
            dateField.setTextAppearance(this, R.style.listDescription);
            dateField.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
            ));

            //Filling action field

            LinearLayout actionBox = new LinearLayout(this);
            actionBox.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams actionBoxParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            actionBoxParams.setMargins(0, 20, 0, 0);
            actionBox.setLayoutParams(actionBoxParams);
            actionBox.setBackground(getResources().getDrawable(R.drawable.list_background));
            actionBox.setOnClickListener(actionItemListener);

            LinearLayout actionFirstRow = new LinearLayout(this);
            actionFirstRow.setOrientation(LinearLayout.HORIZONTAL);
            actionFirstRow.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            if (descField != null) {
                actionFirstRow.addView(descField);
            }
            actionFirstRow.addView(dateField);

            LinearLayout actionSecondRow = new LinearLayout(this);
            actionSecondRow.setOrientation(LinearLayout.HORIZONTAL);
            actionSecondRow.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            actionSecondRow.addView(categoryField);
            actionSecondRow.addView(countField);

            actionBox.addView(actionFirstRow);
            actionBox.addView(actionSecondRow);

            table.addView(actionBox, 0);
            actionMap.put(actionBox, action);
        }

        View current = this.getCurrentFocus();
        if (current != null) {
            imm.hideSoftInputFromWindow(current.getWindowToken(), 0);
        }
    }

    /**
     * Part of an Activity lifecycle
     * Here we are saving our data
     */
    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }

    /**
     * Serializing data
     */
    private void saveData() {
        try {
            FileOutputStream fos = new FileOutputStream(getApplicationContext().getFilesDir() + File.separator + SAVE_FILE);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(allActions);
            out.writeObject(categories);
            out.close();
        } catch (FileNotFoundException e) {
            Toast err = Toast.makeText(this, "Save error: fnf", Toast.LENGTH_SHORT);
            err.setGravity(Gravity.CENTER, 0, -250);
            err.show();
        }
        catch (IOException e) {
            Toast err = Toast.makeText(this, "Save error: IO", Toast.LENGTH_SHORT);
            err.setGravity(Gravity.CENTER, 0, -250);
            err.show();
        }
    }
    /**
     * Deserialization
     */
    private void loadData() {
        try {
            FileInputStream fis = new FileInputStream(getApplicationContext().getFilesDir() + File.separator + SAVE_FILE);
            ObjectInputStream input = new ObjectInputStream(fis);
            Action.allActions = (LinkedList<Action>) input.readObject();
            Category.categories = (ArrayList<Category>) input.readObject();
            fis.close();
        } catch (FileNotFoundException e) {
            Toast err = Toast.makeText(this, "Load error: fnf", Toast.LENGTH_SHORT);
            err.setGravity(Gravity.CENTER, 0, -250);
            err.show();
        } catch (IOException e) {
            Toast err = Toast.makeText(this, "Load error: IO", Toast.LENGTH_SHORT);
            err.setGravity(Gravity.CENTER, 0, -250);
            err.show();
        } catch (ClassNotFoundException e) {
            Toast err = Toast.makeText(this, "Load error: cnf", Toast.LENGTH_SHORT);
            err.setGravity(Gravity.CENTER, 0, -250);
            err.show();
        }
    }

    /**
     * Setting a date for a new action
     * @param view doing nothing here
     */
    public void onSetDateClick(View view) {
        Intent askForDate = new Intent(this, DateSelection.class);
        startActivityForResult(askForDate, REQUEST_FOR_DATE);
    }

    public void onShowStatisticsClick(View view) {
        Intent openStatisticsWindow = new Intent(this, StatisticsActivity.class);
        openStatisticsWindow.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(openStatisticsWindow);
    }

    public void onShowBudgetScreenClick(View view) {
    }
}
