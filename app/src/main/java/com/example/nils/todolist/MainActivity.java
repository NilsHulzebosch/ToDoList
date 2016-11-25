package com.example.nils.todolist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> toDoList;
    private ArrayAdapter<String> currentToDoList;
    private String currentUserInput;
    private EditText userInputET;
    private DBhelper dBhelper;
    private boolean changingItem;
    private int changingItemID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeParams();

        // restore savedInstanceState
        if (savedInstanceState != null) {
            currentUserInput = savedInstanceState.getString("currentUserInput");
            toDoList = savedInstanceState.getStringArrayList("toDoList");
            userInputET.setText(currentUserInput);
        }

        showToDoList(); // update UI
    }

    public void initializeParams() {
        toDoList = new ArrayList<>();
        userInputET = (EditText) findViewById(R.id.inputEditText);
        dBhelper = new DBhelper(this);
        changingItem = false;
    }

    @Override
    // this method saves the state of the program
    public void onSaveInstanceState(Bundle outState) {
        currentUserInput = userInputET.getText().toString();
        outState.putString("currentUserInput", currentUserInput);
        outState.putStringArrayList("toDoList", toDoList);
        super.onSaveInstanceState(outState);
    }

    public void showToDoList() {
        changingItem = false; // user is not changing an item (yet)
        toDoList.clear(); // clear previous ArrayList to make room for new database

        // make ArrayList and loop through to get String values (not the ids)
        final ArrayList<HashMap<String, String>> list = dBhelper.read();
        for (int i = 0; i < list.size(); i++) {
            HashMap item = list.get(i);
            String toDo = item.get("toDo").toString();
            toDoList.add(toDo); // add to ArrayList used for ArrayAdapter
        }

        final ListView toDoListView = (ListView) findViewById(R.id.toDoListView);
        currentToDoList = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, toDoList);
        toDoListView.setAdapter(currentToDoList);
        runOnUiThread(new Runnable() {
            public void run() {
                currentToDoList.notifyDataSetChanged();
            }
        });

        // on short click
        toDoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getID(list, i);
                String toDoListItem = toDoListView.getItemAtPosition(i).toString();
                changeItem(toDoListItem);
            }
        });

        // on long click
        toDoListView.setLongClickable(true);
        toDoListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                getID(list, pos);
                deleteItem(changingItemID);
                return true;
            }
        });
    }

    // get the correct id matching the String value
    public void getID(ArrayList<HashMap<String, String>> list, int pos) {
        HashMap item = list.get(pos);
        String ID = item.get("id").toString();
        changingItemID =  Integer.parseInt(ID);
    }


    /* This method is called whenever a user short-clicks on a to-do list item.
     * They can edit the item and save it. The value is updated in the database.
     */
    public void changeItem(String toDoListItem) {
        changingItem = true; // to know the user is changing a value

        userInputET.setText(toDoListItem);
        userInputET.setSelection(userInputET.getText().length());
        userInputET.requestFocus();

        Toast toast = Toast.makeText(this,
                "You are now editing \"" + toDoListItem + "\"", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 250);
        toast.show();
    }

    public void deleteItem(int pos) {
        dBhelper.delete(pos);
        showToDoList(); // update UI
        Toast.makeText(this, "Removed from to-do list", Toast.LENGTH_SHORT).show();
    }

    // when pressing the save button, this method saves the user input to the database
    public void saveDoable(View view) {
        currentUserInput = userInputET.getText().toString(); // turn user input into a string

        // check whether there is an input
        if (!(currentUserInput.length() == 0)) {
            if (changingItem) {
                // the user is changing/updating an item, not creating it
                dBhelper.update(changingItemID, currentUserInput);
                Toast.makeText(this, "Successfully changed item", Toast.LENGTH_SHORT).show();
            } else {
                // the user is creating an item, save to database
                dBhelper.create(currentUserInput);
                Toast.makeText(this, "Added to to-do list", Toast.LENGTH_SHORT).show();
            }
            userInputET.setText("");
            showToDoList(); // update UI

        } else {
            // notify to type something before saving
            Toast.makeText(this, "Enter something you need to do", Toast.LENGTH_SHORT).show();
        }
    }
}
