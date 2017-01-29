package com.esharoha.financeapp.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.esharoha.financeapp.R;

import java.util.GregorianCalendar;

public class DateSelection extends AppCompatActivity {

    public final static String ANSWER_KEY = "date";
    private DatePicker datePicker;
    private Button submitButton;
    private String rDate = "";
    private int year;
    private int month;
    private int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_selection);

        datePicker = (DatePicker) findViewById(R.id.datePicker);
        submitButton = (Button) findViewById(R.id.submitDate);

        GregorianCalendar today = new GregorianCalendar();

        year = today.get(GregorianCalendar.YEAR);
        month = today.get(GregorianCalendar.MONTH);
        day = today.get(GregorianCalendar.DAY_OF_MONTH);

        datePicker.init(year, month, day, null);

        rDate = day + "." + (month + 1) + "." + year;
    }

    public void onSubmitDateClick(View view) {
        rDate = datePicker.getDayOfMonth() + "." + (datePicker.getMonth() + 1) + "." + datePicker.getYear();

        Intent answer = new Intent();
        answer.putExtra(ANSWER_KEY, rDate);
        setResult(RESULT_OK, answer);
        finish();
    }
}
