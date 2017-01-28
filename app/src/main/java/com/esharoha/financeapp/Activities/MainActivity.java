package com.esharoha.financeapp.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esharoha.financeapp.R;
import com.esharoha.financeapp.common.Action;
import com.esharoha.financeapp.common.Category;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;

import static com.esharoha.financeapp.common.Action.allActions;

public class MainActivity extends AppCompatActivity {

    private final static String SAVE_FILE = "content";
    private HashMap<LinearLayout, Action> actionMap;
    private EditText number;
    private EditText text;
    private LinearLayout table;
    private TextView sumField;
    private TextView categoryText;
    private InputMethodManager imm;
    private Category category;
    private static final int REQUEST_FOR_CAT = 1;

    private int sum = 0;

    private View.OnClickListener actionItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
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
        sumField = (TextView) findViewById(R.id.Summ);
        categoryText = (TextView) findViewById(R.id.textCategory);

        fillTable();

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    public void onSubmitClick(View view) {
        String countFromField = number.getText().toString().trim();
        String descriptionFromField = text.getText().toString().trim();
        String categoryFromField = categoryText.getText().toString().trim();

        //Check if fields are not filled
        if (countFromField.isEmpty() || categoryFromField.isEmpty()) {
            Toast err = Toast.makeText(this, "Please, fill all blanks", Toast.LENGTH_SHORT);
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


        Action newAct = (descriptionFromField.isEmpty()) ? new Action(realCount, realCategory) : new Action(realCount, descriptionFromField, realCategory);
        allActions.add(newAct);

        //refreshing list
        clearTable();

        //filling list
        fillTable();

    }

    public void onSelectCategoryClick(View view) {
        Intent askForCategory = new Intent(this, CategorySelection.class);
        startActivityForResult(askForCategory, REQUEST_FOR_CAT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FOR_CAT ) {
            if (resultCode == RESULT_OK) {
                    String cat = data.getStringExtra(CategorySelection.ANSWER_KEY);
                    categoryText.setText(cat);
            }
        }
    }

    private void clearTable() {
        table.removeAllViews();
        sum = 0;
        number.setText("");
        text.setText("");
        categoryText.setText("");
    }

    private void fillTable() {
        for (Action action : allActions) {
            TextView countField = new TextView(this);
            countField.setText(Integer.toString(action.getCount()));
            countField.setTextAppearance(this, R.style.listCount);
            countField.setGravity(Gravity.RIGHT);
            countField.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
            ));

            TextView descField = null;
            if (!action.getDescription().equals("")) {
                descField = new TextView(this);
                descField.setText(action.getDescription());
                descField.setTextAppearance(this, R.style.listCategory);
                descField.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                ));
            }

            TextView categoryField = new TextView(this);
            categoryField.setText(action.getCategory().getName());
            categoryField.setTextAppearance(this, R.style.listDescription);
            categoryField.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
            ));


            View divider = new View(this);
            divider.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 2
            ));
            divider.setBackgroundColor(Color.DKGRAY);

            sum = sum + action.getCount();

            //Filling action field

            LinearLayout actionBox = new LinearLayout(this);
            actionBox.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams actionBoxParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            actionBoxParams.setMargins(0, 20, 0, 0);
            actionBox.setLayoutParams(actionBoxParams);
            actionBox.setBackground(getResources().getDrawable(R.drawable.list_background));
            actionBox.setOnClickListener(actionItemListener);

            if (descField != null) {
                actionBox.addView(descField);
            }

            LinearLayout actionSecondRow = new LinearLayout(this);
            actionSecondRow.setOrientation(LinearLayout.HORIZONTAL);
            actionSecondRow.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            actionSecondRow.addView(categoryField);
            actionSecondRow.addView(countField);

            actionBox.addView(actionSecondRow);
            //actionBox.addView(divider);

            table.addView(actionBox, 0);
            actionMap.put(actionBox, action);
        }

        String sumText = "Sum: " + Integer.toString(sum);
        sumField.setText(sumText);

        View current = this.getCurrentFocus();
        if (current != null) {
            imm.hideSoftInputFromWindow(current.getWindowToken(), 0);
        }
    }
}
