package com.esharoha.financeapp.common;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;

import static com.esharoha.financeapp.common.Action.allActions;
import static com.esharoha.financeapp.common.Category.categories;

public class Serializator {

    public final static String SAVE_FILE = "content";
    private Context appContext;
    private Context currentContext;

    public Serializator(Context appContext, Context currentContext) {
        this.appContext = appContext;
        this.currentContext = currentContext;
    }

    /**
     * Serializing data
     */
    public void saveData() {
        try {
            FileOutputStream fos = new FileOutputStream(appContext.getFilesDir() + File.separator + SAVE_FILE);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(allActions);
            out.writeObject(categories);
            out.close();
        } catch (FileNotFoundException e) {
            Toast err = Toast.makeText(currentContext, "Save error: fnf", Toast.LENGTH_SHORT);
            err.setGravity(Gravity.CENTER, 0, -250);
            err.show();
        }
        catch (IOException e) {
            Toast err = Toast.makeText(currentContext, "Save error: IO", Toast.LENGTH_SHORT);
            err.setGravity(Gravity.CENTER, 0, -250);
            err.show();
        }
    }
    /**
     * Deserialization
     */
    public void loadData() {
        try {
            FileInputStream fis = new FileInputStream(appContext.getFilesDir() + File.separator + SAVE_FILE);
            ObjectInputStream input = new ObjectInputStream(fis);
            Action.allActions = (LinkedList<Action>) input.readObject();
            Category.categories = (ArrayList<Category>) input.readObject();
            fis.close();
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            Toast err = Toast.makeText(currentContext, "Load error: IO", Toast.LENGTH_SHORT);
            err.setGravity(Gravity.CENTER, 0, -250);
            err.show();
        } catch (ClassNotFoundException e) {
            Toast err = Toast.makeText(currentContext, "Load error: cnf", Toast.LENGTH_SHORT);
            err.setGravity(Gravity.CENTER, 0, -250);
            err.show();
        }
    }
}
