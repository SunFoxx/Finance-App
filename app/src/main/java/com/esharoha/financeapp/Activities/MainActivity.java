package com.esharoha.financeapp.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.esharoha.financeapp.R;
import com.esharoha.financeapp.common.Action;
import com.esharoha.financeapp.common.Category;
import com.esharoha.financeapp.common.Serializator;

import java.util.GregorianCalendar;

import static com.esharoha.financeapp.common.Action.allActions;

public class MainActivity extends AppCompatActivity {

    private Serializator serializator;
    private EditText number;
    private EditText text;
    private LinearLayout table;
    private Button categoryButton;
    private Button dateButton;
    private InputMethodManager imm;
    private static final int REQUEST_FOR_CAT = 1;
    private static final int REQUEST_FOR_DATE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        number = (EditText) findViewById(R.id.moneySpent);
        text = (EditText) findViewById(R.id.description);
        table = (LinearLayout) findViewById(R.id.listLayout);
        categoryButton = (Button) findViewById(R.id.SelectCategory);
        dateButton = (Button) findViewById(R.id.SelectDate);
        serializator = new Serializator(getApplicationContext(), this);

        serializator.loadData();
        fillTable();

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        serializator.loadData();
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
     * @param data - data from other Activity
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
        ListItemGen generator = new ListItemGen(this, table, allActions);
        generator.fillTable();

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
        serializator.saveData();
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
