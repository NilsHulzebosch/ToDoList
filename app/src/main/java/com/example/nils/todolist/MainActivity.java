package com.example.nils.todolist;

import android.app.Service;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<ToDoListItem> toDoListItems;
    private MyAdapter toDoListAdapter;
    private ListView toDoListView;
    private String currentUserInput;
    private EditText userInputET;
    private DBhelper dBhelper;
    private int changingItemID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeParams();
        initializeAdapter();
        updateAdapter(); // update UI
    }

    public void initializeParams() {
        toDoListItems = new ArrayList<>();
        userInputET = (EditText) findViewById(R.id.inputEditText);
        dBhelper = new DBhelper(this);
    }

    @Override
    // this method saves the state of the program
    public void onSaveInstanceState(Bundle outState) {
        currentUserInput = userInputET.getText().toString();
        outState.putString("currentUserInput", currentUserInput);
        super.onSaveInstanceState(outState);
    }

    public void initializeAdapter() {
        toDoListView = (ListView) findViewById(R.id.toDoListView);
        toDoListAdapter = new MyAdapter(this, toDoListItems);
        toDoListView.setAdapter(toDoListAdapter);

        // on short click
        toDoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getID(i);
                ToDoListItem toDoListItem = (ToDoListItem) toDoListView.getItemAtPosition(i);
                toDoListItem.changeState();
                dBhelper.update(changingItemID, toDoListItem);
                updateAdapter(); // update UI
            }
        });

        // on long click
        toDoListView.setLongClickable(true);
        toDoListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                ToDoListItem toDoListItem = (ToDoListItem) toDoListView.getItemAtPosition(pos);
                int item_id = toDoListItem.id;
                dBhelper.delete(item_id);
                updateAdapter(); // update UI
                Toast.makeText(arg1.getContext(), "Removed from to-do list",
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    public void updateAdapter() {
        toDoListItems.clear(); // clear to make room for new database
        toDoListItems.addAll(dBhelper.read()); // add database
        toDoListAdapter.notifyDataSetChanged();
    }

    // get the correct id matching the ToDoListItem
    public void getID(int pos) {
        ToDoListItem item = toDoListItems.get(pos);
        changingItemID =  item.id;
    }


    /* This method is called whenever a user short-clicks on a ToDoListItem.
     * They can edit the item (the string value) and save it.
     * The value is then updated in the database.
     */
    /*
    public void changeItem(String toDoListItem) {
        changingItem = true; // to know the user is changing a value

        // show keyboard
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.showSoftInput(userInputET, 0);

        // set focus and selection at EditText
        userInputET.setText(toDoListItem);
        userInputET.setSelection(userInputET.getText().length());
        userInputET.requestFocus();

        // give feedback
        Toast toast = Toast.makeText(this,
                "You are now editing \"" + toDoListItem + "\"", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 250);
        toast.show();
    }
    */

    // delete ToDoListItem from the database
    public void deleteItem(int id) {
        dBhelper.delete(id);
        updateAdapter(); // update UI
        Toast.makeText(this, "Removed from to-do list", Toast.LENGTH_SHORT).show();
    }

    // when pressing the save button, this method saves the user input to the database
    public void saveItem(View view) {
        currentUserInput = userInputET.getText().toString(); // turn user input into a string

        // check whether there is an input
        if (!(currentUserInput.length() == 0)) {
            ToDoListItem toDoListItem = new ToDoListItem(currentUserInput);
            dBhelper.create(toDoListItem);
            Toast.makeText(this, "Added to to-do list", Toast.LENGTH_SHORT).show();

            userInputET.setText("");
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Service.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(userInputET.getWindowToken(), 0);

            updateAdapter(); // update UI
        } else {
            // notify to type something before saving
            Toast.makeText(this, "Enter something you need to do", Toast.LENGTH_SHORT).show();
        }
    }
}
