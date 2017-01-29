package com.esharoha.financeapp.Activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.esharoha.financeapp.R;
import com.esharoha.financeapp.common.Category;

public class CategorySelection extends AppCompatActivity {

    private TableLayout layout;
    public final static String ANSWER_KEY = "category";

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent answerIntent = new Intent();

            String res = ((Button) v).getText().toString();
            answerIntent.putExtra(ANSWER_KEY, res);
            setResult(RESULT_OK, answerIntent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selection);

        layout = (TableLayout) findViewById(R.id.tableWithButtons);
        int col = 1;
        TableRow nextRow = new TableRow(this);
        nextRow.setLayoutParams(new TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        for (Category cat : Category.categories) {
            Button btn = new Button(this);
            btn.setText(cat.getName());
            btn.setOnClickListener(listener);

            nextRow.addView(btn);
            if (col % 3 == 0 || col == Category.categories.size()) {
                layout.addView(nextRow);
                nextRow = new TableRow(this);
                nextRow.setLayoutParams(new TableRow.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            col += 1;

        }


    }
}
