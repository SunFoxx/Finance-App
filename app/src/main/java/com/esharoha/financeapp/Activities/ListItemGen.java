package com.esharoha.financeapp.Activities;


import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.esharoha.financeapp.R;
import com.esharoha.financeapp.common.Action;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;

public class ListItemGen {
    private HashMap<LinearLayout, Action> actionMap = new HashMap<>();
    private Context context;
    private LinearLayout parentView;
    private LinkedList<Action> allActions;

    private View.OnClickListener actionItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            v.setBackground(context.getResources().getDrawable(R.drawable.list_active));
            showPopUpMenu(v);
        }
    };

    private void showPopUpMenu(final View v) {
        PopupMenu actionMenu = new PopupMenu(context, v);
        actionMenu.inflate(R.menu.action_popup);

        actionMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.remove) {
                    if (!allActions.equals(Action.allActions)) {
                        allActions.remove(actionMap.get(v));
                    }
                    Action.allActions.remove(actionMap.get(v));
                    actionMap.remove(v);
                    parentView.removeView(v);
                    return true;
                } else {
                    return false;
                }
            }
        });

        actionMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                v.setBackground(context.getResources().getDrawable(R.drawable.list_background));
            }
        });

        actionMenu.show();
    }

    public ListItemGen(Context context, LinearLayout parentView, LinkedList<Action> allActions) {
        this.context = context;
        this.parentView = parentView;
        this.allActions = allActions;
    }

    public void fillTable() {
        for (Action action : allActions) {
            //generating Textviews
            TextView countField = new TextView(context);
            countField.setText(Integer.toString(action.getCount()));
            countField.setTextAppearance(context, R.style.listCount);
            countField.setGravity(Gravity.END);
            countField.setPadding(0, 0, 15, 0);
            countField.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
            ));

            TextView descField = null;
            if (!action.getDescription().equals("")) {
                descField = new TextView(context);
                descField.setText(action.getDescription());
                descField.setTextAppearance(context, R.style.listCategory);
                descField.setPadding(15, 0, 0, 0);
                descField.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
                ));
            }

            TextView categoryField = new TextView(context);
            categoryField.setText(action.getCategory().getName());
            categoryField.setTextAppearance(context, R.style.listDescription);
            categoryField.setPadding(15, 0, 0, 0);
            categoryField.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
            ));

            TextView dateField = new TextView(context);
            String dateStr = action.getDate().get(GregorianCalendar.DAY_OF_MONTH) + "." + (action.getDate().get(GregorianCalendar.MONTH) + 1);
            dateField.setText(dateStr);
            dateField.setGravity(Gravity.END);
            dateField.setPadding(0, 0, 15, 0);
            dateField.setTextAppearance(context, R.style.listDescription);
            dateField.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
            ));

            //Filling action field

            LinearLayout actionBox = new LinearLayout(context);
            actionBox.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams actionBoxParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            actionBoxParams.setMargins(0, 20, 0, 0);
            actionBox.setLayoutParams(actionBoxParams);
            actionBox.setBackground(context.getResources().getDrawable(R.drawable.list_background));
            actionBox.setOnClickListener(actionItemListener);

            LinearLayout actionFirstRow = new LinearLayout(context);
            actionFirstRow.setOrientation(LinearLayout.HORIZONTAL);
            actionFirstRow.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            if (descField != null) {
                actionFirstRow.addView(descField);
            }
            actionFirstRow.addView(dateField);

            LinearLayout actionSecondRow = new LinearLayout(context);
            actionSecondRow.setOrientation(LinearLayout.HORIZONTAL);
            actionSecondRow.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            actionSecondRow.addView(categoryField);
            actionSecondRow.addView(countField);

            actionBox.addView(actionFirstRow);
            actionBox.addView(actionSecondRow);

            parentView.addView(actionBox, 0);
            actionMap.put(actionBox, action);
        }

    }

    public Action getActionByLayout(ListView key) {
        return actionMap.get(key);
    }
}
